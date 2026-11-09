(ns irving.core
  (:require [irving.window :as window]
            [irving.v2.input :as input])
  (:import
    [org.lwjgl.glfw GLFW]
    [org.lwjgl.system MemoryStack]))

(defonce state
  (atom {:running true
         :width 1280
         :height 720}))

(defn- update-game
  []
  (GLFW/glfwPollEvents)
  (println "Are we in here?")
  (when (input/key-pressed? GLFW/GLFW_KEY_SPACE)
    (println "FLAP!")))

(defn- render
  [window]
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
    (get-window-size window)
    (GLFW/glfwSetKeyCallback window input/key-callback)
    (GLFW/glfwMakeContextCurrent window)
    (GLFW/glfwSwapInterval 1)
    (GLFW/glfwShowWindow window)
    window))

(defn- run-engine
  []
  (let [window (init)]
    (while (:running @state)
      (update-game)
      (render window)
      (when-not (GLFW/glfwWindowShouldClose window)
        (swap! state #(assoc % :running false))))))

(defn- start
  []
  (.start (Thread. run-engine))
  #_(run-engine))

(defn -main
  []
  (println "Here goes nothing")
  #_(start)
  (window/run))