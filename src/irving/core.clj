(ns irving.core
  (:require [irving.v2.input :as input])
  (:import
    [org.lwjgl.glfw Callbacks GLFW]
    [org.lwjgl.opengl GL GL11]
    [org.lwjgl.system MemoryStack]))

(defonce state
  (atom {:running true
         :width 1280
         :height 720}))

(defn- update-game
  []
  (GLFW/glfwPollEvents)
  (when (input/key-pressed? GLFW/GLFW_KEY_SPACE)
    (println "FLAP!")))

(defn- render
  [window]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT))
  (GLFW/glfwSwapBuffers window))

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

(defn- init
  []
  (when (not (GLFW/glfwInit))
    (throw (IllegalStateException. "Unable to initialize GLFW")))
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (let [window (GLFW/glfwCreateWindow (:width @state) (:height @state) "Flappy" 0 0)]
    (when (nil? window)
      (throw (RuntimeException. "Failed to create window!")))
    (GLFW/glfwSetKeyCallback window input/key-callback)
    (get-window-size window)
    (GLFW/glfwMakeContextCurrent window)
    (GLFW/glfwSwapInterval 1)
    (GLFW/glfwShowWindow window)
    window))

(defn- run-engine
  []
  (let [window (init)]
    (GL/createCapabilities)
    (while (:running @state)
      (update-game)
      (render window)
      (when (GLFW/glfwWindowShouldClose window)
        (swap! state #(assoc % :running false))))
    (Callbacks/glfwFreeCallbacks window)
    (GLFW/glfwDestroyWindow window)
    (GLFW/glfwTerminate)
    (.free (GLFW/glfwSetErrorCallback nil))))

(defn- start
  []
  (.start (Thread. run-engine)))

(defn -main
  []
  (println "Here goes nothing")
  (start))