package org.davidCMs.engine.openxr;

import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.openxr.XR10.XR_REFERENCE_SPACE_TYPE_LOCAL;
import static org.lwjgl.openxr.XR10.xrCreateReferenceSpace;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRReferenceSpace {

    private final XrSpace space;

    public OpenXRReferenceSpace(OpenXRSession session) {
        try (MemoryStack stack = stackPush()) {
            PointerBuffer pp = stack.mallocPointer(1);

            xrCreateReferenceSpace(
                    session.getXrSession(),
                    XrReferenceSpaceCreateInfo.malloc(stack)
                            .type$Default()
                            .next(NULL)
                            .referenceSpaceType(XR_REFERENCE_SPACE_TYPE_LOCAL)
                            .poseInReferenceSpace(XrPosef.malloc(stack)
                                    .orientation(XrQuaternionf.malloc(stack)
                                            .x(0)
                                            .y(0)
                                            .z(0)
                                            .w(1))
                                    .position$(XrVector3f.calloc(stack))),
                    pp
            );

            space = new XrSpace(pp.get(0), session.getXrSession());
        }
    }
}
