package org.davidCMs.engine.render.renderer;

import org.joml.Matrix4f;

public abstract class Renderer {
    public abstract void render(Matrix4f view, Matrix4f projection);
}
