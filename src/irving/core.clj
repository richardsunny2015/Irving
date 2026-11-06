(ns irving.core
  (:require [irving.mouse :as mouse])
  (:import [org.lwjgl Version]
           [org.lwjgl.glfw Callbacks GLFW GLFWErrorCallback]
           [org.lwjgl.opengl GL GLCapabilities GL11]
           [org.lwjgl.system MemoryStack]))

(defn get-window-size
  [window]
  (with-open [stack (MemoryStack/stackPush)]
    (let [p-width (.mallocInt stack 1)
          p-height (.mallocInt stack 1)
          _ (GLFW/glfwGetWindowSize window p-width p-height)
          vidmode (GLFW/glfwGetVideoMode (GLFW/glfwGetPrimaryMonitor))]
      (GLFW/glfwSetWindowPos window
                             (/ (- (.width vidmode) (.get p-width 0)) 2)
                             (/ (- (.height vidmode) (.get p-height 0)) 2)))))

(defn init
  []
  (-> System/err
      (GLFWErrorCallback/createPrint)
      (.set))
  (when (not (GLFW/glfwInit))
    (throw (IllegalStateException. "Unable to initialize GLFW")))
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (let [window (GLFW/glfwCreateWindow 300 300 "Hello World!" 0 0)]
    (when (nil? window)
      (throw (RuntimeException. "Failed to create window!")))
    (GLFW/glfwSetCursorPosCallback window mouse/mouse-pos-callback)
    (GLFW/glfwSetMouseButtonCallback window mouse/mouse-button-callback)
    (GLFW/glfwSetScrollCallback window mouse/mouse-scroll-callback)
    (GLFW/glfwSetKeyCallback
     window
     ;; may need to proxy this:
     (fn [window key _scancode action _mods]
       (when (and (= key GLFW/GLFW_KEY_ESCAPE) (= action GLFW/GLFW_RELEASE))
         (GLFW/glfwSetWindowShouldClose window true))
       nil))
    (get-window-size window)
    (GLFW/glfwMakeContextCurrent window)
    (GLFW/glfwSwapInterval 1)
    (GLFW/glfwShowWindow window)
    window))

(defn- render
  [window]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT))
  (GLFW/glfwSwapBuffers window)
  (GLFW/glfwPollEvents))

(defn game-loop
  [window]
  (GL/createCapabilities)
  (GL11/glClearColor 1.0 1.0 1.0 0.0)
  (while (not (GLFW/glfwWindowShouldClose window))
    (render window)))

(defn run
  []
  (let [window (init)]
    (game-loop window)
    (Callbacks/glfwFreeCallbacks window)
    (GLFW/glfwDestroyWindow window)

    (GLFW/glfwTerminate)
    (.free (GLFW/glfwSetErrorCallback nil))))

(defn -main
  []
  (println "Here goes nothing")
  (run))