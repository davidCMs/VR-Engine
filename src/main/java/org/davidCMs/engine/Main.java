package org.davidCMs.engine;

import org.davidCMs.engine.render.renderer.Material;
import org.davidCMs.engine.render.renderer.SceneRenderer;
import org.davidCMs.engine.scene.Scene;
import org.davidCMs.engine.scene.gameobject.GameObject;
import org.davidCMs.engine.scene.gameobject.JiggleComponent;
import org.davidCMs.engine.scene.gameobject.components.CameraComponent;
import org.davidCMs.engine.scene.gameobject.components.MeshComponent;
import org.davidCMs.engine.utils.EasingFunctions;
import org.davidCMs.engine.utils.GlobalRandom;
import org.davidCMs.engine.window.GLFWException;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.openxr.*;
import org.davidCMs.engine.render.model.Mesh;
import org.davidCMs.engine.render.renderer.oglobjects.EBO;
import org.davidCMs.engine.render.renderer.oglobjects.GLDrawType;
import org.davidCMs.engine.render.renderer.oglobjects.VAO;
import org.davidCMs.engine.render.renderer.oglobjects.VBO;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.openxr.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43.*;

public class Main {

    private static OpenXRInstance openXRInstance;
    private static OpenXRSystem openXRSystem;
    private static OpenXRSession openXRSession;
    private static OpenXRReferenceSpace openXRRefranceSpace;
    private static OpenXRSwapchains openXRSwapchains;

    public static XrEventDataBuffer eventDataBuffer;

    public static void main(String[] args) {

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        else
            System.out.println("Successfully initialised GLFW");
        GLFWErrorCallback.createPrint(System.err).set();

        GameWindow gameWindow = new GameWindow();

        Scene scene = new Scene();
        SceneRenderer sceneRenderer = new SceneRenderer(scene);

        GameObject gameCamera = new GameObject();

        TransformComponent gameCameraTransformComponent = new TransformComponent(new Vector3f(1,1,1));
        CameraComponent gameCameraCameraComponent = new CameraComponent();

        gameCamera.addComponent(gameCameraTransformComponent);
        gameCamera.addComponent(gameCameraCameraComponent);

        gameCameraTransformComponent.setPosition(new Vector3f(0, 10, 15));
        gameCameraCameraComponent.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));

        //openXRInstance = new OpenXRInstance();
        //openXRSystem = new OpenXRSystem(openXRInstance);
        //openXRSession = new OpenXRSession(openXRInstance, openXRSystem, gameWindow);
        //openXRRefranceSpace = new OpenXRReferenceSpace(openXRSession);
        //openXRSwapchains = new OpenXRSwapchains(openXRInstance, openXRSystem, openXRSession);

        //eventDataBuffer = XrEventDataBuffer.calloc().type$Default();

        float[] vertices = {
                0.5f, 0.5f, 0.5f,    0.5f, 0.5f, 0.0f,      0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,   0.5f, -0.5f, 0.0f,     0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,   -0.5f, 0.5f, 0.0f,     -0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,  -0.5f, -0.5f, 0.0f,    -0.5f, -0.5f,

                0.5f, 0.5f, -0.5f,    0.5f, 0.5f, 0.0f,      0.5f, 0.5f,
                0.5f, -0.5f, -0.5f,   0.5f, -0.5f, 0.0f,     0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,   -0.5f, 0.5f, 0.0f,     -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,  -0.5f, -0.5f, 0.0f,    -0.5f, -0.5f,
        };

        int[] indices = {
                2, 6, 7, 2, 3, 7,
                0, 4, 5, 0, 1, 5,
                0, 2, 6, 0, 4, 6,
                1, 3, 7, 1, 5, 7,
                0, 2, 3, 0, 1, 3,
                4, 6, 7, 4, 5, 7
        };

        GL.createCapabilities();

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            System.err.println("GL CALLBACK: " + GLDebugMessageCallback.getMessage(length, message));
        }, 0);

        int size = 20;


        VBO vbo = new VBO(vertices, GLDrawType.STATIC);
        EBO ebo = new EBO(indices, GLDrawType.STATIC);
        VAO vao = new VAO(vbo, ebo);


        int iter = 0;

        for (int i = size/2*-1; i < size/2; i++) {
            for (int j = size/2*-1; j < size/2; j++) {

                GameObject gameObject = new GameObject();
                gameObject.getComponent(TransformComponent.class).setPosition(new Vector3f(i, 0, j));

                Material material = new Material();
                Vector4f color = new Vector4f(
                        GlobalRandom.random.nextFloat(),
                        GlobalRandom.random.nextFloat(),
                        GlobalRandom.random.nextFloat(),
                        GlobalRandom.random.nextFloat());
                material.setColor(color);

                Mesh mesh = new Mesh(vao, material);
                MeshComponent meshComponent = new MeshComponent(mesh);


                gameObject.addComponent(meshComponent);

                gameObject.addComponent(new JiggleComponent());

                scene.add(gameObject);
                iter++;
            }
        }

        System.out.println("looped " + iter + " times");

        float i = 10;
        glfwSwapInterval(0);

        double lastTime = System.nanoTime() / 1_000_000_000.0;
        double deltaTime;

        glEnable(GL_DEPTH_TEST);

        while (!gameWindow.shouldClose()) {
            double currentTime = System.nanoTime() / 1_000_000_000.0;
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            if (!gameWindow.hasContext())
                throw new GLFWException("no context?!?!?!!?!?!?!?!?");

            //gameObject.getComponent(TransformComponent.class).setRotation(new Quaternionf().rotateXYZ((float) 0, (float) 0, (float) ((float) i* 0.001)));

            scene.update(deltaTime);

            gameCameraCameraComponent.update();


            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            glClear(GL_DEPTH_BUFFER_BIT);
            GL11.glClearColor(0f, 0f, 0f, 1.0f);

            sceneRenderer.render(gameCamera.getComponent(CameraComponent.class).getView(), gameWindow.getProjectionMat());

            gameWindow.swapBuffers();

            int errorCode = GL11.glGetError();
            if (errorCode != GL11.GL_NO_ERROR) {
                System.err.println("OpenGL Error: " + errorCode);
            }

            i += deltaTime;

            glfwPollEvents();
        }
    }


}