package org.davidCMs.engine.render.renderer.oglobjects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class VAO extends OpenGLObject {

    private VBO vbo;
    private EBO ebo;
    private final Map<Integer, Integer> map = new HashMap<>();

    public VAO() {
        id = GL30.glGenVertexArrays();

        bind();

        int stride = (3 + 3 + 2) * Float.BYTES;

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, stride, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, stride, 6 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);
    }
    public VAO(VBO vbo, EBO ebo) {
        this();
        attachVBO(vbo);
        attachEBO(ebo);
    }

    public void attachVBO(VBO vbo) {
        bind();
        this.vbo = vbo;
        vbo.bind();
    }

    public void attachEBO(EBO ebo) {
        bind();
        this.ebo = ebo;
        ebo.bind();
    }

    public void setAttributePointer(int index, int size, int stride, int offset) {
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, stride, offset);
        GL20.glEnableVertexAttribArray(index);

        map.put(index, size);
    }

    public int getVertexSize() {
        AtomicInteger vertexSize = new AtomicInteger(0);
        map.values().forEach(vertexSize::addAndGet);
        return vertexSize.get();
    }

    @Override
    public void bind() {
        GL30.glBindVertexArray(id);
    }

    @Override
    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public VBO getVbo() {
        return vbo;
    }

    public EBO getEbo() {
        return ebo;
    }
}
