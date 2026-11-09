(ns irving.scene)

;; this might be a proto and all scenes use the same impl
;; via record

(defprotocol Scene
 (update-scene [this duration]))