package com.daron;

import com.daron.haar.features.HaarFeature;
import com.daron.haar.features.IHaar;
import com.daron.haar.features.TiltedHaarPolygon;
import com.daron.utils.MyBounds;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

class DragResizeMod {

    private final static double minElementSizeWidth = 15;
    private final static double minElementSizeHeight = 15;
    private static MainWindowController mainWindowController;
    private final int ResizeEventAreaMargin = 5;
    private double initClickX, initClickY, nodeX, nodeY, nodeH, nodeW;
    private double maxDragOrResizeParentW;
    private double maxDragOrResizeParentH;
    private double marginForDragW = 0;
    private double marginForDragH = 0;
    private boolean isNodeRotated = false;
    private MOUSESTATES state = MOUSESTATES.DEFAULT;
    private Node node;
    private boolean onlyDraggable = false;
    private IonDragResizeEventListener listener;

    public DragResizeMod(Node node, double maxDragOrResizeParentW, double maxDragOrResizeParentH) {
        this.maxDragOrResizeParentW = maxDragOrResizeParentW;
        this.maxDragOrResizeParentH = maxDragOrResizeParentH;

        this.node = node;
        listener = new IonDragResizeEventListener() {
            @Override
            public void onDrag(Event event, Node node1, double newX, double newY, double newWidth, double newHeight) {
                //            Bounds boundsInParent = node.getParent().getBoundsInParent();
                //            double width =  boundsInParent.getMaxX() - boundsInParent.getMinX();
                //            double height = boundsInParent.getMaxY() - boundsInParent.getMinY();

                if (isNodeRotated) {
                    TiltedHaarPolygon hp = (TiltedHaarPolygon) node1;

                    if (newX < hp.getIntegerHeight()) {
                        newX = hp.getIntegerHeight();
                    }

                    if (newX + hp.getIntegerWidth() > DragResizeMod.this.maxDragOrResizeParentW) {
                        newX = DragResizeMod.this.maxDragOrResizeParentW - hp.getIntegerWidth();
                    }

                    if (newY < 0) {
                        newY = 0;
                    }

                    if (newY + hp.getIntegerWidth() + hp.getIntegerHeight() > DragResizeMod.this.maxDragOrResizeParentH) {
                        newY = DragResizeMod.this.maxDragOrResizeParentH - hp.getIntegerHeight() - hp.getIntegerWidth();
                    }

                    newHeight = -1; // Not change
                    newWidth = -1;  // Not change


                } else {
                    if (newX > DragResizeMod.this.maxDragOrResizeParentW + marginForDragW - newWidth) {
                        newX = DragResizeMod.this.maxDragOrResizeParentW + marginForDragW - newWidth;
                    }
                    if (newY > DragResizeMod.this.maxDragOrResizeParentH + marginForDragH - newHeight) {
                        newY = DragResizeMod.this.maxDragOrResizeParentH + marginForDragH - newHeight;
                    }
                    if (newX < -marginForDragW) {
                        newX = -marginForDragW;
                    }
                    if (newY < -marginForDragH) {
                        newY = -marginForDragH;
                    }
                }


                if (node1 instanceof IHaar) {

                    ((IHaar) node1).setNewSizeAndPosition(
                            (int) Math.round(newX),
                            (int) Math.round(newY),
                            (int) Math.round(newWidth),
                            (int) Math.round(newHeight)
                    );
                } else {
                    //Drag initital point
                    setNodeSize(event, node1, newX, newY, newWidth, newHeight);
                }


            }

            @Override
            public void onResize(Event event, Node node1, double newX, double newY, double newWidth, double newHeight) {


                if (isNodeRotated) {
                    TiltedHaarPolygon hp = (TiltedHaarPolygon) node1;

                    MyBounds bp = hp.getBoundsPoints();

                    if (newX + newWidth > maxDragOrResizeParentW) {
                        newWidth = maxDragOrResizeParentW - newX;
                    }

                    if (newY + newHeight + newWidth > maxDragOrResizeParentH) {
                        newWidth = maxDragOrResizeParentH - newY - newHeight;
                    }


                    if (newX < hp.getIntegerHeight()) {
                        newX = hp.getIntegerHeight();
                    }

                    if (newY < 0) {
                        newY = 0;
                    }

                    if (newWidth < minElementSizeWidth) {
                        newWidth = minElementSizeWidth;
                    }

                    if (newHeight < minElementSizeHeight) {
                        newHeight = minElementSizeHeight;
                    }


                    System.out.format("newX=(%5.0f) newY=(%5.0f) newWidth=(%5.0f) newHeight=(%5.0f) \n",
                            newX, newY, newWidth, newHeight);

                } else {
                    if (newWidth > maxDragOrResizeParentW - newX) {
                        newWidth = maxDragOrResizeParentW - newX;
                    }
                    if (newHeight > maxDragOrResizeParentH - newY) {
                        newHeight = maxDragOrResizeParentH - newY;
                    }
                    if (newX < 0) {
                        newX = 0;
                    }
                    if (newY < 0) {
                        newY = 0;
                    }

                    if (newWidth > maxDragOrResizeParentW) {
                        newWidth = maxDragOrResizeParentH;
                        newX = 0;
                    }

                    if (newHeight > maxDragOrResizeParentH) {
                        newHeight = maxDragOrResizeParentH;
                        newY = 0;
                    }
                }


                ((IHaar) node1).setNewSizeAndPosition(
                        (int) Math.round(newX),
                        (int) Math.round(newY),
                        (int) Math.round(newWidth),
                        (int) Math.round(newHeight)
                );

            }

            private void setNodeSize(Event event, Node node1, double x, double y, double w, double h) {

                node1.setLayoutX(x);
                node1.setLayoutY(y);

                if (node1 instanceof Canvas) {
                    ((Canvas) node1).setWidth(w);
                    ((Canvas) node1).setHeight(h);
                } else if (node1 instanceof HaarFeature) {
                    HaarFeature rect = (HaarFeature) node1;

                    rect.setWidth(w);
                    rect.maxWidth(w);
                    rect.minWidth(w);

                    rect.setHeight(h);
                    rect.minHeight(h);
                    rect.maxHeight(h);

                } else if (node1 instanceof Pane) {

                    Pane pane = (Pane) node1;

                    pane.maxWidth(w);
                    pane.minWidth(w);
                    pane.setPrefWidth(w);

                    pane.setMaxHeight(h);
                    pane.setMinHeight(h);
                    pane.setPrefHeight(h);
                }
            }
        };
    }

