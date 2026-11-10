(ns irving.v2.shader
  (:require [irving.v2.byte-buffer-utils :as bbu])
  (:import
    [org.lwjgl.opengl GL20]))

(defn- log-shader-error
  [shader-id]
  (println "Failed to compile shader")
  (println (GL20/glGetShaderInfoLog shader-id))
  false)

(defn- shader-compiled?
  [shader-id]
  (if (= (GL20/glGetShaderi shader-id GL20/GL_COMPILE_STATUS)
         GL20/GL_FALSE)
    (log-shader-error shader-id)
    true))

(defn- compile-shader
  [shader-id]
  (GL20/glCompileShader shader-id)
  (shader-compiled? shader-id))

(defn create-shader
  [vert frag]
  (let [program (GL20/glCreateProgram)
        vert-id (GL20/glCreateShader GL20/GL_VERTEX_SHADER)
        frag-id (GL20/glCreateShader GL20/GL_FRAGMENT_SHADER)]
    (GL20/glShaderSource vert-id vert)
    (GL20/glShaderSource frag-id frag)
    (when (compile-shader vert-id)
      (when (compile-shader frag-id)
        (GL20/glAttachShader program vert-id)
        (GL20/glAttachShader program frag-id)
        (GL20/glLinkProgram program)
        (GL20/glValidateProgram program)
        (GL20/glDeleteShader vert-id)
        (GL20/glDeleteShader frag-id)
        program))))

(defn load-shader
  [vert-f frag-f]
  (let [vert (slurp vert-f)
        frag (slurp frag-f)]
    (create-shader vert frag)))

(defn enable-shader
  [program-id]
  (GL20/glUseProgram program-id))

(defn disable-shader
  [program-id]
  (GL20/glUseProgram 0))

(defn get-uniform
  [program-id name]
  ;; may need to cache and print error
  (GL20/glGetUniformLocation program-id name))

(defn set-uniform-1i
  [program-id name value]
  (GL20/glUniform1i (get-uniform program-id name) value))

(defn set-uniform-1f
  [program-id name value]
  (GL20/glUniform1f (get-uniform program-id name) value))

(defn set-uniform-2f
  [program-id name x y]
  (GL20/glUniform2f (get-uniform program-id name) x y))

(defn set-uniform-3f
  [program-id name {:keys [x y z]}]
  (GL20/glUniform3f (get-uniform program-id name) x y z))

(defn set-uniform-matrix-4f
  [program-id name matrix]
  (GL20/glUniformMatrix4fv (get-uniform program-id name)
                           false
                           (bbu/vec->float-buffer matrix)))