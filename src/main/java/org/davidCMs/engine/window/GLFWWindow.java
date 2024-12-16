package org.davidCMs.engine.window;

import org.joml.Vector2f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.davidCMs.engine.utils.GLFWUtils.ToGLFWBool;
import static org.davidCMs.engine.utils.GLFWUtils.fromGLFWBool;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The {@code GLFWWindow} class is an encapsulation of a GLFW window witch provides methods for manipulating the
 * encapsulated window without needing to use any GLFW functions.
 *
 * @author davidCMs
 * @since 0.0.1
 */
public class GLFWWindow {

    /** The handle to the GLFW window.
     * @since 0.0.1
     */
    private final long window;

    /** List of {@code GLFWKeyCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWKeyCallbackI> keyCallbacks = new ArrayList<>();
    /** List of {@code GLFWCharCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCharCallbackI> charCallbacks = new ArrayList<>();
    /** List of {@code GLFWDropCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWDropCallbackI> dropCallbacks = new ArrayList<>();
    /** List of {@code GLFWPreeditCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWPreeditCallbackI> preeditCallbacks = new ArrayList<>();
    /** List of {@code GLFWScrollCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWScrollCallbackI> scrollCallbacks = new ArrayList<>();
    /** List of {@code GLFWCharModsCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCharModsCallbackI> charModsCallbacks = new ArrayList<>();
    /** List of {@code GLFWCursorEnterCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCursorEnterCallbackI> cursorEnterCallbacks = new ArrayList<>();
    /** List of {@code GLFWCursorPosCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCursorPosCallbackI> cursorPosCallbacks = new ArrayList<>();
    /** List of {@code GLFWFramebufferSizeCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWFramebufferSizeCallbackI> framebufferSizeCallbacks = new ArrayList<>();
    /** List of {@code GLFWMouseButtonCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWMouseButtonCallbackI> mouseButtonCallbacks = new ArrayList<>();
    /** List of {@code GLFWPreeditCandidateCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWPreeditCandidateCallbackI> preeditCandidateCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowCloseCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowCloseCallbackI> windowCloseCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowFocusCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowFocusCallbackI> windowFocusCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowIconifyCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowIconifyCallbackI> windowIconifyCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowMaximizeCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowMaximizeCallbackI> windowMaximizeCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowPosCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowPosCallbackI> windowPosCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowRefreshCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowRefreshCallbackI> windowRefreshCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowSizeCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowSizeCallbackI> windowSizeCallbacks = new ArrayList<>();
    /** List of {@code GLFWWindowContentScaleCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowContentScaleCallbackI> windowContentScaleCallbacks = new ArrayList<>();

    /** Main constructor responsible for creating the window.
     *
     * @param width The width of the window to be created.
     * @param height The height of the window to be created.
     * @param title The title of the window to be created.
     * @param makeContext Whenever or not the window should hold the context for OpenGL.
     *
     * @since 0.0.1
     */
    public GLFWWindow(int width, int height, String title, boolean makeContext) {

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        setupCallbacks();

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        if (makeContext)
            makeContext();
    }

