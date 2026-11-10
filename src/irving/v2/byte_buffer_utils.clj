(ns irving.v2.byte-buffer-utils
  (:import [java.nio ByteBuffer ByteOrder]))

(defn byte-array->byte-buffer
  [ba]
  (doto (-> (ByteBuffer/allocateDirect (count ba))
            (.order (ByteOrder/nativeOrder)))
    (.put ba)
    (.flip)))

(defn vec->float-buffer
  [float-vec]
  (let [a (float-array float-vec)]
    (doto (-> (ByteBuffer/allocateDirect (* 4 (count float-vec)))
              (.order (ByteOrder/nativeOrder))
              (.asFloatBuffer))
      (.put a)
      (.flip))))

(defn vec->int-buffer
  [int-vec]
  (let [a (int-array int-vec)]
    (doto (-> (ByteBuffer/allocateDirect (* 4 (count int-vec)))
              (.order (ByteOrder/nativeOrder))
              (.asIntBuffer))
      (.put a)
      (.flip))))