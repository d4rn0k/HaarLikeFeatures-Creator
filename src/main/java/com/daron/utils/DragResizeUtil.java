package com.daron.utils;

import com.daron.haar.features.HaarFeature;
import com.daron.haar.features.TiltedRectangleHaarFeature;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import static com.daron.utils.MouseStates.getCursorForState;

public class DragResizeUtil {

    private final static int minElementSizeWidth = 15;
    private final static int minElementSizeHeight = 15;
    private final Node node;
    private final OnDragResizeEventListener listener;
    private MouseStates mouseState = MouseStates.DEFAULT;
    private double initialClickX, initialClickY, nodeX, nodeY, nodeH, nodeW;
    private int maxParentSizeForDragW;
    private int maxParentSizeForDragH;
    private int marginDragW = 0;
    private int marginDragH = 0;
    private boolean isNodeRotated = false;
    private boolean isOnlyDraggable = false;

    public DragResizeUtil(Node currentNode, int maxParentSizeForDragW, int maxParentSizeForDragH) {
        this.maxParentSizeForDragW = maxParentSizeForDragW;
        this.maxParentSizeForDragH = maxParentSizeForDragH;

        this.node = currentNode;
        listener = new OnDragResizeEventListener() {
            @Override
            public void onDrag(Event event, Node node, double newX, double newY, double newWidth, double newHeight) {

                if (node instanceof HaarFeature) {
                    ((HaarFeature) currentNode).setNewPosition((int) Math.round(newX), (int) Math.round(newY));
                } else {
                    node.setLayoutX(newX);
                    node.setLayoutY(newY);
                }
            }

            @Override
            public void onResize(Event event, Node currentNode, double newX, double newY, double newWidth, double newHeight) {

                if (currentNode instanceof HaarFeature) {

                    HaarFeature myHaarFeature = (HaarFeature) currentNode;

                    myHaarFeature.setNewPosition(
                            (int) Math.round(newX),
                            (int) Math.round(newY));

                    myHaarFeature.setNewSize(
                            (int) Math.round(newWidth),
                            (int) Math.round(newHeight));

                }
            }
        };

    }

    public void makeOnlyDraggable() {
        node.setOnMousePressed(this::mousePressed);
        node.setOnMouseDragged(this::mouseDragged);
        isOnlyDraggable = true;
    }

