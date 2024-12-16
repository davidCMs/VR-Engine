package org.davidCMs.engine.scene.gameobject;

import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.utils.EasingFunctions;
import org.davidCMs.engine.utils.GlobalRandom;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class JiggleComponent extends Component {

    double i = GlobalRandom.random.nextInt(3000);
    double speed = GlobalRandom.random.nextFloat();
    Vector3f scale = new Vector3f(GlobalRandom.random.nextFloat(), GlobalRandom.random.nextFloat(), GlobalRandom.random.nextFloat());
    Vector3f dir = new Vector3f(GlobalRandom.random.nextFloat()-0.5f, GlobalRandom.random.nextFloat()-0.5f, GlobalRandom.random.nextFloat()-0.5f);
    Vector3f rotation = new Vector3f(GlobalRandom.random.nextFloat()-0.5f, GlobalRandom.random.nextFloat()-0.5f, GlobalRandom.random.nextFloat()-0.5f).mul(0.1f);

    @Override
    protected void onUpdate(double timeDelta) {
        if (!gameObject.hasComponent(TransformComponent.class))
            return;
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);

        Vector3f pos = transformComponent.getPosition();
        pos.add(new Vector3f(dir).mul(0.5f));
        transformComponent.setPosition(new Vector3f(pos));

        Quaternionf rot = transformComponent.getRotation();
        rot.rotateAxis((float) ((float) 1*timeDelta), rotation);
        transformComponent.setRotation(new Quaternionf(rot));

        transformComponent.setScale(
                new Vector3f(scale).mul((float) (EasingFunctions.easeOutInElastic((float) Math.abs(Math.sin(i)))+0.3))
        );
        i += timeDelta*speed;
    }
}
