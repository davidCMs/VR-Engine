package org.davidCMs.engine.render.renderer;

import org.davidCMs.engine.render.Texture;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.Optional;

public class Material {

    private ShaderProgram shader;
    private Texture texture;
    private Vector4f color;

    public static final ShaderProgram defaultShader;

    static {
        try {
            defaultShader = new ShaderProgram.ShaderProgramBuilder()
                            .attachVertexShader(new Shader(Shader.GLShaderType.VERTEX, new String(Material.class.getResourceAsStream("/shaders/vertex.vsh").readAllBytes())))
                            .attachFragmentShader(new Shader(Shader.GLShaderType.FRAGMENT, new String(Material.class.getResourceAsStream("/shaders/fragment.fsh").readAllBytes())))
                            .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Material() {
        shader = defaultShader;
    }

    public Material(Optional<ShaderProgram> shader, Optional<Texture> texture) {
        shader.ifPresentOrElse(
                shaderProgram -> this.shader = shaderProgram,
                () -> this.shader = defaultShader);
        texture.ifPresent(value -> this.texture = value);
    }

    public void bind() {
        shader.bind();
        texture.bind();
    }

    public void unbind() {
        shader.unbind();
        texture.unbind();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public Vector4f getColor() {
        return color;
    }
}
