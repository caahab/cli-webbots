(ns server.core
  (:gen-class)
  (:require [org.httpkit.server :refer :all]
            [clojure.data.json :as json]))


(defn- getParameterArray "separate &" [params]
  [])
(defn- getKeyValues "separate =" [params]
  {})
(defn- deconstruct [body]
  (let [params (getParameterArray body)]
  	(loop [x 0
          vParams {}]
     (if (>= x (count params))
       vParams
       (recur (inc x) (conj vParams (getKeyValues (nth params x))))))))

;;
;;
(defmulti buildResponse (fn [x vParam] (:uri x)))
(defmethod buildResponse "/task" [req params]
  	(let [_ (println (deconstruct params))]
	  {:status 200
	   :headers {"Content-Type" "json"}
	   :body (json/write-str {:cmd "exec" :prg "scrap_image"})}))
;
(defmethod buildResponse :default [req params]
  	(let [_ (println deconstruct params)]
		{:status  404
		 :headers {"Content-Type" "json"}
	 	 :body    (json/write-str {:cmd "idl"})}))

;;
;;
(defn async-handler [req]
  	(with-channel req channel
        (on-receive channel (fn [data]
            (println "Received data: " data)))
		(let [_ (println "Client request received")
        		_ (println "Requested link: " (:uri req))
        		vParam (.readLine (clojure.java.io/reader (.bytes (:body req))))]
	  		(send! channel (buildResponse req vParam)))))

;;
;;
(defn -main [& args]
  	(println "Start server")
	(run-server async-handler {:port 8000}))
