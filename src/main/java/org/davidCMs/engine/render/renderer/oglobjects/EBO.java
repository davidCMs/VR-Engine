package org.davidCMs.engine.render.renderer.oglobjects;

import org.lwjgl.opengl.GL15;

import java.util.Arrays;

/** <p> The {@code EBO} class is Wrapper for the Element Buffer Object from openGL </p> */
public class EBO extends OpenGLObject {

    int[] indices;

    public EBO() {
        id = GL15.glGenBuffers();
    }

    public EBO(int[] indices, GLDrawType type) {
        this();
        this.indices = indices;
        uploadData(indices, type);
    }
    public void uploadData(int[] data, GLDrawType type) {
        bind();

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, type.getValue());
    }

    public void setIndices(int[] indices, GLDrawType type) {
        this.indices = indices;
        uploadData(indices, type);
    }

    public int[] getIndices() {
        return indices;
    }

    @Override
    public void bind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
    }

    @Override
    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
