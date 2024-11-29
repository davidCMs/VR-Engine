package org.davidCMs.engine.scene.gameobject.components;

import org.davidCMs.engine.exceptions.GameObjectStateException;
import org.davidCMs.engine.scene.gameobject.Component;
import org.davidCMs.engine.scene.gameobject.GameObject;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.render.model.Mesh;

public class MeshComponent extends Component {

    private Mesh mesh;

    public MeshComponent(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void onAdd(GameObject gameObject) {
        super.onAdd(gameObject);
        if (!gameObject.hasComponent(TransformComponent.class))
            throw new GameObjectStateException("Cannot add a mesh component to an gameObject without an transform component");
    }

    @Override
    public void onRemove(GameObject gameObject) {
        super.onRemove(gameObject);
    }

    @Override
    public void onUpdate(double timeDelta) {

    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
