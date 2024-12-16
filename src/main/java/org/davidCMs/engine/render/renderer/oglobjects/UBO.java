package org.davidCMs.engine.render.renderer.oglobjects;

import org.davidCMs.engine.exceptions.OpenGLErrorException;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL41.*;

public class UBO extends OpenGLObject {

    private final int size;

    /**
     * @param size is in bytes
     * */
    public UBO(int size, int bindingPoint, GLDrawType type) {
        this.size = size;
        id = glGenBuffers();
        bind();
        glBufferData(GL_UNIFORM_BUFFER, size, type.getValue());
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, id);
    }

    public UBO(int size, int bindingPoint) {
        this(size, bindingPoint, GLDrawType.DYNAMIC);
    }

    /**
     * @param offset is in bytes
     * */
    public void uploadData(FloatBuffer buffer, int offset) {
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER, offset, buffer);
        int error = glGetError();
        switch (error) {
            case GL_INVALID_VALUE -> throw new OpenGLErrorException("Uploading buffer caused and GL_INVALID_VALUE error");
            case GL_OUT_OF_MEMORY -> throw new OpenGLErrorException("Cannot upload buffer as GPU VRAM is full");
            case GL_INVALID_OPERATION -> throw new OpenGLErrorException("Cannot upload buffer as UBO isn't bound");
        }
    }

    @Override
    public void bind() {
        glBindBuffer(GL_UNIFORM_BUFFER, id);
    }

    @Override
    public void unbind() {
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
