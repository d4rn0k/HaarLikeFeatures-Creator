package com.daron.utils;

import javafx.scene.Cursor;

public enum MouseStates {
    DEFAULT,
    DRAG,
    NW_RESIZE,
    SW_RESIZE,
    NE_RESIZE,
    SE_RESIZE,
    E_RESIZE,
    W_RESIZE,
    N_RESIZE,
    S_RESIZE;

    public static Cursor getCursorForState(MouseStates state) {
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

    public boolean isResizeState() {
        return this.name().contains("RESIZE");
    }

    public boolean isNorthOrSubNorthState() {
        return this.equals(N_RESIZE) || this.equals(NW_RESIZE) || this.equals(NE_RESIZE);
    }


    public boolean isSouthOrSubSouthState() {
        return this.equals(S_RESIZE) || this.equals(SW_RESIZE) || this.equals(SE_RESIZE);
    }

    public boolean isWestOrSubWestState() {
        return this.equals(W_RESIZE) || this.equals(NW_RESIZE) || this.equals(SW_RESIZE);
    }

    public boolean isEastOrSubEastState() {
        return this.equals(E_RESIZE) || this.equals(NE_RESIZE) || this.equals(SE_RESIZE);
    }

}


