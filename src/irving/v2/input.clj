(ns irving.v2.input
  (:import
    [org.lwjgl.glfw GLFW]))

(defonce state
  (atom {:keys (vec (repeat 350 false))}))


(defn key-callback
  [window key scancode action mods]
  (swap! state #(assoc-in %
                          [:keys key]
                          (not= action GLFW/GLFW_RELEASE))))

(defn key-pressed?
  [key]
  (-> @state
      :keys
      (get key)))