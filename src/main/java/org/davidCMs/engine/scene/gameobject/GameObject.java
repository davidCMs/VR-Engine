package org.davidCMs.engine.scene.gameobject;

import org.davidCMs.engine.exceptions.GameObjectStateException;
import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GameObject {

    private GameObject parent;
    private final Vector<ParentChangeListener> parentChangeListeners = new Vector<>();
    private final Vector<GameObject> children = new Vector<>();
    private final Map<Class<? extends Component>, Component> components = new HashMap<>(1);

    public GameObject() {
        addComponent(new TransformComponent());
        // todo make a constructor that takes in pos,angle,size and pass it to transform comp
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        component.onAdd(this);
    }

    public <T extends Component> void removeComponent(T component) {
        if (components.containsKey(component.getClass())) {
            components.remove(component);
            component.onRemove();
        }
        else throw new GameObjectStateException("Cannot remove an nonexistent component.");
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }

    public void update(double deltaTime) {
        components.values().forEach(component -> component.onUpdate(deltaTime));
    }

    public void addChiled(GameObject gameObject) {
        children.add(gameObject);
        gameObject.setParent(this);
    }

    private void triggerParentChangeListeners(GameObject oldParent, GameObject newParent) {
        parentChangeListeners.forEach((parentChangeListener -> parentChangeListener.onChange(oldParent, newParent)));
    }

    public void addParentChangeListener(ParentChangeListener listener) {
        parentChangeListeners.add(listener);
    }
    public void removeParentChangeListener(ParentChangeListener listener) {
        parentChangeListeners.remove(listener);
    }
    public boolean containsParentChangeListener(ParentChangeListener listener) {
        return parentChangeListeners.contains(listener);
    }

    public GameObject getParent() {
        return parent;
    }

    public Vector<GameObject> getChildren() {
        return children;
    }
    public void setParent(GameObject parent) {
        triggerParentChangeListeners(this.getParent(), parent);
        this.parent = parent;
    }
}
