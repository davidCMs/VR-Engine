package org.davidCMs.engine.scene.gameobject.components.transform.listeners;

import org.joml.Matrix4f;

@FunctionalInterface
public interface PositionListener {
    void onPositionChange(Matrix4f oldTrans, Matrix4f newTrans);
}
