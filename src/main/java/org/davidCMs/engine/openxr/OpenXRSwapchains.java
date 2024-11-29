package org.davidCMs.engine.openxr;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB10_A2;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.openxr.KHROpenGLEnable.XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_KHR;
import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class OpenXRSwapchains {

    public static class Swapchain {
        XrSwapchain                      handle;
        int                              width;
        int                              height;
        XrSwapchainImageOpenGLKHR.Buffer images;
    }

    private long glColorFormat;
    private final XrView.Buffer views;
    private Swapchain[] swapchains;
    private final XrViewConfigurationView.Buffer viewConfigs;
    private final int viewConfigType = XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO;

    public OpenXRSwapchains(OpenXRInstance xrInstance, OpenXRSystem system, OpenXRSession xrSession) {
        try (MemoryStack stack = stackPush()) {
            XrSystemProperties systemProperties = XrSystemProperties.calloc(stack)
                    .type$Default();
            xrGetSystemProperties(xrInstance.getXrInstance(), system.getSystemID(), systemProperties);

            System.out.printf("Headset name:%s vendor:%d \n",
                    memUTF8(memAddress(systemProperties.systemName())),
                    systemProperties.vendorId());

            XrSystemTrackingProperties trackingProperties = systemProperties.trackingProperties();
            System.out.printf("Headset orientationTracking:%b positionTracking:%b \n",
                    trackingProperties.orientationTracking(),
                    trackingProperties.positionTracking());

            XrSystemGraphicsProperties graphicsProperties = systemProperties.graphicsProperties();
            System.out.printf("Headset MaxWidth:%d MaxHeight:%d MaxLayerCount:%d \n",
                    graphicsProperties.maxSwapchainImageWidth(),
                    graphicsProperties.maxSwapchainImageHeight(),
                    graphicsProperties.maxLayerCount());

            IntBuffer pi = stack.mallocInt(1);

            xrEnumerateViewConfigurationViews(xrInstance.getXrInstance(), system.getSystemID(), viewConfigType, pi, null);
            viewConfigs = XRHelper.fill(
                    XrViewConfigurationView.calloc(pi.get(0)),
                    XrViewConfigurationView.TYPE,
                    XR_TYPE_VIEW_CONFIGURATION_VIEW
            );

            xrEnumerateViewConfigurationViews(xrInstance.getXrInstance(), system.getSystemID(), viewConfigType, pi, viewConfigs);
            int viewCountNumber = pi.get(0);

            views = XRHelper.fill(
                    XrView.calloc(viewCountNumber),
                    XrView.TYPE,
                    XR_TYPE_VIEW
            );

            if (viewCountNumber > 0) {
                xrEnumerateSwapchainFormats(xrSession.getXrSession(), pi, null);
                LongBuffer swapchainFormats = stack.mallocLong(pi.get(0));
                xrEnumerateSwapchainFormats(xrSession.getXrSession(), pi, swapchainFormats);

                long[] desiredSwapchainFormats = {
                        GL_RGB10_A2,
                        GL_RGBA16F,
                        GL_RGBA8,
                        GL31.GL_RGBA8_SNORM
                };

                out:
                for (long glFormatIter : desiredSwapchainFormats) {
                    for (int i = 0; i < swapchainFormats.limit(); i++) {
                        if (glFormatIter == swapchainFormats.get(i)) {
                            glColorFormat = glFormatIter;
                            break out;
                        }
                    }
                }

                if (glColorFormat == 0) {
                    throw new IllegalStateException("No compatable swapchain / framebuffer format availible");
                }

                swapchains = new Swapchain[viewCountNumber];
                for (int i = 0; i < viewCountNumber; i++) {
                    XrViewConfigurationView viewConfig = viewConfigs.get(i);

                    Swapchain swapchainWrapper = new Swapchain();

                    XrSwapchainCreateInfo swapchainCreateInfo = XrSwapchainCreateInfo.malloc(stack)
                            .type$Default()
                            .next(NULL)
                            .createFlags(0)
                            .usageFlags(XR_SWAPCHAIN_USAGE_SAMPLED_BIT | XR_SWAPCHAIN_USAGE_COLOR_ATTACHMENT_BIT)
                            .format(glColorFormat)
                            .sampleCount(viewConfig.recommendedSwapchainSampleCount())
                            .width(viewConfig.recommendedImageRectWidth())
                            .height(viewConfig.recommendedImageRectHeight())
                            .faceCount(1)
                            .arraySize(1)
                            .mipCount(1);

                    PointerBuffer pp = stack.mallocPointer(1);
                    xrCreateSwapchain(xrSession.getXrSession(), swapchainCreateInfo, pp);

                    swapchainWrapper.handle = new XrSwapchain(pp.get(0), xrSession.getXrSession());
                    swapchainWrapper.width = swapchainCreateInfo.width();
                    swapchainWrapper.height = swapchainCreateInfo.height();

                    xrEnumerateSwapchainImages(swapchainWrapper.handle, pi, null);
                    int imageCount = pi.get(0);

                    XrSwapchainImageOpenGLKHR.Buffer swapchainImageBuffer = XRHelper.fill(
                            XrSwapchainImageOpenGLKHR.calloc(imageCount),
                            XrSwapchainImageOpenGLKHR.TYPE,
                            XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_KHR
                    );

                    xrEnumerateSwapchainImages(swapchainWrapper.handle, pi, XrSwapchainImageBaseHeader.create(swapchainImageBuffer));
                    swapchainWrapper.images = swapchainImageBuffer;
                    swapchains[i] = swapchainWrapper;
                }
            }
        }
    }

    public long getGlColorFormat() {
        return glColorFormat;
    }

    public XrView.Buffer getViews() {
        return views;
    }

    public Swapchain[] getSwapchains() {
        return swapchains;
    }

    public XrViewConfigurationView.Buffer getViewConfigs() {
        return viewConfigs;
    }

    public int getViewConfigType() {
        return viewConfigType;
    }
}
