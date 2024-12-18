package org.davidCMs.engine;

import org.davidCMs.engine.render.renderer.Material;
import org.davidCMs.engine.render.renderer.SceneRenderer;
import org.davidCMs.engine.scene.Scene;
import org.davidCMs.engine.scene.gameobject.GameObject;
import org.davidCMs.engine.scene.gameobject.JiggleComponent;
import org.davidCMs.engine.scene.gameobject.components.CameraComponent;
import org.davidCMs.engine.scene.gameobject.components.MeshComponent;
import org.davidCMs.engine.utils.GlobalRandom;
import org.davidCMs.engine.utils.LogList;
import org.davidCMs.engine.utils.ModelLoader;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.openxr.*;
import org.davidCMs.engine.render.model.Mesh;
import org.davidCMs.engine.render.renderer.oglobjects.GLDrawType;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.openxr.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43.*;

public class Main {

    private static OpenXRInstance openXRInstance;
    private static OpenXRSystem openXRSystem;
    private static OpenXRSession openXRSession;
    private static OpenXRReferenceSpace openXRRefranceSpace;
    private static OpenXRSwapchains openXRSwapchains;

    public static XrEventDataBuffer eventDataBuffer;

    private static AtomicBoolean update = new AtomicBoolean(false);

    private static final int TARGET_UPS = 60;
    private static Scene scene;

    private static final LogList FPSLog = new LogList(100);
    private static final LogList UPSLog = new LogList(10);


    public static void main(String[] args) {

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        else
            System.out.println("Successfully initialised GLFW");
        GLFWErrorCallback.createPrint(System.err).set();

        GameWindow gameWindow = new GameWindow();

        scene = new Scene();
        SceneRenderer sceneRenderer = new SceneRenderer(scene);

        //openXRInstance = new OpenXRInstance();
        //openXRSystem = new OpenXRSystem(openXRInstance);
        //openXRSession = new OpenXRSession(openXRInstance, openXRSystem, gameWindow);
        //openXRRefranceSpace = new OpenXRReferenceSpace(openXRSession);
        //openXRSwapchains = new OpenXRSwapchains(openXRInstance, openXRSystem, openXRSession);

        //eventDataBuffer = XrEventDataBuffer.calloc().type$Default();



        GL.createCapabilities();

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            System.err.println("GL CALLBACK: " + GLDebugMessageCallback.getMessage(length, message));
        }, 0);

        GameObject gameCamera = new GameObject();
        gameCamera.getComponent(TransformComponent.class).setPosition(new Vector3f(0.001f, 0.001f, 100.001f));
        CameraComponent gameCameraCameraComponent = new CameraComponent();
        JiggleComponent jiggleComponent = new JiggleComponent();
        gameCamera.addComponent(jiggleComponent);
        gameCamera.addComponent(gameCameraCameraComponent);
        gameCameraCameraComponent.update();
        


        AIScene aiScene = Assimp.aiImportFile("model.obj", Assimp.aiProcess_Triangulate);
        if (aiScene == null) {
            System.err.println("No cube.obj in current dir");
            System.exit(-1);
        }
        PointerBuffer buffer = aiScene.mMeshes();

        Mesh mesh = ModelLoader.loadMesh(AIMesh.create(buffer.get(0)), GLDrawType.STATIC);

        int size = 2;
        int iter = 0;

        for (int i = size/2*-1; i < size/2; i++) {
            for (int j = size/2*-1; j < size/2; j++) {
                for (int k = size/2*-1; k < size/2; k++) {

                    GameObject gameObject = new GameObject();
                    gameObject.getComponent(TransformComponent.class).setPosition(new Vector3f(i, k, j));
                    gameObject.getComponent(TransformComponent.class);

                    Material material = new Material();
                    Vector4f color = new Vector4f(
                            GlobalRandom.random.nextFloat(),
                            GlobalRandom.random.nextFloat(),
                            GlobalRandom.random.nextFloat(),
                            0.7f);
                    material.setColor(color);

                    Mesh coloredMesh = new Mesh(mesh);

                    coloredMesh.setMaterial(material);

                    MeshComponent meshComponent = new MeshComponent(coloredMesh);


                    gameObject.addComponent(meshComponent);

                    gameObject.addComponent(new JiggleComponent());

                    scene.add(gameObject);
                    iter++;
                }
            }
        }

        System.out.println("looped " + iter + " times");



        gameWindow.addKeyCallback((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS)
                update.set(!update.get());
        });

        glfwSwapInterval(0);

        double lastTime = System.nanoTime() / 1_000_000_000.0;
        double deltaTime;

        glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable updateRunnable = getUpdaterRunnable();
        executorService.scheduleAtFixedRate(updateRunnable,0, 1000/TARGET_UPS, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));

        System.gc();

        while (!gameWindow.shouldClose()) {
            double currentTime = System.nanoTime() / 1_000_000_000.0;
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            //gameObject.getComponent(TransformComponent.class).setRotation(new Quaternionf().rotateXYZ((float) 0, (float) 0, (float) ((float) i* 0.001)));

            gameCameraCameraComponent.update();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            glClear(GL_DEPTH_BUFFER_BIT);
            GL11.glClearColor(0f, 0f, 0f, 1.0f);

            sceneRenderer.render(gameCamera.getComponent(CameraComponent.class).getView(), gameWindow.getProjectionMat());

            gameWindow.swapBuffers();

            double fps = 1 / deltaTime;
            FPSLog.add(fps);

            double avgFps = FPSLog.getAverage();
            double avgUps = UPSLog.getAverage();
            gameWindow.setTitle("Game, FPS = " + avgFps + ",\t UPS = " + avgUps);

            glfwPollEvents();
        }
        executorService.shutdown();
        glfwPollEvents();
    }

    private static final long[] lastExecutionTime = {System.nanoTime()};
    private static final long[] lastSecondTime = {System.nanoTime()};
    private static int ups = 0;
    public static Runnable getUpdaterRunnable() {
        return () -> {

            long currentTime = System.nanoTime();
            double timeDelta = (currentTime - lastExecutionTime[0]) / 1_000_000_000.0;

            ups++;

            if ((currentTime - lastSecondTime[0]) >= 1_000_000_000L) {
                UPSLog.add(ups);
                ups = 0;
                lastSecondTime[0] = currentTime;
            }

            lastExecutionTime[0] = currentTime;

            if (update.get()) {
                scene.update(timeDelta);
            }

        };
    }

}