    //todo write java doc
    private void setupCallbacks() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            for (GLFWKeyCallbackI cb : keyCallbacks)
                cb.invoke(window, key, scancode, action, mods);
        });
        glfwSetCharCallback(window, (window, codepoint) -> {
            for (GLFWCharCallbackI cb : charCallbacks)
                cb.invoke(window, codepoint);
        });
        glfwSetDropCallback(window, (window, count, names) ->  {
            for (GLFWDropCallbackI cb : dropCallbacks)
                cb.invoke(window, count, names);
        });
        glfwSetPreeditCallback(window, (window, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret) ->  {
            for (GLFWPreeditCallbackI cb : preeditCallbacks)
                cb.invoke(window, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret);
        });
        glfwSetScrollCallback(window, (window, xoffset, yoffset) ->  {
            for (GLFWScrollCallbackI cb : scrollCallbacks)
                cb.invoke(window, xoffset, yoffset);
        });
        glfwSetCharModsCallback(window, (window, codepoint, mods) ->  {
            for (GLFWCharModsCallbackI cb : charModsCallbacks)
                cb.invoke(window, codepoint, mods);
        });
        glfwSetCursorEnterCallback(window, (window, entered) -> {
            for (GLFWCursorEnterCallbackI cb : cursorEnterCallbacks)
                cb.invoke(window, entered);
        });
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            for (GLFWCursorPosCallbackI cb : cursorPosCallbacks)
                cb.invoke(window, xpos, ypos);
        });
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            for (GLFWFramebufferSizeCallbackI cb : framebufferSizeCallbacks)
                cb.invoke(window, width, height);
        });
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            for (GLFWMouseButtonCallbackI cb : mouseButtonCallbacks)
                cb.invoke(window, button, action, mods);
        });
        glfwSetPreeditCandidateCallback(window, (window, candidates_count, selected_index, page_start, page_size) -> {
            for (GLFWPreeditCandidateCallbackI cb : preeditCandidateCallbacks)
                cb.invoke(window, candidates_count, selected_index, page_start, page_size);
        });
        glfwSetWindowCloseCallback(window, window -> {
            for (GLFWWindowCloseCallbackI cb : windowCloseCallbacks)
                cb.invoke(window);
        });
        glfwSetWindowFocusCallback(window, (window, focused) -> {
            for (GLFWWindowFocusCallbackI cb : windowFocusCallbacks)
                cb.invoke(window, focused);
        });
        glfwSetWindowIconifyCallback(window, (window, iconified) -> {
            for (GLFWWindowIconifyCallbackI cb : windowIconifyCallbacks)
                cb.invoke(window, iconified);
        });
        glfwSetWindowMaximizeCallback(window, (window, maximized) -> {
            for (GLFWWindowMaximizeCallbackI cb : windowMaximizeCallbacks)
                cb.invoke(window, maximized);
        });
        glfwSetWindowPosCallback(window, (window, xpos, ypos) -> {
            for (GLFWWindowPosCallbackI cb : windowPosCallbacks)
                cb.invoke(window, xpos, ypos);
        });
        glfwSetWindowRefreshCallback(window, window -> {
            for (GLFWWindowRefreshCallbackI cb : windowRefreshCallbacks)
                cb.invoke(window);
        });
        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            for (GLFWWindowSizeCallbackI cb : windowSizeCallbacks)
                cb.invoke(window, width, height);
        });
        glfwSetWindowContentScaleCallback(window, (window, xscale, yscale) -> {
            for (GLFWWindowContentScaleCallbackI cb : windowContentScaleCallbacks)
                cb.invoke(window, xscale, yscale);
        });
    }

    //todo write java doc
    public boolean isKeyPressed(int key) {
        switch (glfwGetKey(window, key)) {
            case GLFW_REPEAT, GLFW_PRESS -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /** @return Whenever or not the window has the OpenGL context.
     * @since 0.0.1
     */
    public boolean hasContext() {
        return glfwGetCurrentContext() == window;
    }

    /** Closes the window. */
    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    /** Makes the window have the OpenGL context.
     * @since 0.0.1
     */
    public void makeContext() {
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        Vector2f size = getSize();
        GL11.glViewport(0,0, (int) size.x, (int) size.y);
    }

    /** Enable/disable VSync on the window.
     *
     * @throws GLFWException if the window does not have the OpenGL context.
     *
     * @since 0.0.1
     */
    public void setVSync(boolean vSync) {
        if (!hasContext())
            throw new GLFWException("Current window does not hold the OpenGL context.");

        if (vSync) {
            glfwSwapInterval(1);
        }
        else {
            glfwSwapInterval(0);
        }
    }

    /** Swaps the front and back buffers.
     * @since 0.0.1
     */
    public void swapBuffers() {
        if (!hasContext())
            throw new GLFWException("Current window does not hold the OpenGL context.");

        glfwSwapBuffers(window);
    }

    //todo javadoc
    public GlfwEnums.CursorState getCursorState() {
        return GlfwEnums.CursorState.fromConstant(glfwGetInputMode(window, GLFW_CURSOR));
    }

    //todo javadoc
    public void setCursorState(GlfwEnums.CursorState state) {
        glfwSetInputMode(window, GLFW_CURSOR, state.getConstant());
    }

    //todo javadoc
    public void setCursorPosition(Vector2f pos) {
        glfwSetCursorPos(window, pos.x, pos.y);
    }

    //todo javadoc
    public Vector2f getCorsorPosition() {

        double[] x = new double[1];
        double[] y = new double[1];

        glfwGetCursorPos(window, x, y);

        return new Vector2f((float) x[0], (float) y[0]);
    }

    public long getHDC() {
        return GLFWNativeWGL.glfwGetWGLContext(window);
    }

    /** Adds a key callback, triggered when a keyboard key is pressed.
     *
     * @since 0.0.1
     */
    public void addKeyCallback(GLFWKeyCallbackI callback) {
        keyCallbacks.add(callback);
    }
    /** Adds a character callback, triggered when a character is input.
     *
     * @since 0.0.1
     */
    public void addCharCallback(GLFWCharCallbackI callback) {
        charCallbacks.add(callback);
    }

    /** Adds a drop callback, triggered when files are dropped onto the window.
     *
     * @since 0.0.1
     */
    public void addDropCallback(GLFWDropCallbackI callback) {
        dropCallbacks.add(callback);
    }

    /** Adds a character mods callback, triggered when a character with modifiers is input.
     *
     * @since 0.0.1
     */
    public void addCharModsCallback(GLFWCharModsCallbackI callback) {
        charModsCallbacks.add(callback);
    }

    /** Adds a preedit callback, triggered during text composition.
     *
     * @since 0.0.1
     */
    public void addPreeditCallback(GLFWPreeditCallbackI callback) {
        preeditCallbacks.add(callback);
    }

    /** Adds a scroll callback, triggered when the scroll wheel is used.
     *
     * @since 0.0.1
     */
    public void addScrollCallback(GLFWScrollCallbackI callback) {
        scrollCallbacks.add(callback);
    }

    /** Adds a cursor enter callback, triggered when the cursor enters or leaves the window.
     *
     * @since 0.0.1
     */
    public void addCursorEnterCallback(GLFWCursorEnterCallbackI callback) {
        cursorEnterCallbacks.add(callback);
    }

    /** Adds a cursor position callback, triggered when the cursor is moved.
     *
     * @since 0.0.1
     */
    public void addCursorPosCallback(GLFWCursorPosCallbackI callback) {
        cursorPosCallbacks.add(callback);
    }

    /** Adds a framebuffer size callback, triggered when the framebuffer size changes.
     *
     * @since 0.0.1
     */
    public void addFramebufferSizeCallback(GLFWFramebufferSizeCallbackI callback) {
        framebufferSizeCallbacks.add(callback);
    }

    /** Adds a mouse button callback, triggered when a mouse button is pressed or released.
     *
     * @since 0.0.1
     */
    public void addMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
        mouseButtonCallbacks.add(callback);
    }

    /** Adds a preedit candidate callback, triggered during text composition for candidates.
     *
     * @since 0.0.1
     */
    public void addPreeditCandidateCallback(GLFWPreeditCandidateCallbackI callback) {
        preeditCandidateCallbacks.add(callback);
    }

    /** Adds a window close callback, triggered when the window is requested to close.
     *
     * @since 0.0.1
     */
    public void addWindowCloseCallback(GLFWWindowCloseCallbackI callback) {
        windowCloseCallbacks.add(callback);
    }

    /** Adds a window focus callback, triggered when the window gains or loses focus.
     *
     * @since 0.0.1
     */
    public void addWindowFocusCallback(GLFWWindowFocusCallbackI callback) {
        windowFocusCallbacks.add(callback);
    }

    /** Adds a window iconify callback, triggered when the window is minimized or restored.
     *
     * @since 0.0.1
     */
    public void addWindowIconifyCallback(GLFWWindowIconifyCallbackI callback) {
        windowIconifyCallbacks.add(callback);
    }

    /** Adds a window maximize callback, triggered when the window is maximized or restored.
     *
     * @since 0.0.1
     */
    public void addWindowMaximizeCallback(GLFWWindowMaximizeCallbackI callback) {
        windowMaximizeCallbacks.add(callback);
    }

    /** Adds a window position callback, triggered when the window position changes.
     *
     * @since 0.0.1
     */
    public void addWindowPosCallback(GLFWWindowPosCallbackI callback) {
        windowPosCallbacks.add(callback);
    }

    /** Adds a window refresh callback, triggered when the window content needs to be redrawn.
     *
     * @since 0.0.1
     */
    public void addWindowRefreshCallback(GLFWWindowRefreshCallbackI callback) {
        windowRefreshCallbacks.add(callback);
    }

    /** Adds a window size callback, triggered when the window size changes.
     *
     * @since 0.0.1
     */
    public void addWindowSizeCallback(GLFWWindowSizeCallbackI callback) {
        windowSizeCallbacks.add(callback);
    }

    /** Adds a window content scale callback, triggered when the content scale of the window changes.
     *
     * @since 0.0.1
     */
    public void addWindowContentScaleCallback(GLFWWindowContentScaleCallbackI callback) {
        windowContentScaleCallbacks.add(callback);
    }




    /** Shows the window.
     * @since 0.0.1
     */
    public void show() {
        glfwShowWindow(window);
    }
    /** Hides the window.
     * @since 0.0.1
     */
    public void hide() {
        glfwHideWindow(window);
    }
    /** @return True if the window is visible else returns false.
     * @since 0.0.1
     */
    public boolean getVisible() {
        return fromGLFWBool(glfwGetWindowAttrib(window, GLFW_VISIBLE));
    }
    /** @param visible Sets the visibility of the window.
     * @since 0.0.1
     */
    public void setVisible(boolean visible) {
        glfwSetWindowAttrib(window, GLFW_VISIBLE, ToGLFWBool(visible));
    }

    /**
     * @param title Sets the title of the window.
     * @since 0.0.1
     * */
    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    /** @param resizable Sets the resizability of the window.
     * @since 0.0.1
     */
    public void setResizable(boolean resizable) {
        glfwSetWindowAttrib(window, GLFW_RESIZABLE, ToGLFWBool(resizable));
    }
    /** @return True if the window is resizable else returns false.
     * @since 0.0.1
     */
    public boolean isResizable() {
        return fromGLFWBool(glfwGetWindowAttrib(window, GLFW_RESIZABLE));
    }

    /** @param position Sets the position of the window.
     * @since 0.0.1
     */
    public void setPosition(Vector2f position) {
        glfwSetWindowPos(window, (int) position.x, (int) position.y);
    }
    /** @return The position of the window in a {@link Vector2f}.
     * @since 0.0.1
     */
    public Vector2f getPosition() {
        Vector2f position = new Vector2f();
        int[] x = new int[1], y = new int[1];
        glfwGetWindowPos(window, x,y);
        position.x = x[0];
        position.y = y[0];
        return position;
    }
    /** @param size Sets the size of the window, where {@link Vector2f#x} is the width, {@link Vector2f#y} is the height.
     * @since 0.0.1
     */
    public void setSize(Vector2f size) {
        glfwSetWindowSize(window, (int) size.x, (int) size.y);
        GL43.glViewport(0,0, (int) size.x, (int) size.y);
    }

    /** @return The size of the window in a {@link Vector2f}, where {@link Vector2f#x} is the width, {@link Vector2f#y} is the height.
     * @since 0.0.1
     */
    public Vector2f getSize() {
        Vector2f size = new Vector2f();
        int[] width = new int[1], height = new int[1];
        glfwGetWindowSize(window, width, height);
        size.x = width[0];
        size.y = height[0];
        return size;
    }

    public long getWindow() {
        return window;
    }
}