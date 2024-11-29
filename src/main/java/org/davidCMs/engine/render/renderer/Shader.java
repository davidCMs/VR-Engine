package org.davidCMs.engine.render.renderer;

import org.davidCMs.engine.Constants;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader {

    public enum GLShaderType {

        VERTEX(GL_VERTEX_SHADER, "vertex"),
        FRAGMENT(GL_FRAGMENT_SHADER, "fragment"),
        GEOMETRY(GL_GEOMETRY_SHADER, "geometry");

        private final int GLConstant;
        private final String name;

        GLShaderType(int GLConstant, String name) {
            this.GLConstant = GLConstant;
            this.name = name;
        }

        public int getGLConstant() {
            return GLConstant;
        }

        public String getName() {
            return name;
        }
    }

    private final GLShaderType type;
    private final int shader;
    private String source;

    public Shader(GLShaderType type) {
        this.type = type;
        shader = glCreateShader(type.getGLConstant());
    }

    public Shader(GLShaderType type, String source) {
        this(type);
        this.source = source;

        System.out.println("Created new " + type.getName() + " shader with source: ");
        System.out.println(source);

    }

    public void compileShader() {
        glShaderSource(shader, source);
        glCompileShader(shader);

        System.out.println(glGetShaderInfoLog(shader));

    }

    public boolean isCreated() {
        return shader != 0;
    }

    int getShader() {
        return shader;
    }

    public GLShaderType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
