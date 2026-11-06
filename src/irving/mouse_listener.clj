(ns irving.mouse-listener
  (:import [org.lwjgl.glfw GLFW]))

(defonce mouse-state
  (atom {:scroll-x 0.0
         :scroll-y 0.0
         :x-pos 0.0
         :y-pos 0.0
         :last-x 0.0
         :last-y 0.0
         :is-dragging false
         :mouse-button-pressed (vec (repeat 3 false))}))

(defn mouse-pos-callback
  [window xpos ypos]
  (swap! mouse-state
         #(merge %
                 {:last-x (:x-pos @mouse-state)
                  :last-y (:y-pos @mouse-state)
                  :x-pos xpos
                  :y-pos ypos})))

(defn mouse-button-callback
  [window button action mods]
  (cond
    (= action GLFW/GLFW_PRESS)
    (when (< button (-> @mouse-state :mouse-button-pressed (count)))
      (swap! mouse-state
             #(assoc-in %
                        [:mouse-button-pressed button]
                        true)))

    (= action GLFW/GLFW_RELEASE)
    (when (< button (-> @mouse-state :mouse-button-pressed (count)))
     (swap! mouse-state
            (comp #(assoc-in %
                             [:mouse-button-pressed button]
                             false)
                  #(assoc % :is-dragging false))))))

(defn mouse-scroll-callback
  [window x-offset y-offset]
  (swap! mouse-state
         #(merge %
                 {:scroll-x x-offset
                  :scroll-y y-offset})))