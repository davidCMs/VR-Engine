package org.davidCMs.engine.scene.gameobject.components.transform.listeners;

import org.joml.Matrix4f;

@FunctionalInterface
public interface RotationListener {
    void onRotationChange(Matrix4f newTrans);
}
