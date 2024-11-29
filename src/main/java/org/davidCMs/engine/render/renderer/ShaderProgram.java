package org.davidCMs.engine.render.renderer;

import org.davidCMs.engine.exceptions.ShaderException;
import org.davidCMs.engine.render.renderer.oglobjects.Bindable;

import static org.lwjgl.opengl.GL43.*;

public class ShaderProgram implements Bindable {

    public static class ShaderProgramBuilder {
        private Shader vert;
        private Shader frag;
        private Shader geom;

        public ShaderProgramBuilder attachVertexShader(Shader vertex) {
            vert = vertex;
            return this;
        }
        public ShaderProgramBuilder attachFragmentShader(Shader fragment) {
            frag = fragment;
            return this;
        }
        public ShaderProgramBuilder attachGeometryShader(Shader geometry) {
            geom = geometry;
            return this;
        }

        public ShaderProgram build() {
            if (vert == null)
                throw new ShaderException("Cannot create a shader program without an vertex shader.");
            if (frag == null)
                throw new ShaderException("Cannot create a shader program without an fragment shader.");

            return new ShaderProgram(this);
        }

        public Shader getVert() {
            return vert;
        }
        public Shader getFrag() {
            return frag;
        }
        public Shader getGeom() {
            return geom;
        }
    }


    private Shader vert;
    private Shader frag;
    private Shader geom;

    private final int shaderProgram;

    public ShaderProgram() {
        shaderProgram = glCreateProgram();
    }

    private ShaderProgram(ShaderProgramBuilder builder) {
        this.vert = builder.getVert();
        this.frag = builder.getFrag();
        this.geom = builder.getGeom();

        shaderProgram = glCreateProgram();
        attachShaders();
        compile();
        link();
    }

    public void attachShaders() {
        glAttachShader(shaderProgram, vert.getShader());
        glAttachShader(shaderProgram, frag.getShader());
        if (geom != null) glAttachShader(shaderProgram, geom.getShader());
    }

    private void compile() {
        if (vert == null)
            throw new ShaderException("Cannot compile shader program as vertex shader is null");
        if (frag == null)
            throw new ShaderException("Cannot compile shader program as fragment shader is null");

        vert.compileShader();
        frag.compileShader();

        if (geom != null) geom.compileShader();
    }

    public void link() {
        glLinkProgram(shaderProgram);
    }

    public String getVertexSrc() {
        return vert.getSource();
    }

    public void setVertexSrc(String source) {
        vert.setSource(source);
        vert.compileShader();
        link();
    }

    public int getUniform(String name) {
        return glGetUniformLocation(shaderProgram, name);
    }

    public void setVertexShader(Shader vertex) {
        this.vert = vertex;
        attachShaders();
        compile();
        link();
    }

    public String getFragmentSrc() {
        return frag.getSource();
    }

    public void setFragmentSrc(String source) {
        frag.setSource(source);
        frag.compileShader();
        link();
    }

    public void setFragmentShader(Shader fragment) {
        this.frag = fragment;
        attachShaders();
        compile();
        link();
    }

    public String getGeometrySrc() {
        return geom.getSource();
    }

    public void setGeometrySrc(String source) {
        geom.setSource(source);
        geom.compileShader();
        link();
    }

    public void setGeometryShader(Shader geometry) {
        this.geom = geometry;
        attachShaders();
        compile();
        link();
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    public void use() {
        bind();
    }

    @Override
    public void bind() {
        glUseProgram(shaderProgram);
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }
}