    public void makeDraggableAndResizable() {
        // Dragable
        node.setOnMousePressed(this::mousePressed);
        node.setOnMouseDragged(this::mouseDragged);

        // Resizable
        node.setOnMouseMoved(this::mouseOver);
        node.setOnMouseReleased(this::mouseReleased);

        // Double click causes rotation by 45 degrees
        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (isNodeRotated)) {
                TiltedRectangleHaarFeature rotatedRectangle = (TiltedRectangleHaarFeature) event.getSource();
                rotatedRectangle.rotate45();
            }
        });
    }

    public void setNodeRotated(boolean nodeRotated) {
        isNodeRotated = nodeRotated;
    }

    public void setMarginsForDrag(int width, int height) {
        marginDragW = width;
        marginDragH = height;
    }

    private void mouseOver(MouseEvent event) {
        node.setCursor(getCursorForState(currentMouseState(event)));
    }

    private void mouseReleased(MouseEvent event) {
        node.setCursor(Cursor.DEFAULT);
        mouseState = MouseStates.DEFAULT;
    }

    private void mousePressed(MouseEvent event) {

        setNewInitialEventCoordinates(event);
        if (isInResizeZone(event)) {
            mouseState = currentMouseState(event);
        } else {
            mouseState = MouseStates.DRAG;
        }
    }

    private void mouseDragged(MouseEvent event) {

        if (listener == null) {
            return;
        }

        double mouseX = parentX(event.getX());
        double mouseY = parentY(event.getY());

        if (isOnlyDraggable || mouseState == MouseStates.DRAG) {

            double newX, newY;

            if (isNodeRotated) {
                TiltedRectangleHaarFeature hp = (TiltedRectangleHaarFeature) node;

                int oldX = hp.getStartPointX();
                int oldY = hp.getStartPointY();
                int deltaX = (int) (event.getX() - initialClickX);
                int deltaY = (int) (event.getY() - initialClickY);

                newX = oldX + deltaX;
                newY = oldY + deltaY;

                newX = (newX < hp.getHeightInteger()) ? hp.getHeightInteger() : newX;

                if (newX + hp.getWidthInteger() > DragResizeUtil.this.maxParentSizeForDragW) {
                    newX = DragResizeUtil.this.maxParentSizeForDragW - hp.getWidthInteger();
                }

                newY = (newY < 0) ? 0 : newY;

                if (newY + hp.getWidthInteger() + hp.getHeightInteger() > DragResizeUtil.this.maxParentSizeForDragH) {
                    newY = DragResizeUtil.this.maxParentSizeForDragH - hp.getHeightInteger() - hp.getWidthInteger();
                }

                setNewInitialEventCoordinates(event);

            } else {
                newX = mouseX - initialClickX;
                newY = mouseY - initialClickY;

                if (newX > DragResizeUtil.this.maxParentSizeForDragW + marginDragW - nodeW) {
                    newX = DragResizeUtil.this.maxParentSizeForDragW + marginDragW - nodeW;
                }
                if (newY > DragResizeUtil.this.maxParentSizeForDragH + marginDragH - nodeH) {
                    newY = DragResizeUtil.this.maxParentSizeForDragH + marginDragH - nodeH;
                }

                newX = (newX < -marginDragW) ? -marginDragW : newX;
                newY = (newY < -marginDragH) ? -marginDragH : newY;
            }

            listener.onDrag(event, node, newX, newY, nodeW, nodeH);

        } else if (mouseState.isResizeState()) {


            if (isNodeRotated) {
                resizeTiltedHaarFeature(node, mouseState, event);
            } else {
                resizeHaarFeature(node, mouseState, event);
            }

        }
    }

    private void resizeTiltedHaarFeature(Node node, MouseStates mouseState, MouseEvent event) {
        HaarFeature rotatedRectangle = (HaarFeature) event.getSource();

        double newX = nodeX;
        double newY = nodeY;

        double newWidth = nodeW;
        double newHeight = nodeH;

        final int oldX = rotatedRectangle.getStartPointX();
        final int oldY = rotatedRectangle.getStartPointY();
        final int oldWidth = rotatedRectangle.getWidthInteger();
        final int oldHeight = rotatedRectangle.getHeightInteger();


        int deltaX = (int) (event.getX() - initialClickX);
        int deltaY = (int) (event.getY() - initialClickY);

        if (mouseState == MouseStates.NW_RESIZE) {
            newWidth = oldWidth - deltaX;
            newX = oldX + deltaX;
            newY = oldY + deltaX;

            if (newY < 0) {
                newY = 0;
                newX = oldX;
                newWidth = oldWidth;
            }

            if (newX < rotatedRectangle.getHeightInteger()) {
                newX = rotatedRectangle.getHeightInteger();
                newY = oldY;
                newWidth = oldWidth;
            }


        } else if (mouseState == MouseStates.SE_RESIZE) {
            newWidth = oldWidth + deltaX;

            // Width bound
            if (newX + newWidth > maxParentSizeForDragW) {
                newWidth = maxParentSizeForDragW - newX;
            }

            // Height bound
            if (newY + newWidth + newHeight > maxParentSizeForDragH) {
                newWidth = maxParentSizeForDragH - newY - newHeight;
            }

        } else if (mouseState == MouseStates.NE_RESIZE) {
            newHeight = oldHeight - deltaY;
            newX = oldX - deltaY;
            newY = oldY + deltaY;


            if (newY < 0) {
                newY = 0;
                newX = oldX;
                newHeight = oldHeight;
            }

            if (newX + newWidth > maxParentSizeForDragW) {
                newY = oldY;
                newX = oldX;
                newHeight = oldHeight;
            }


        } else if (mouseState == MouseStates.SW_RESIZE) {
            newHeight = oldHeight + deltaY;

            if (newHeight > oldX) {
                newHeight = oldHeight;
            }

            if (oldY + newWidth + newHeight > maxParentSizeForDragH) {
                newHeight = maxParentSizeForDragH - oldY - newWidth;
            }

        }

        if (newWidth < minElementSizeWidth) {
            if (mouseState.isWestOrSubWestState()) {
                newX = newX - minElementSizeWidth + newWidth;
            }
            newWidth = minElementSizeWidth;
        }

        if (newHeight < minElementSizeHeight) {
            if (mouseState.isNorthOrSubNorthState()) {
                newY = newY + newHeight - minElementSizeHeight;
            }
            newHeight = minElementSizeHeight;
        }


        newX = (newX < rotatedRectangle.getHeightInteger()) ? rotatedRectangle.getHeightInteger() : newX;
        newY = (newY < 0) ? 0 : newY;

        setNewInitialEventCoordinates(event);
        listener.onResize(event, node, newX, newY, newWidth, newHeight);
    }

    private void resizeHaarFeature(Node node, MouseStates mouseState, MouseEvent event) {

        double newX = nodeX;
        double newY = nodeY;

        double newWidth = nodeW;
        double newHeight = nodeH;

        double mouseX = parentX(event.getX());
        double mouseY = parentY(event.getY());

        if (mouseState.isEastOrSubEastState()) {
            newWidth = mouseX - nodeX;
        }

        if (mouseState.isWestOrSubWestState()) {
            newX = mouseX;

            if (newX > 0) {
                newWidth = nodeW + nodeX - newX;
            } else {
                newWidth = nodeW + nodeX;
            }
        }

        if (mouseState.isSouthOrSubSouthState()) {
            newHeight = mouseY - nodeY;
        }

        if (mouseState.isNorthOrSubNorthState()) {
            newY = mouseY;

            if (newY > 0) {
                newHeight = nodeH + nodeY - newY;
            } else {
                newHeight = nodeH + nodeY;
            }
        }

        //min valid rect size check
        if (newWidth < minElementSizeWidth) {

            if (mouseState.isWestOrSubWestState()) {
                newX = newX + newWidth - minElementSizeWidth;
            }
            newWidth = minElementSizeWidth;
        }

        if (newHeight < minElementSizeHeight) {

            if (mouseState.isNorthOrSubNorthState()) {
                newY = newY + newHeight - minElementSizeHeight;
            }
            newHeight = minElementSizeHeight;
        }

        newX = (newX < 0) ? 0 : newX;
        newY = (newY < 0) ? 0 : newY;

        newWidth = (newWidth > maxParentSizeForDragW - newX) ? maxParentSizeForDragW - newX : newWidth;
        newHeight = (newHeight > maxParentSizeForDragH - newY) ? maxParentSizeForDragH - newY : newHeight;

        listener.onResize(event, node, newX, newY, newWidth, newHeight);
    }

    private void setNewInitialEventCoordinates(MouseEvent event) {
        nodeX = nodeX();
        nodeY = nodeY();
        nodeH = nodeH();
        nodeW = nodeW();

        initialClickX = event.getX();
        initialClickY = event.getY();
    }

    private boolean isInResizeZone(MouseEvent event) {
        return isLeftResizeZone(event) ||
                isRightResizeZone(event) ||
                isBottomResizeZone(event) ||
                isTopResizeZone(event);
    }

    private boolean isLeftResizeZone(MouseEvent event) {
        if (isNodeRotated) {
            TiltedRectangleHaarFeature target = ((TiltedRectangleHaarFeature) event.getTarget());

            double diff = event.getY() - target.getBoundsInParent().getMinY();
            double targetX = target.getStartPointX();

            return intersect(targetX - diff, event.getX());
        } else {
            return intersect(0, event.getX());
        }
    }

    private boolean isRightResizeZone(MouseEvent event) {
        if (isNodeRotated) {
            TiltedRectangleHaarFeature target = ((TiltedRectangleHaarFeature) event.getTarget());

            double targetX = target.getBoundsPoints().c.getX();
            double diffY = target.getBoundsPoints().c.getY() - event.getY();

            return intersect(targetX + diffY, event.getX());

        } else {
            return intersect(nodeW(), event.getX());
        }
    }

    private boolean isTopResizeZone(MouseEvent event) {
        if (isNodeRotated) {

            TiltedRectangleHaarFeature target = ((TiltedRectangleHaarFeature) event.getTarget());

            double minY = target.getBoundsPoints().a.getY();
            double diffY = event.getY() - minY;
            double targetX = target.getStartPointX();

            return intersect(targetX + diffY, event.getX());
        } else {
            return intersect(0, event.getY());
        }
    }

    private boolean isBottomResizeZone(MouseEvent event) {
        if (isNodeRotated) {

            TiltedRectangleHaarFeature target = ((TiltedRectangleHaarFeature) event.getTarget());

            double minX = target.getBoundsInParent().getMinX();
            double diff = event.getX() - minX;
            double targetY = target.getStartPointY() + target.getHeightInteger();

            return intersect(targetY + diff, event.getY());
        } else {
            return intersect(nodeH(), event.getY());
        }
    }

    private boolean intersect(double side, double point) {
        int resizeEventAreaMargin = 5;
        return side + resizeEventAreaMargin > point && side - resizeEventAreaMargin < point;
    }

    private MouseStates currentMouseState(MouseEvent event) {
        MouseStates state;

        boolean left = isLeftResizeZone(event);
        boolean right = isRightResizeZone(event);
        boolean top = isTopResizeZone(event);
        boolean bottom = isBottomResizeZone(event);

        if (isNodeRotated) {

            if (left) {
                state = MouseStates.NW_RESIZE;
            } else if (right) {
                state = MouseStates.SE_RESIZE;
            } else if (top) {
                state = MouseStates.NE_RESIZE;
            } else if (bottom) {
                state = MouseStates.SW_RESIZE;
            } else {
                state = MouseStates.DRAG;
            }

            return state;
        }

        if (left && top) {
            state = MouseStates.NW_RESIZE;
        } else if (left && bottom) {
            state = MouseStates.SW_RESIZE;
        } else if (right && top) {
            state = MouseStates.NE_RESIZE;
        } else if (right && bottom) {
            state = MouseStates.SE_RESIZE;
        } else if (right) {
            state = MouseStates.E_RESIZE;
        } else if (left) {
            state = MouseStates.W_RESIZE;
        } else if (top) {
            state = MouseStates.N_RESIZE;
        } else if (bottom) {
            state = MouseStates.S_RESIZE;
        } else {
            state = MouseStates.DRAG;
        }

        return state;
    }

    private double parentX(double localX) {
        if (isNodeRotated) {
            return localX;
        }

        return nodeX() + localX;
    }

    private double parentY(double localY) {
        if (isNodeRotated) {
            return localY;
        }
        return nodeY() + localY;
    }

    private double nodeX() {
        if (isNodeRotated) {
            TiltedRectangleHaarFeature haarPolygon = (TiltedRectangleHaarFeature) this.node;
            return haarPolygon.getBoundsPoints().a.getX();
        }

        return node.getBoundsInParent().getMinX();
    }

    private double nodeY() {
        return node.getBoundsInParent().getMinY();
    }

    private double nodeW() {
        if (isNodeRotated) {
            return ((TiltedRectangleHaarFeature) node).getWidthInteger();
        }

        return node.getBoundsInParent().getWidth();
    }

    private double nodeH() {
        if (isNodeRotated) {
            return ((TiltedRectangleHaarFeature) node).getHeightInteger();
        }

        return node.getBoundsInParent().getHeight();
    }

    private interface OnDragResizeEventListener {
        void onDrag(Event event, Node node, double x, double y, double w, double h);

        void onResize(Event event, Node node, double x, double y, double w, double h);
    }

}