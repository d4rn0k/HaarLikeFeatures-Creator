package com.daron;

import com.daron.haar.features.HaarFeature;
import com.daron.haar.features.IHaar;
import com.daron.haar.features.RotatedHaarPolygon;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.Contract;

class DragResizeMod {

    private final int ResizeEventAreaMargin = 5;
    private final double minElementSizeW = 15;
    private final double minElementSizeH = 15;
    private double clickX, clickY, nodeX, nodeY, nodeH, nodeW;
    private double maxDragOrResizeParentW;
    private double maxDragOrResizeParentH;
    private double marginForDragW = 0;
    private double marginForDragH = 0;
    private boolean isNodeRotated = false;
    private final IonDragResizeEventListener defaultListener = new IonDragResizeEventListener() {
        @Override
        public void onDrag(Event event, Node node, double newX, double newY, double newWidth, double newHeight) {
//            Bounds boundsInParent = node.getParent().getBoundsInParent();
//            double width =  boundsInParent.getMaxX() - boundsInParent.getMinX();
//            double height = boundsInParent.getMaxY() - boundsInParent.getMinY();


            if (isNodeRotated) {
                RotatedHaarPolygon hp = (RotatedHaarPolygon) node;

                if (newX < hp.getIntegerHeight()) {
                    newX = hp.getIntegerHeight();
                }

                if (newX + hp.getIntegerWidth() > maxDragOrResizeParentW) {
                    newX = maxDragOrResizeParentW - hp.getIntegerWidth();
                }

                if (newY < 0) {
                    newY = 0;
                }

                if (newY + hp.getIntegerWidth() + hp.getIntegerHeight() > maxDragOrResizeParentH) {
                    newY = maxDragOrResizeParentH - hp.getIntegerHeight() - hp.getIntegerWidth();
                }

                newHeight = -1; // Not change
                newWidth = -1; //Not change


            } else {
                if (newX > maxDragOrResizeParentW + marginForDragW - newWidth) {
                    newX = maxDragOrResizeParentW + marginForDragW - newWidth;
                }
                if (newY > maxDragOrResizeParentH + marginForDragH - newHeight) {
                    newY = maxDragOrResizeParentH + marginForDragH - newHeight;
                }
                if (newX < -marginForDragW) {
                    newX = -marginForDragW;
                }
                if (newY < -marginForDragH) {
                    newY = -marginForDragH;
                }
            }

            if (node instanceof IHaar) {
                ((IHaar) node).setNewSizeAndPosition((int) newX, (int) newY, (int) newWidth, (int) newHeight);
            } else {
                setNodeSize(event, node, newX, newY, newWidth, newHeight);
            }


        }

        @Override
        public void onResize(Event event, Node node, double newX, double newY, double newWidth, double newHeight) {


            if (isNodeRotated) {
                RotatedHaarPolygon hp = (RotatedHaarPolygon) node;

                if (newWidth > maxDragOrResizeParentW - (hp.getIntegerX() + hp.getIntegerWidth())) {
                    newWidth = maxDragOrResizeParentW - newHeight;
                }


                if (newX < hp.getIntegerHeight()) {
                    newX = hp.getIntegerHeight();
                }

                if (newX + hp.getIntegerWidth() > maxDragOrResizeParentW) {
                    newX = maxDragOrResizeParentW - hp.getIntegerWidth();
                }

                if (newY + hp.getIntegerWidth() + hp.getIntegerHeight() > maxDragOrResizeParentH) {
                    newY = maxDragOrResizeParentH - hp.getIntegerHeight() - hp.getIntegerWidth();
                }

                if (newY < 0) {
                    newY = 0;
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


            if (node instanceof IHaar) {
                ((IHaar) node).setNewSizeAndPosition((int) newX, (int) newY, (int) newWidth, (int) newHeight);
            } else {
                setNodeSize(event, node, newX, newY, newWidth, newHeight);
            }

        }

        private void setNodeSize(Event event, Node node, double x, double y, double w, double h) {

//            if (runWithShiftOrNot(event)) {
//                return;
//            }

            node.setLayoutX(x);
            node.setLayoutY(y);
            // TODO find generic way to set width and height of any node
            // here we cant set height and width to node directly.
            // or i just cant find how to do it,
            // so you have to wright resize code anyway for your Nodes...
            //something like this
            if (node instanceof Canvas) {
                ((Canvas) node).setWidth(w);
                ((Canvas) node).setHeight(h);
            } else if (node instanceof HaarFeature) {
                HaarFeature rect = (HaarFeature) node;

                rect.setWidth(w);
                rect.maxWidth(w);
                rect.minWidth(w);

                rect.setHeight(h);
                rect.minHeight(h);
                rect.maxHeight(h);

            } else if (node instanceof Pane) {

                Pane pane = (Pane) node;

                pane.maxWidth(w);
                pane.minWidth(w);
                pane.setPrefWidth(w);

                pane.setMaxHeight(h);
                pane.setMinHeight(h);
                pane.setPrefHeight(h);
            }
        }
    };
    private MOUSESTATES state = MOUSESTATES.DEFAULT;
    private Node node;
    private boolean onlyDraggable = false;
    private IonDragResizeEventListener listener;

    public DragResizeMod(Node node, double maxDragOrResizeParentWidth, double maxDragOrResizeParentHeight) {
        this.maxDragOrResizeParentW = maxDragOrResizeParentWidth;
        this.maxDragOrResizeParentH = maxDragOrResizeParentHeight;

        this.node = node;
        listener = defaultListener;
    }

    @Contract(pure = true)
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

    private void mouseDragged(MouseEvent event) {

        if (listener != null) {
            double mouseX = parentX(event.getX());
            double mouseY = parentY(event.getY());

            if (onlyDraggable || state == MOUSESTATES.DRAG) {

                if (isNodeRotated) {
                    listener.onDrag(event, node, mouseX, mouseY, nodeW, nodeH);
                } else {
                    listener.onDrag(event, node, mouseX - clickX, mouseY - clickY, nodeW, nodeH);
                }

            } else if (state != MOUSESTATES.DEFAULT) {
                //resizing
                double newX = nodeX;
                double newY = nodeY;
                double newH = nodeH;
                double newW = nodeW;


                if (isNodeRotated) {
                    mouseX = parentX(event.getX());
                    mouseY = parentY(event.getY());

                    if (state == MOUSESTATES.NW_RESIZE) {
                        newW = Math.abs(mouseX - nodeX);
                        newX = mouseX;
                        newY = mouseY;
                    } else if (state == MOUSESTATES.SE_RESIZE) {
                        newW = Math.abs(mouseX - nodeX);
                        newX = -1;
                        newY = -1;
                    } else if (state == MOUSESTATES.NE_RESIZE) {
                        newH = Math.abs(mouseY - nodeY);
                        newX = mouseX;
                        newY = mouseY;
                    } else if (state == MOUSESTATES.SW_RESIZE) {
                        newH = Math.abs(mouseY - nodeY);
                        newX = -1;
                        newY = -1;
                    }

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
                if (newW < minElementSizeW) {
                    if (state == MOUSESTATES.W_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.SW_RESIZE) {
                        newX = newX - minElementSizeW + newW;
                    }
                    newW = minElementSizeW;
                }

                if (newH < minElementSizeH) {
                    if (state == MOUSESTATES.N_RESIZE || state == MOUSESTATES.NW_RESIZE || state == MOUSESTATES.NE_RESIZE) {
                        newY = newY + newH - minElementSizeH;
                    }
                    newH = minElementSizeH;
                }

                listener.onResize(event, node, newX, newY, newW, newH);
            }
        }
    }

    private void mousePressed(MouseEvent event) {

//        if (isInResizeZone(event)) {
//            setNewInitialEventCoordinates(event);
//            state = currentMouseState(event);
//        } else if (isInDragZone(event)) {
//            setNewInitialEventCoordinates(event);
//            state = MOUSESTATES.DRAG;
//        } else {
//            state = MOUSESTATES.DEFAULT;
//        }
        if (isInResizeZone(event)) {
            setNewInitialEventCoordinates(event);
            state = currentMouseState(event);
        } else {
            setNewInitialEventCoordinates(event);
            state = MOUSESTATES.DRAG;
        }
    }

    private void setNewInitialEventCoordinates(MouseEvent event) {
        nodeX = nodeX();
        nodeY = nodeY();
        nodeH = nodeH();
        nodeW = nodeW();
        clickX = event.getX();
        clickY = event.getY();
    }

    private MOUSESTATES currentMouseState(MouseEvent event) {
        MOUSESTATES state = MOUSESTATES.DEFAULT;
        boolean left = isLeftResizeZone(event);
        boolean right = isRightResizeZone(event);
        boolean top = isTopResizeZone(event);
        boolean bottom = isBottomResizeZone(event);

        if (isNodeRotated) {
            if (right) {
                state = MOUSESTATES.SE_RESIZE;
            } else if (left) {
                state = MOUSESTATES.NW_RESIZE;
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
            RotatedHaarPolygon target = ((RotatedHaarPolygon) event.getTarget());

            double minY = target.getBoundsInParent().getMinY();

            double diff = event.getY() - minY;

            double targetX = target.getIntegerX();

            return intersect(targetX - diff, event.getX());
        } else {
            return intersect(0, event.getX());
        }
    }

    private boolean isRightResizeZone(MouseEvent event) {

        if (isNodeRotated) {
            RotatedHaarPolygon target = ((RotatedHaarPolygon) event.getTarget());

            double diff = event.getY() - (target.getIntegerX() + target.getIntegerWidth());

            double targetX = target.getIntegerX() + target.getIntegerWidth();

            return intersect(targetX - diff, event.getX());

        } else {
            return intersect(nodeW(), event.getX());
        }

    }

    private boolean isTopResizeZone(MouseEvent event) {
        if (isNodeRotated) {

            RotatedHaarPolygon target = ((RotatedHaarPolygon) event.getTarget());

            double minY = target.getBoundsInParent().getMinY();

            double diff = event.getX() - minY;

            double targetX = target.getIntegerX();

            return intersect(targetX + diff, event.getY());
        } else {
            return intersect(0, event.getY());
        }
    }

    private boolean isBottomResizeZone(MouseEvent event) {
        if (isNodeRotated) {

            RotatedHaarPolygon target = ((RotatedHaarPolygon) event.getTarget());

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
            RotatedHaarPolygon haarPolygon = (RotatedHaarPolygon) this.node;

            return haarPolygon.getBoundsPoints().a.getX();
        }

        return node.getBoundsInParent().getMinX();
    }

    private double nodeY() {
        return node.getBoundsInParent().getMinY();
    }

    private double nodeW() {
        if (isNodeRotated) {
            return ((RotatedHaarPolygon) node).getIntegerWidth();
        }

        return node.getBoundsInParent().getWidth();
    }

    private double nodeH() {
        if (isNodeRotated) {
            return ((RotatedHaarPolygon) node).getIntegerHeight();
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