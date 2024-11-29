package org.davidCMs.engine.render.renderer;

import org.davidCMs.engine.scene.Scene;
import org.davidCMs.engine.scene.gameobject.components.MeshComponent;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.glProgramUniform4f;
import static org.lwjgl.opengl.GL41.glProgramUniformMatrix4fv;

public class SceneRenderer extends Renderer {

    private final Scene scene;

    public SceneRenderer(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void render(Matrix4f view, Matrix4f projection) {
        scene.getGameObjects().forEach(gameObject -> {
            if (!gameObject.hasComponent(MeshComponent.class))
                return;

            MeshComponent meshComponent = gameObject.getComponent(MeshComponent.class);
            TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);

            float[] modelMat = new float[16];
            transformComponent.getTransform().get(modelMat);

            meshComponent.getMesh().getVao().bind();
            meshComponent.getMesh().getVao().getVbo().bind();
            meshComponent.getMesh().getMaterial().getShader().use();

            glProgramUniformMatrix4fv(
                    meshComponent.getMesh().getMaterial().getShader().getShaderProgram(),
                    meshComponent.getMesh().getMaterial().getShader().getUniform("model"),
                    false,
                    modelMat
            );

            float[] viewFloats = new float[16];
            view.get(viewFloats);
            glProgramUniformMatrix4fv(
                    meshComponent.getMesh().getMaterial().getShader().getShaderProgram(),
                    meshComponent.getMesh().getMaterial().getShader().getUniform("view"),
                    false,
                    viewFloats
            );

            float[] projectionFloats = new float[16];
            projection.get(projectionFloats);
            glProgramUniformMatrix4fv(
                    meshComponent.getMesh().getMaterial().getShader().getShaderProgram(),
                    meshComponent.getMesh().getMaterial().getShader().getUniform("projection"),
                    false,
                    projectionFloats
            );

            glProgramUniform4f(
                    meshComponent.getMesh().getMaterial().getShader().getShaderProgram(),
                    meshComponent.getMesh().getMaterial().getShader().getUniform("color"),
                    meshComponent.getMesh().getMaterial().getColor().x,
                    meshComponent.getMesh().getMaterial().getColor().y,
                    meshComponent.getMesh().getMaterial().getColor().z,
                    meshComponent.getMesh().getMaterial().getColor().w
            );

            glDrawElements(GL_TRIANGLES, meshComponent.getMesh().getVao().getEbo().getIndices().length, GL_UNSIGNED_INT, 0);

        });
    }
}