    public static void setMainWindowController(MainWindowController mainWindowController) {
        DragResizeMod.mainWindowController = mainWindowController;
    }

    private static Cursor getCursorForState(MOUSESTATES state) {
        switch (state) {
            case NW_RESIZE:
                return Cursor.NW_RESIZE;
            case SW_RESIZE:
                return Cursor.SW_RESIZE;
            case NE_RESIZE:
                return Cursor.NE_RESIZE;
            case SE_RESIZE:
                return Cursor.SE_RESIZE;
            case E_RESIZE:
                return Cursor.E_RESIZE;
            case W_RESIZE:
                return Cursor.W_RESIZE;
            case N_RESIZE:
                return Cursor.N_RESIZE;
            case S_RESIZE:
                return Cursor.S_RESIZE;
            case DRAG:
                return Cursor.MOVE;
            default:
                return Cursor.DEFAULT;
        }
    }

    private void setNewDragOrResizeMeasures(double maxDragOrResizeParentWidth, double maxDragOrResizeParentHeight) {
        this.maxDragOrResizeParentW = maxDragOrResizeParentWidth;
        this.maxDragOrResizeParentH = maxDragOrResizeParentHeight;
    }

    public DragResizeMod makeOnlyDraggable() {
        node.setOnMousePressed(this::mousePressed);
        node.setOnMouseDragged(this::mouseDragged);
        onlyDraggable = true;

        return this;
    }

