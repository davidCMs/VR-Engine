package org.davidCMs.engine.render.renderer.oglobjects;

import org.lwjgl.opengl.GL15;

public enum GLDrawType {

    STATIC(GL15.GL_STATIC_DRAW),
    DYNAMIC(GL15.GL_DYNAMIC_DRAW),
    STREAM(GL15.GL_STREAM_DRAW);

    private final int value;
    GLDrawType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
