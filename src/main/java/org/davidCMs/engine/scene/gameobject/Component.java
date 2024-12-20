package org.davidCMs.engine.scene.gameobject;

public abstract class Component {

    protected boolean active;
    protected GameObject gameObject;

    protected void onAdd(GameObject gameObject) {
        if (this.gameObject != null)
            onRemove();
        this.gameObject = gameObject;
    }
    protected void onRemove() {
        this.gameObject = null;
    }

    protected void enable(GameObject gameObject) {
        active = true;
    }

    protected void disable(GameObject gameObject) {
        active = false;
    }

    protected abstract void onUpdate(double timeDelta);
}
