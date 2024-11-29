package org.davidCMs.engine.utils;

import org.davidCMs.engine.exceptions.GameObjectStateException;
import org.davidCMs.engine.scene.gameobject.GameObject;
import org.davidCMs.engine.scene.gameobject.components.CameraComponent;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.window.GLFWWindow;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

public class CameraController {
    private final GLFWWindow window;
    private final TransformComponent cameraTransform;
    private final CameraComponent cameraComponent;

    private float sensitivity = .10f;
    private float movementSpeed = .1f;

    private float lastMouseX, lastMouseY;
    private boolean firstMouse = true;

    private float offsetX, offsetY;



    public CameraController(GameObject gameObject, GLFWWindow window) {
        if (!gameObject.hasComponent(CameraComponent.class))
            throw new GameObjectStateException("Cannot create a camera controller as the gameObject doesn't have a camera component");
        this.window = window;

        cameraTransform = gameObject.getComponent(TransformComponent.class);
        cameraComponent = gameObject.getComponent(CameraComponent.class);

        if (cameraTransform == null)
            throw new RuntimeException("what?");

        //window.setCursorState(GlfwEnums.CursorState.GLFW_CURSOR_DISABLED);

        GLFWCursorPosCallbackI mouseListener = (glfwWindow, xpos, ypos) -> {

            if (xpos == 0 && ypos == 0) {
                System.out.println("skipping");
                return;
            }


            if (firstMouse) {
                lastMouseX = (float) xpos;
                lastMouseY = (float) ypos;
                firstMouse = false;
            }

            offsetX = (float) xpos - lastMouseX;
            offsetY = lastMouseY - (float) ypos;

            lastMouseX = (float) xpos;
            lastMouseY = (float) ypos;

            offsetX *= sensitivity;
            offsetY *= sensitivity;

            System.out.println("x = " + offsetX + " y = " + offsetY);

        };

        window.addCursorPosCallback(mouseListener);
    }

    public void update(float deltaTime) {
        float speed = movementSpeed * deltaTime;



        // Center the cursor at the beginning of the mouse callback
        window.setCursorPosition(window.getSize().mul(.5f));

        // Calculate direction vectors
        Vector3f forward = new Vector3f(0, 0, -1).rotate(cameraTransform.getRotation());
        Vector3f right = new Vector3f(1, 0, 0).rotate(cameraTransform.getRotation());
        Vector3f up = new Vector3f(0, 1, 0);

        Quaternionf currentRotation = cameraTransform.getRotation();
        Vector3f currentPosition = cameraTransform.getPosition();

        currentRotation.rotateY((float) Math.toRadians(-offsetX));
        currentRotation.rotateLocalX((float) Math.toRadians(-offsetY));

        offsetX = 0;
        offsetY = 0;

        // Apply movement
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            currentPosition.add(forward.mul(speed, new Vector3f()));
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            currentPosition.sub(forward.mul(speed, new Vector3f()));
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            currentPosition.sub(right.mul(speed, new Vector3f()));
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            currentPosition.add(right.mul(speed, new Vector3f()));
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            currentPosition.add(up.mul(speed, new Vector3f()));
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            currentPosition.sub(up.mul(speed, new Vector3f()));
        }

        cameraTransform.setRotation(currentRotation);
        cameraTransform.setPosition(currentPosition);
    }
}
