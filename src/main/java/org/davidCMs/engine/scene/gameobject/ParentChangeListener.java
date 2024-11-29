package org.davidCMs.engine.scene.gameobject;

@FunctionalInterface
public interface ParentChangeListener {
    void onChange(GameObject oldParent, GameObject newParent);
}
