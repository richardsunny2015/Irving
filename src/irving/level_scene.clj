(ns irving.level-scene
  (:require [irving.scene :as scene]
            [irving.key-listener :as key-listener]))

(defrecord LevelScene [changing-scene time-to-change]
  scene/Scene
  (update-scene [this duration]
    (cond )))

(defn init
  []
  (map->LevelScene {:changing-scene false
                    :time-to-change 2.0}))