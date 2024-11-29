package org.davidCMs.engine.openxr;

import org.davidCMs.engine.window.GLFWWindow;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL30.GL_MAJOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_MINOR_VERSION;
import static org.lwjgl.openxr.EXTDebugUtils.*;
import static org.lwjgl.openxr.KHROpenGLEnable.xrGetOpenGLGraphicsRequirementsKHR;
import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRSession {

    private final XrSession xrSession;
    private XrDebugUtilsMessengerEXT xrDebugMessenger;

    public OpenXRSession(OpenXRInstance xrInstance, OpenXRSystem system, GLFWWindow window) {
        try (MemoryStack stack = stackPush()) {

            XrGraphicsRequirementsOpenGLKHR graphicsRequirements = XrGraphicsRequirementsOpenGLKHR.malloc(stack)
                    .type$Default()
                    .next(NULL)
                    .minApiVersionSupported(0)
                    .maxApiVersionSupported(0);

            xrGetOpenGLGraphicsRequirementsKHR(xrInstance.getXrInstance(), system.getSystemID(), graphicsRequirements);

            int minMajorVersion = XR_VERSION_MAJOR(graphicsRequirements.minApiVersionSupported());
            int minMinorVersion = XR_VERSION_MINOR(graphicsRequirements.minApiVersionSupported());

            int maxMajorVersion = XR_VERSION_MAJOR(graphicsRequirements.maxApiVersionSupported());
            int maxMinorVersion = XR_VERSION_MINOR(graphicsRequirements.maxApiVersionSupported());

            System.out.println("The OpenXR runtime supports OpenGL " + minMajorVersion + "." + minMinorVersion
                    + " to OpenGL " + maxMajorVersion + "." + maxMinorVersion);

            if (maxMajorVersion < 4) {
                throw new UnsupportedOperationException("This application requires at least OpenGL 4.0");
            }
            int majorVersionToRequest = 4;
            int minorVersionToRequest = 0;

            if (minMajorVersion == 4) {
                minorVersionToRequest = 5;
            }

            int actualMajorVersion = glGetInteger(GL_MAJOR_VERSION);
            int actualMinorVersion = glGetInteger(GL_MINOR_VERSION);

            if (minMajorVersion > actualMajorVersion || (minMajorVersion == actualMajorVersion && minMinorVersion > actualMinorVersion)) {
                throw new IllegalStateException(
                        "The OpenXR runtime supports only OpenGL " + minMajorVersion + "." + minMinorVersion +
                                " and later, but got OpenGL " + actualMajorVersion + "." + actualMinorVersion
                );
            }

            if (actualMajorVersion > maxMajorVersion || (actualMajorVersion == maxMajorVersion && actualMinorVersion > maxMinorVersion)) {
                throw new IllegalStateException(
                        "The OpenXR runtime supports only OpenGL " + maxMajorVersion + "." + minMajorVersion +
                                " and earlier, but got OpenGL " + actualMajorVersion + "." + actualMinorVersion
                );
            }

            PointerBuffer pp = stack.mallocPointer(1);
            xrCreateSession(
                    xrInstance.getXrInstance(),
                    XRHelper.createGraphicsBindingOpenGL(
                            XrSessionCreateInfo.malloc(stack)
                                    .type$Default()
                                    .next(NULL)
                                    .createFlags(0)
                                    .systemId(system.getSystemID()),
                            stack,
                            window.getWindow(),
                            xrInstance.isUseEglGraphicsBinding()
                    ),
                    pp
            );

            xrSession = new XrSession(pp.get(0), xrInstance.getXrInstance());

            if (!xrInstance.isMissingXrDebug() && !xrInstance.isUseEglGraphicsBinding()) {
                XrDebugUtilsMessengerCreateInfoEXT ciDebugUtils = XrDebugUtilsMessengerCreateInfoEXT.calloc(stack)
                        .type$Default()
                        .messageSeverities(
                                XR_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT |
                                        XR_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT |
                                        XR_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT
                        )
                        .messageTypes(
                                XR_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
                                        XR_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
                                        XR_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT |
                                        XR_DEBUG_UTILS_MESSAGE_TYPE_CONFORMANCE_BIT_EXT
                        )
                        .userCallback((messageSeverity, messageTypes, pCallbackData, userData) -> {
                            XrDebugUtilsMessengerCallbackDataEXT callbackData = XrDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                            System.out.println("XR Debug Utils: " + callbackData.messageString());
                            return 0;
                        });

                System.out.println("Enabling OpenXR debug utils");
                xrCreateDebugUtilsMessengerEXT(xrInstance.getXrInstance(), ciDebugUtils, pp);
                xrDebugMessenger = new XrDebugUtilsMessengerEXT(pp.get(0), xrInstance.getXrInstance());
            }
        }
    }

    public XrSession getXrSession() {
        return xrSession;
    }

    public XrDebugUtilsMessengerEXT getXrDebugMessenger() {
        return xrDebugMessenger;
    }
}
