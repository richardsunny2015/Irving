(ns irving.v2.math)

;; MATRIX MATHS

(defn new-matrix
  []
  (vec (repeat (* 4 4) 0.0)))

(defn identity-matrix
  "Makes a diagonal line"
  []
  (let [matrix (new-matrix)]
    (-> matrix
        (assoc (+ 0 (* 0 4)) 1.0)
        (assoc (+ 1 (* 1 4)) 1.0)
        (assoc (+ 2 (* 2 4)) 1.0)
        (assoc (+ 3 (* 3 4)) 1.0))))

(defn orthographic-matrix
  [left right bottom top near far]
  (let [matrix (identity-matrix)]
    (-> matrix
        (assoc (+ 0 (* 0 4)) (/ 2.0 (- right left)))
        (assoc (+ 1 (* 1 4)) (/ 2.0 (- top bottom)))
        (assoc (+ 2 (* 2 4)) (/ 2.0 (- near far)))
        (assoc (+ 0 (* 3 4)) (/ (+ left right)
                                (- left right)))
        (assoc (+ 1 (* 3 4)) (/ (+ bottom top)
                                (- bottom top)))
        (assoc (+ 2 (* 3 4)) (/ (+ far near)
                                (- far near))))))

(defn translate-matrix
  [vector-3-f]
  (let [matrix (identity-matrix)]
    (-> matrix
        (assoc (+ 0 (* 3 4)) (:x vector-3-f))
        (assoc (+ 1 (* 3 4)) (:y vector-3-f))
        (assoc (+ 2 (* 3 4)) (:z vector-3-f)))))

(defn rotate-matrix
  [angle]
  (let [matrix (identity-matrix)
        radians (Math/toRadians angle)
        cos (Math/cos radians)
        sin (Math/sin radians)]
    (-> matrix
        (assoc (+ 0 (* 0 4)) cos)
        (assoc (+ 1 (* 0 4)) sin)
        (assoc (+ 0 (* 1 4)) (* -1 sin))
        (assoc (+ 1 (* 1 4)) cos))))

(defn multiply-matrix
  [m1 m2]
  (loop [result (new-matrix)
         y 0
         x 0]
    (if (= 4 y)
      result
      (let [e [0 1 2 3]
            x-indexes (->> (map #(+ x (* % 4)) e)
                           (into #{}))
            y-indexes (->> (map #(+ % (* y 4)) e)
                           (into #{}))
            xs (keep-indexed
                (fn [index n]
                  (when (contains? x-indexes index)
                    n))
                m1)
            ys (keep-indexed
                (fn [index n]
                  (when (contains? y-indexes index)
                    n))
                m2)
            products (map * xs ys)
            sum (reduce + 0 products)]
        (recur
         (assoc result (+ x (* y 4)) sum)
         (cond-> y
           (= 4 (inc x)) (inc))
         (mod (inc x) 4))))))