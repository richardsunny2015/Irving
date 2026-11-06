(ns irving.key-listener
  (:import
    [org.lwjgl.glfw GLFW]))

(defonce key-state
  (atom {:key-pressed (vec (repeat 350 false))}))

(defn key-callback
  [window key scancode action mods]
  (cond
    (= action GLFW/GLFW_PRESS)
    (swap! key-state
           #(assoc-in % [:key-pressed key] true))

    (= action GLFW/GLFW_RELEASE)
    (swap! key-state
           #(assoc-in % [:key-pressed key] false))))