    public DragResizeMod setMarginsForDrag(double width, double height) {
        marginForDragW = width;
        marginForDragH = height;

        return this;
    }

    public DragResizeMod makeDraggableAndResizable() {

        //Dragable
        node.setOnMousePressed(this::mousePressed);
        node.setOnMouseDragged(this::mouseDragged);

        //Resizable
        node.setOnMouseMoved(this::mouseOver);
        node.setOnMouseReleased(this::mouseReleased);

        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (isNodeRotated)) {

                TiltedHaarPolygon rotatedRectangle = (TiltedHaarPolygon) event.getSource();

                rotatedRectangle.rotate45();

            }
        });

        return this;
    }

    private void mouseReleased(MouseEvent event) {
        node.setCursor(Cursor.DEFAULT);
        state = MOUSESTATES.DEFAULT;
    }

    private void mouseOver(MouseEvent event) {
        MOUSESTATES state = currentMouseState(event);
        Cursor cursor = getCursorForState(state);
        node.setCursor(cursor);
    }

    private void setNewInitialEventCoordinates(MouseEvent event) {
        nodeX = nodeX();
        nodeY = nodeY();
        nodeH = nodeH();
        nodeW = nodeW();

        initClickX = event.getX();
        initClickY = event.getY();
    }

    private void mousePressed(MouseEvent event) {

        if (event.getSource() instanceof IHaar) {
            mainWindowController.setListViewSelection((IHaar) event.getSource());
        }

        setNewInitialEventCoordinates(event);
        if (isInResizeZone(event)) {
            state = currentMouseState(event);
        } else {
            state = MOUSESTATES.DRAG;
        }
    }

    private void mouseDragged(MouseEvent event) {

        if (listener != null) {
            double mouseX = parentX(event.getX());
            double mouseY = parentY(event.getY());


            if (onlyDraggable || state == MOUSESTATES.DRAG) {


                if (isNodeRotated) {
                    IHaar rotatedRectangle = (IHaar) event.getSource();

                    int oldX = rotatedRectangle.getIntegerX();
                    int oldY = rotatedRectangle.getIntegerY();
                    int deltaX = (int) (event.getX() - initClickX);
                    int deltaY = (int) (event.getY() - initClickY);

                    listener.onDrag(event, node, oldX + deltaX, oldY + deltaY, nodeW, nodeH);
                    setNewInitialEventCoordinates(event);
                } else {
                    listener.onDrag(event, node, mouseX - initClickX, mouseY - initClickY, nodeW, nodeH);
                }

            } else if (state != MOUSESTATES.DEFAULT) {
                //resizing
                double newX = nodeX;
                double newY = nodeY;

                double newW = nodeW;
                double newH = nodeH;

                if (isNodeRotated) {
                    IHaar rotatedRectangle = (IHaar) event.getSource();

                    int oldX = rotatedRectangle.getIntegerX();
                    int oldY = rotatedRectangle.getIntegerY();
                    int oldWidth = rotatedRectangle.getIntegerWidth();
                    int oldHeight = rotatedRectangle.getIntegerHeight();


                    int deltaX = (int) (event.getX() - initClickX);
                    int deltaY = (int) (event.getY() - initClickY);

                    if (state == MOUSESTATES.NW_RESIZE) {

                        newW = oldWidth - deltaX;
                        newX = oldX + deltaX;
                        newY = oldY + deltaX;

                    } else if (state == MOUSESTATES.SE_RESIZE) {
                        newW = oldWidth + deltaX;

                    } else if (state == MOUSESTATES.NE_RESIZE) {
                        newH = oldHeight - deltaY;
                        newX = oldX - deltaY;
                        newY = oldY + deltaY;
                    } else if (state == MOUSESTATES.SW_RESIZE) {
                        newH = oldHeight + deltaY;

                    }

                    if (newW < minElementSizeWidth) {
                        if (state == MOUSESTATES.W_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.SW_RESIZE) {
                            newX = newX - minElementSizeWidth + newW;
                        }
                        newW = minElementSizeWidth;
                    }

                    if (newH < minElementSizeHeight) {
                        if (state == MOUSESTATES.N_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.NE_RESIZE) {
                            newY = newY + newH - minElementSizeHeight;
                        }
                        newH = minElementSizeHeight;
                    }

//                    System.out.println("MouseX= (" + mouseX + ") NodeX= (" + nodeX + ") MouseY= (" + mouseY +
//                            ") MouseY= " + mouseY + ")");

                    setNewInitialEventCoordinates(event);

                    listener.onResize(event, node, newX, newY, newW, newH);
                    return;
                }

                // Right Resize
                if (state == MOUSESTATES.E_RESIZE || state == MOUSESTATES.NE_RESIZE || state == MOUSESTATES.SE_RESIZE) {
                    newW = mouseX - nodeX;
                }

                // Left Resize
                if (state == MOUSESTATES.W_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.SW_RESIZE) {
                    newX = mouseX;
                    newW = nodeW + nodeX - newX;
                }

                // Bottom Resize
                if (state == MOUSESTATES.S_RESIZE || state == MOUSESTATES.SE_RESIZE || state == MOUSESTATES.SW_RESIZE) {
                    newH = mouseY - nodeY;
                }

                // Top Resize
                if (state == MOUSESTATES.N_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.NE_RESIZE) {
                    newY = mouseY;
                    newH = nodeH + nodeY - newY;
                }

                //min valid rect Size Check
                if (newW < minElementSizeWidth) {
                    if (state == MOUSESTATES.W_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.SW_RESIZE) {
                        newX = newX - minElementSizeWidth + newW;
                    }
                    newW = minElementSizeWidth;
                }

                if (newH < minElementSizeHeight) {
                    if (state == MOUSESTATES.N_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.NE_RESIZE) {
                        newY = newY + newH - minElementSizeHeight;
                    }
                    newH = minElementSizeHeight;
                }

                listener.onResize(event, node, newX, newY, newW, newH);
            }
        }
    }

    private MOUSESTATES currentMouseState(MouseEvent event) {
        MOUSESTATES state = MOUSESTATES.DEFAULT;
        boolean left = isLeftResizeZone(event);
        boolean right = isRightResizeZone(event);
        boolean top = isTopResizeZone(event);
        boolean bottom = isBottomResizeZone(event);

        if (isNodeRotated) {
            if (left) {
                state = MOUSESTATES.NW_RESIZE;
            } else if (right) {
                state = MOUSESTATES.SE_RESIZE;
            } else if (top) {
                state = MOUSESTATES.NE_RESIZE;
            } else if (bottom) {
                state = MOUSESTATES.SW_RESIZE;
            } else {
                state = MOUSESTATES.DRAG;
            }
//            System.out.println(state);
            return state;
        }

        if (left && top) {
            state = MOUSESTATES.NW_RESIZE;
        } else if (left && bottom) {
            state = MOUSESTATES.SW_RESIZE;
        } else if (right && top) {
            state = MOUSESTATES.NE_RESIZE;
        } else if (right && bottom) {
            state = MOUSESTATES.SE_RESIZE;
        } else if (right) {
            state = MOUSESTATES.E_RESIZE;
        } else if (left) {
            state = MOUSESTATES.W_RESIZE;
        } else if (top) {
            state = MOUSESTATES.N_RESIZE;
        } else if (bottom) {
            state = MOUSESTATES.S_RESIZE;
        } else {
            state = MOUSESTATES.DRAG;
        }

//        else if (isInDragZone(event)) {
//            state = MOUSESTATES.DRAG;
//        }

        return state;
    }

    private boolean isInResizeZone(MouseEvent event) {
        return isLeftResizeZone(event) || isRightResizeZone(event) || isBottomResizeZone(event) || isTopResizeZone(event);
    }

    private boolean isInDragZone(MouseEvent event) {
        double xPos = parentX(event.getX());
        double yPos = parentY(event.getY());

        double nodeX = nodeX() + ResizeEventAreaMargin;
        double nodeY = nodeY() + ResizeEventAreaMargin;
        double nodeX0 = nodeX() + nodeW() - ResizeEventAreaMargin;
        double nodeY0 = nodeY() + nodeH() - ResizeEventAreaMargin;

        return (xPos > nodeX && xPos < nodeX0) && (yPos > nodeY && yPos < nodeY0);
    }

    private boolean isLeftResizeZone(MouseEvent event) {

        if (isNodeRotated) {
            TiltedHaarPolygon target = ((TiltedHaarPolygon) event.getTarget());

            double diff = event.getY() - target.getBoundsInParent().getMinY();

            double targetX = target.getIntegerX();

            return intersect(targetX - diff, event.getX());
        } else {
            return intersect(0, event.getX());
        }
    }

    private boolean isRightResizeZone(MouseEvent event) {

        if (isNodeRotated) {
            TiltedHaarPolygon target = ((TiltedHaarPolygon) event.getTarget());

            double targetX = target.getBoundsPoints().c.getX();

            double diffY = target.getBoundsPoints().c.getY() - event.getY();


            return intersect(targetX + diffY, event.getX());

        } else {
            return intersect(nodeW(), event.getX());
        }

    }

    private boolean isTopResizeZone(MouseEvent event) {
        if (isNodeRotated) {

            TiltedHaarPolygon target = ((TiltedHaarPolygon) event.getTarget());

            double minY = target.getBoundsPoints().a.getY();

            double diffY = event.getY() - minY;

            double targetX = target.getIntegerX();

            return intersect(targetX + diffY, event.getX());
        } else {
            return intersect(0, event.getY());
        }
    }

    private boolean isBottomResizeZone(MouseEvent event) {
        if (isNodeRotated) {

            TiltedHaarPolygon target = ((TiltedHaarPolygon) event.getTarget());

            double minX = target.getBoundsInParent().getMinX();

            double diff = event.getX() - minX;

            double targetY = target.getIntegerY() + target.getIntegerHeight();

            return intersect(targetY + diff, event.getY());
        } else {
            return intersect(nodeH(), event.getY());
        }

    }

    private boolean intersect(double side, double point) {
        return side + ResizeEventAreaMargin > point && side - ResizeEventAreaMargin < point;
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
            TiltedHaarPolygon haarPolygon = (TiltedHaarPolygon) this.node;

            return haarPolygon.getBoundsPoints().a.getX();
        }

        return node.getBoundsInParent().getMinX();
    }

    private double nodeY() {
        return node.getBoundsInParent().getMinY();
    }

    private double nodeW() {
        if (isNodeRotated) {
            return ((TiltedHaarPolygon) node).getIntegerWidth();
        }

        return node.getBoundsInParent().getWidth();
    }

    private double nodeH() {
        if (isNodeRotated) {
            return ((TiltedHaarPolygon) node).getIntegerHeight();
        }

        return node.getBoundsInParent().getHeight();
    }

    public DragResizeMod setNodeRotated(boolean nodeRotated) {
        isNodeRotated = nodeRotated;

        return this;
    }

    private enum MOUSESTATES {
        DEFAULT,
        DRAG,
        NW_RESIZE,
        SW_RESIZE,
        NE_RESIZE,
        SE_RESIZE,
        E_RESIZE,
        W_RESIZE,
        N_RESIZE,
        S_RESIZE
    }


    private interface IonDragResizeEventListener {
        void onDrag(Event event, Node node, double x, double y, double w, double h);

        void onResize(Event event, Node node, double x, double y, double w, double h);
    }
}