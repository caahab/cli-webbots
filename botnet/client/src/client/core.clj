(ns client.core
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))


;;
(defn- getCommand [body]
  	(let [data (json/read-str body :key-fn keyword)]
     (:cmd data)))

;;
;;
(defmulti handleCommand (fn [x] (getCommand x)))
;
(defmethod handleCommand "exec" [args]
  (println "Execute task"))
;
(defmethod handleCommand "update" [args]
  (println "Update programm"))
;
(defmethod handleCommand :default [args]
  (println "Idle"))

;;
;;
(defn -main [& args]
	(let [response1 (http/post "http://localhost:8000/task" {:form-params {:version "1.0.5" :pass "arstart"}})]
	  ;; Handle responses one-by-one, blocking as necessary
	  ;; Other keys :headers :body :error :opts
		(println "Server response status: " (:status @response1))
		(handleCommand (:body @response1))))
