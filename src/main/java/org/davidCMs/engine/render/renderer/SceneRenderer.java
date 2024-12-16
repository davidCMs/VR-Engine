package org.davidCMs.engine.render.renderer;

import org.davidCMs.engine.render.renderer.oglobjects.UBO;
import org.davidCMs.engine.render.renderer.oglobjects.VAO;
import org.davidCMs.engine.render.renderer.oglobjects.VBO;
import org.davidCMs.engine.scene.Scene;
import org.davidCMs.engine.scene.gameobject.components.MeshComponent;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.glProgramUniform4f;
import static org.lwjgl.opengl.GL41.glProgramUniformMatrix4fv;

public class SceneRenderer extends Renderer {

    private final Scene scene;
    private ShaderProgram currentShader;
    private VAO currentVao;
    private VBO currentVbo;
    private final UBO matrixUBO = new UBO(16 * 2 * Float.BYTES, 0);

    public SceneRenderer(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void render(Matrix4f view, Matrix4f projection) {

        FloatBuffer matrices = MemoryUtil.memAllocFloat(32);
        view.get(0, matrices);
        projection.get(16, matrices);
        matrixUBO.uploadData(matrices, 0);
        MemoryUtil.memFree(matrices);

        scene.getGameObjects().forEach(gameObject -> {
            if (!gameObject.hasComponent(MeshComponent.class))
                return;

            MeshComponent meshComponent = gameObject.getComponent(MeshComponent.class);
            TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);

            ShaderProgram shader = meshComponent.getMesh().getMaterial().getShader();
            if (shader != currentShader) {
                shader.use();
                currentShader = shader;
            }

            float[] modelMat = new float[16];
            transformComponent.getTransform().get(modelMat);

            VAO vao = meshComponent.getMesh().getVao();
            if (vao != currentVao) {
                vao.bind();
                currentVao = vao;
            }
            if (vao.getVbo() != currentVbo) {
                vao.getVbo().bind();
                currentVbo = vao.getVbo();
            }

            glProgramUniformMatrix4fv(
                    currentShader.getShaderProgram(),
                    currentShader.getUniform("model"),
                    false,
                    modelMat
            );

            glProgramUniform4f(
                    currentShader.getShaderProgram(),
                    currentShader.getUniform("color"),
                    meshComponent.getMesh().getMaterial().getColor().x,
                    meshComponent.getMesh().getMaterial().getColor().y,
                    meshComponent.getMesh().getMaterial().getColor().z,
                    meshComponent.getMesh().getMaterial().getColor().w
            );

            glDrawElements(GL_TRIANGLES, meshComponent.getMesh().getVao().getEbo().getIndices().length, GL_UNSIGNED_INT, 0);

        });
    }
}
