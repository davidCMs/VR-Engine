/*
 * Code borrowed and spilt up form
 * https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/openxr/HelloOpenXRGL.java
 * license https://www.lwjgl.org/license
 * */

package org.davidCMs.engine.openxr;

import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openxr.EXTDebugUtils.XR_EXT_DEBUG_UTILS_EXTENSION_NAME;
import static org.lwjgl.openxr.KHROpenGLEnable.XR_KHR_OPENGL_ENABLE_EXTENSION_NAME;
import static org.lwjgl.openxr.MNDXEGLEnable.XR_MNDX_EGL_ENABLE_EXTENSION_NAME;

import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRInstance {

    private boolean missingXrDebug, useEglGraphicsBinding;
    private final XrInstance xrInstance;

    public OpenXRInstance() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);

            xrEnumerateInstanceExtensionProperties((ByteBuffer)null, pi, null);
            int numExtensions = pi.get(0);

            XrExtensionProperties.Buffer properties = XRHelper.prepareExtensionProperties(stack, numExtensions);

            xrEnumerateInstanceExtensionProperties((ByteBuffer)null, pi, properties);

            System.out.printf("OpenXR loaded with %d extensions:%n", numExtensions);

            boolean missingOpenGL = true;
            missingXrDebug = true;

            useEglGraphicsBinding = false;
            for (int i = 0; i < numExtensions; i++) {
                XrExtensionProperties prop = properties.get(i);

                String extensionName = prop.extensionNameString();
                System.out.println("    -" + extensionName);

                if (extensionName.equals(XR_KHR_OPENGL_ENABLE_EXTENSION_NAME)) {
                    missingOpenGL = false;
                }
                if (extensionName.equals(XR_EXT_DEBUG_UTILS_EXTENSION_NAME)) {
                    missingXrDebug = false;
                }
                if (extensionName.equals(XR_MNDX_EGL_ENABLE_EXTENSION_NAME)) {
                    useEglGraphicsBinding = true;
                }
            }

            if (missingOpenGL) {
                throw new IllegalStateException("OpenXR library does not provide required extension: " + XR_KHR_OPENGL_ENABLE_EXTENSION_NAME);
            }

            if (useEglGraphicsBinding) {
                System.out.println("Going to use cross-platform experimental EGL for session creation");
            } else {
                System.out.println("Going to use platform-specific session creation");
            }

            PointerBuffer extensions = stack.mallocPointer(2);
            extensions.put(stack.UTF8(XR_KHR_OPENGL_ENABLE_EXTENSION_NAME));
            if (useEglGraphicsBinding) {
                extensions.put(stack.UTF8(XR_MNDX_EGL_ENABLE_EXTENSION_NAME));
            } else if (!missingXrDebug) {
                extensions.put(stack.UTF8(XR_EXT_DEBUG_UTILS_EXTENSION_NAME));
            }
            extensions.flip();
            System.out.println("----------------------------------------------------");

            boolean useValidationLayer = false;

            xrEnumerateApiLayerProperties(pi, null);
            int numLayers = pi.get(0);

            XrApiLayerProperties.Buffer pLayers = XRHelper.prepareApiLayerProperties(stack, numLayers);
            xrEnumerateApiLayerProperties(pi, pLayers);
            System.out.println(numLayers + " XR layers are available:");
            for (int index = 0; index < numLayers; index++) {
                XrApiLayerProperties layer = pLayers.get(index);

                String layerName = layer.layerNameString();
                System.out.println(layerName);

                if (!useEglGraphicsBinding && layerName.equals("XR_APILAYER_LUNARG_core_validation")) {
                    useValidationLayer = true;
                }
            }
            System.out.println("----------------------------------------------------");

            PointerBuffer wantedLayers;
            if (useValidationLayer) {
                wantedLayers = stack.callocPointer(1);
                wantedLayers.put(0, stack.UTF8("XR_APILAYER_LUNARG_core_validation"));
                System.out.println("Enabling XR core validation");
            } else {
                System.out.println("Running without validation layers");
                wantedLayers = null;
            }

            XrInstanceCreateInfo createInfo = XrInstanceCreateInfo.malloc(stack)
                    .type$Default()
                    .next(NULL)
                    .createFlags(0)
                    .applicationInfo(XrApplicationInfo.calloc(stack)
                            .applicationName(stack.UTF8("Game"))
                            .engineName(stack.UTF8("MokerCug-Engine"))
                            .apiVersion(XR_API_VERSION_1_0))
                    .enabledApiLayerNames(wantedLayers)
                    .enabledExtensionNames(extensions);

            PointerBuffer pp = stack.mallocPointer(1);
            System.out.println("Creating OpenXR instance...");
            xrCreateInstance(createInfo, pp);
            xrInstance = new XrInstance(pp.get(0), createInfo);
            System.out.println("Created OpenXR instance");
        }
    }

    public boolean isMissingXrDebug() {
        return missingXrDebug;
    }

    public boolean isUseEglGraphicsBinding() {
        return useEglGraphicsBinding;
    }

    public XrInstance getXrInstance() {
        return xrInstance;
    }
}
