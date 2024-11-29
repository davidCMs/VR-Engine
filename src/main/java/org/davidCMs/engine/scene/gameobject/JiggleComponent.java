package org.davidCMs.engine.scene.gameobject;

import org.davidCMs.engine.scene.gameobject.components.transform.TransformComponent;
import org.davidCMs.engine.utils.EasingFunctions;
import org.davidCMs.engine.utils.GlobalRandom;
import org.joml.Random;
import org.joml.Vector3f;

public class JiggleComponent extends Component {

    double i = GlobalRandom.random.nextInt(3000);
    Vector3f scale = new Vector3f(GlobalRandom.random.nextFloat(), GlobalRandom.random.nextFloat(), GlobalRandom.random.nextFloat());

    @Override
    protected void onUpdate(double timeDelta) {
        if (!gameObject.hasComponent(TransformComponent.class))
            return;
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);



        transformComponent.setScale(
                new Vector3f(scale).mul((float) (EasingFunctions.easeOutInElastic((float) Math.abs(Math.sin(i)))+0.3))
        );
        i += timeDelta;
    }
}
