package org.davidCMs.engine.scene.gameobject.components.transform;

import org.davidCMs.engine.scene.gameobject.Component;
import org.davidCMs.engine.scene.gameobject.GameObject;
import org.davidCMs.engine.scene.gameobject.components.transform.listeners.PositionListener;
import org.davidCMs.engine.scene.gameobject.components.transform.listeners.RotationListener;
import org.davidCMs.engine.scene.gameobject.components.transform.listeners.ScaleListener;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Stack;
import java.util.Vector;

/** The {@code TransformComponent} class is a component that can be attached to a {@link GameObject} object, this will
 * give that game object the ability to be represented in world space. you can also attach listeners to it
 * ({@link PositionListener}, {@link RotationListener}, {@link ScaleListener}) witch can be used to
 * synchronise with other game objects or even physics in the future. It will automatically synchronise the transforms
 * of the children of the object it's attached to.*/
public class TransformComponent extends Component {

    /** The matrix that actually represents the transform. */
    private Matrix4f transform;
    /** The Position of the transform. */
    private final Vector3f position;
    /** The rotation of the transform. */
    private final Quaternionf rotation;
    /** The scale of the transform. */
    private final Vector3f scale;

    /** An {@link Vector} of all the attached {@link PositionListener}s. */
    private final Vector<PositionListener> positionListeners = new Vector<>();
    /** An {@link Vector} of all the attached {@link RotationListener}s. */
    private final Vector<RotationListener> rotationListeners = new Vector<>();
    /** An {@link Vector} of all the attached {@link ScaleListener}s. */
    private final Vector<ScaleListener> scaleListeners = new Vector<>();

    public TransformComponent() {
        transform = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1, 1, 1);
        updateTransform();
    }

    public TransformComponent(Vector3f pos) {
        this();
        position.set(pos);
        updateTransform();
    }

    public TransformComponent(Vector3f pos, Quaternionf rotation) {
        this(pos);
        this.rotation.set(rotation);
        updateTransform();
    }

    public TransformComponent(Vector3f pos, Quaternionf rotation, Vector3f scale) {
        this(pos, rotation);
        this.scale.set(scale);
        updateTransform();
    }

    /** Updates the {@link TransformComponent#transform} with value from
     * {@link TransformComponent#position}, {@link TransformComponent#rotation}, {@link TransformComponent#scale}.*/
    private void updateTransform() {
        transform.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    /** Triggers all the {@link  PositionListener}s in {@link TransformComponent#positionListeners}. */
    private void triggerPosListeners() {
        for (PositionListener positionListener : positionListeners)
            positionListener.onPositionChange(transform);
    }
    /** Triggers all the {@link  RotationListener}s in {@link TransformComponent#rotationListeners}. */
    private void triggerRotListeners() {
        for (RotationListener rotationListener : rotationListeners)
            rotationListener.onRotationChange(transform);
    }
    /** Triggers all the {@link  ScaleListener}s in {@link TransformComponent#scaleListeners}. */
    private void triggerSclListeners() {
        for (ScaleListener scaleListener : scaleListeners)
            scaleListener.onScaleChange(transform);
    }

    /** Sets the {@link TransformComponent#transform}, updates the
     * {@link TransformComponent#position}, {@link TransformComponent#rotation} and {@link TransformComponent#scale}
     * and calls
     * {@link TransformComponent#triggerPosListeners},
     * {@link TransformComponent#triggerRotListeners} and
     * {@link TransformComponent#triggerSclListeners}.
     *
     * @param transform the new transform. */
    public void setTransform(Matrix4f transform) {
        this.transform = transform;

        transform.getTranslation(position);
        transform.getNormalizedRotation(rotation);
        transform.getScale(scale);

        triggerPosListeners();
        triggerRotListeners();
        triggerSclListeners();
    }
    /** Sets the {@link TransformComponent#position}, updates the {@link TransformComponent#transform} and calls
     * {@link TransformComponent#triggerPosListeners}.
     * @param pos the new pos. */
    public void setPosition(Vector3f pos) {
        position.set(pos);
        updateTransform();
        triggerPosListeners();
    }
    /** Sets the {@link TransformComponent#rotation}, updates the {@link TransformComponent#transform} and calls
     * {@link TransformComponent#triggerRotListeners}.
     * @param rotation the new angle. */
    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
        updateTransform();
        triggerRotListeners();
    }
    /** Sets the {@link TransformComponent#scale}, updates the {@link TransformComponent#transform} and calls
     * {@link TransformComponent#triggerSclListeners}.
     * @param scale the new scale.*/
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        updateTransform();
        triggerSclListeners();
    }

    //todo write java doc
    //todo rewrite to be non-static implementation
    public static Matrix4f getGlobalTransform(GameObject gameObject) {
        Matrix4f globalTransform = new Matrix4f(gameObject.getComponent(TransformComponent.class).getTransform());

        Stack<GameObject> gameObjectStack = new Stack<>();
        GameObject current = gameObject.getParent();

        while (current != null && current.hasComponent(TransformComponent.class)) {
            gameObjectStack.push(current);
            current = current.getParent();
        }

        while (!gameObjectStack.isEmpty()) {
            globalTransform.mul(gameObjectStack.pop().getComponent(TransformComponent.class).getTransform());
        }

        return globalTransform;
    }

    /** Gets the value in {@link TransformComponent#transform}.
     * @return the transform. */
    public Matrix4f getTransform() {
        return transform;
    }
    /** Gets the value in {@link TransformComponent#position}.
     * @return the position of the transform. */
    public Vector3f getPosition() {
        return position;
    }
    /** Gets the value in {@link TransformComponent#rotation}.
     * @return the rotation of the transform. */
    public Quaternionf getRotation() {
        return rotation;
    }
    /** Gets the value in {@link TransformComponent#scale}.
     * @return the scale of the transform. */
    public Vector3f getScale() {
        return scale;
    }

    /** Adds an {@link PositionListener} to {@link TransformComponent#positionListeners}.
     * @param listener the listener that will be added. */
    public void addPositionListener(PositionListener listener) {
        positionListeners.add(listener);
    }
    /** Adds an {@link RotationListener} to {@link TransformComponent#rotationListeners}.
     * @param listener the listener that will be added. */
    public void addRotationListener(RotationListener listener) {
        rotationListeners.add(listener);
    }
    /** Adds an {@link ScaleListener} to {@link TransformComponent#scaleListeners}.
     * @param listener the listener that will be added. */
    public void addScaleListener(ScaleListener listener) {
        scaleListeners.add(listener);
    }

    /** removes an {@link PositionListener} from {@link TransformComponent#positionListeners}.
     * @param listener the listener that will be removed. */
    public void removePositionListener(PositionListener listener) {
        positionListeners.remove(listener);
    }
    /** removes an {@link RotationListener} from {@link TransformComponent#rotationListeners}.
     * @param listener the listener that will be removed. */
    public void removeRotationListener(RotationListener listener) {
        rotationListeners.remove(listener);
    }
    /** removes an {@link ScaleListener} from {@link TransformComponent#scaleListeners}.
     * @param listener the listener that will be removed. */
    public void removeScaleListener(ScaleListener listener) {
        scaleListeners.remove(listener);
    }

    @Override
    public void onAdd(GameObject gameObject) {
        super.onAdd(gameObject);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        positionListeners.clear();
        rotationListeners.clear();
        scaleListeners.clear();
    }

    @Override
    public void onUpdate(double timeDelta) {}

}
