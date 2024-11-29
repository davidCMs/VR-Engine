package org.davidCMs.engine.scene;

import org.davidCMs.engine.scene.gameobject.GameObject;

import java.util.Vector;

public class Scene {
    private final Vector<GameObject> gameObjects = new Vector<>();

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public GameObject remove(GameObject gameObject) {
        gameObjects.remove(gameObject);
        return gameObject;
    }

    public boolean contains(GameObject gameObject) {
        return gameObjects.contains(gameObject);
    }

    public void update(double deltaTime) {
        gameObjects.forEach(gameObject -> gameObject.update(deltaTime));
    }

    public Vector<GameObject> getGameObjects() {
        return gameObjects;
    }
}
