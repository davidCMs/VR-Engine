package org.davidCMs.engine.openxr;

import org.lwjgl.openxr.XrSystemGetInfo;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;

import static org.lwjgl.openxr.XR10.XR_FORM_FACTOR_HEAD_MOUNTED_DISPLAY;
import static org.lwjgl.openxr.XR10.xrGetSystem;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRSystem {

    private final long systemID;

    public OpenXRSystem(OpenXRInstance xrInstance) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer pl = stack.longs(0);
            xrGetSystem(
                    xrInstance.getXrInstance(),
                    XrSystemGetInfo.malloc(stack)
                            .type$Default()
                            .next(NULL)
                            .formFactor(XR_FORM_FACTOR_HEAD_MOUNTED_DISPLAY),
                    pl
            );

            systemID = pl.get(0);
            if (systemID == 0) {
                throw new IllegalStateException("No compatible headset detected");
            }
            System.out.printf("Headset found with System ID: %d\n", systemID);
        }
    }

    public long getSystemID() {
        return systemID;
    }

}
