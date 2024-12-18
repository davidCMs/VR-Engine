package org.davidCMs.engine.scene.gameobject.components;

import org.davidCMs.engine.exceptions.GameObjectStateException;
import org.davidCMs.engine.scene.gameobject.Component;
import org.davidCMs.engine.scene.gameobject.GameObject;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.scene.gameobject.components.transform.listeners.PositionListener;
import org.davidCMs.engine.scene.gameobject.components.transform.listeners.RotationListener;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CameraComponent extends Component {

    private TransformComponent transform;
    private Vector3f cameraTarget;
    private Vector3f up;
    private Matrix4f view;

    private final PositionListener positionListener = (newTrans) -> update();
    private final RotationListener rotationListener = (newTrans) -> update();

    public CameraComponent() {
        view = new Matrix4f();
        cameraTarget = new Vector3f(0,0,0);
        up = new Vector3f(0,1,0);
    }

    public void setUp(Vector3f up) {
        this.up = up;
        try { update(); } catch (GameObjectStateException ignored) {}
    }

    public Matrix4f getView() {
        return view;
    }

    public void update() {
        if (gameObject == null)
            throw new GameObjectStateException("Cannot update as gameObject is null");

        Quaternionf rotation = transform.getRotation();
        Vector3f position = transform.getPosition();

        view.identity()
                .rotate(rotation)
                .translate(-position.x, -position.y, -position.z);
    }

    public void lookAt(Vector3f target, Vector3f up) {
        if (gameObject == null)
            throw new GameObjectStateException("Cannot look at as gameObject is null");

        this.up = up;
        view.identity().lookAt(transform.getPosition(), target, up);
        transform.setRotation(new Quaternionf().setFromNormalized(view));
    }

    @Override
    public void onAdd(GameObject gameObject) {
        super.onAdd(gameObject);

        if (!gameObject.hasComponent(TransformComponent.class))
            throw new GameObjectStateException("Cannot add an camera component to a game object without and transform component");
        transform = gameObject.getComponent(TransformComponent.class);

        transform.addPositionListener(positionListener);
        transform.addRotationListener(rotationListener);

        update();
    }

    @Override
    public void onRemove() {
        super.onRemove();

        transform.removePositionListener(positionListener);
        transform.removeRotationListener(rotationListener);

        transform = null;
    }

    @Override
    public void onUpdate(double timeDelta) {

    }

}
