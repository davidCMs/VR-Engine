package org.davidCMs.engine.utils;


/** @see <a href="https://easings.net/">https://easings.net/</a> */
public class EasingFunctions {

    public static float easeOutBack(float x) {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    public static float easeOutBounce(double x) {
        double n1 = 7.5625;
        double d1 = 2.75;

        if (x < 1 / d1) {
            return (float) (n1 * x * x);
        } else if (x < 2 / d1) {
            x -= 1.5 / d1;
            return (float) (n1 * x * x + 0.75);
        } else if (x < 2.5 / d1) {
            x -= 2.25 / d1;
            return (float) (n1 * x * x + 0.9375);
        } else {
            x -= 2.625 / d1;
            return (float) (n1 * x * x + 0.984375);
        }
    }

    public static float easeInOutElastic(float x) {
        final float c5 = (float) (2 * Math.PI) / 4.5f;

        if (x == 0 || x == 1) {
            return x;
        }

        double sin = Math.sin((20 * x - 11.125) * c5);
        if (x < 0.5) {
            return (float) -(Math.pow(2, 20 * x - 10) * sin) / 2;
        } else {
            return (float) (Math.pow(2, -20 * x + 10) * sin) / 2 + 1;
        }
    }

    public static float easeOutInElastic(float x) {
        return 1 - easeInOutElastic(1 - x);
    }

    public static float easeInBounce(float x) {
        return 1 - easeOutBounce(1 - x);
    }
}
