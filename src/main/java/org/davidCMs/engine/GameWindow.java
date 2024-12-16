package org.davidCMs.engine;

import org.davidCMs.engine.window.GLFWWindow;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;

public class GameWindow extends GLFWWindow {

    private final Matrix4f projection = new Matrix4f();

    public GameWindow() {
        super(600 * 2, 400 * 2, "Game", true);
        setVSync(true);

        addKeyCallback((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                close();
        });

        addWindowSizeCallback((window, width, height) -> {
            if (width > 0 && height > 0 && hasContext()) {
                GL11.glViewport(0, 0, width, height);
                projection.identity().perspective(
                        (float) Math.toRadians(70),
                        (float) width / height,
                        0.01f,
                        10000
                );
            }
        });

        Vector2f windowSize = getSize();
        projection.perspective((float) Math.toRadians(70), windowSize.x / windowSize.y, 0.01f, 10000);

        show();
    }

    public Matrix4f getProjectionMat() {
        return projection;
    }
}
