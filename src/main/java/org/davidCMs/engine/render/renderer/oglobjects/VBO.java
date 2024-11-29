package org.davidCMs.engine.render.renderer.oglobjects;

import org.davidCMs.engine.Constants;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.Arrays;

/** <p> The {@code EBO} class is Wrapper for the Vertex Buffer Object from openGL </p> */
public class VBO extends OpenGLObject {

    private float[] vertices;

    public VBO() {
        id = GL15.glGenBuffers();
    }
    public VBO(float[] vertices, GLDrawType type) {
        this();
        this.vertices = vertices;
        uploadData(vertices, type);
    }

    public void uploadData(float[] data, GLDrawType type) {
        bind();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, type.getValue());
    }

    public void setVertices(float[] vertices, GLDrawType type) {
        this.vertices = vertices;
        uploadData(vertices, type);
    }

    public float[] getVertices() {
        return vertices;
    }

    @Override
    public void bind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
    }

    @Override
    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
}
