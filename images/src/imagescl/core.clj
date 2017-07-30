(ns imagescl.core
	(:gen-class)
	(:refer-clojure :exclude [name parents])
	(:require [net.cgrand.enlive-html :as html]
			  [me.raynes.fs :refer :all]
        [clj-http.client :as client])
	)
(import [java.net URL])


;;
(defn- loadPage [url]
  (let [html (html/html-resource (URL. url))]
    html))
;
(defn- getImageSources []
  	(loop [x 0
           images (html/select (loadPage "https://www.pinterest.com/pin/239183430188569979/" ) [:img])
           data []]
        	(if (>= x (count images))
         		data
           		(recur (inc x) images (conj data (:src (:attrs (nth images x)))) ))))

;;
(defn- downloadImages [adr directory]
  (if (exists? directory)
      false
      (let [content (client/get (.toString adr) {:as :byte-array})]
        (with-open [w (clojure.java.io/output-stream directory)]
          (.write w (:body content)))
        true)))

;;
(defn- createDirectories [directories]
  (loop [x 0
         path "./images"]
    (if (< x (- (count directories) 1))
      (let [part (nth directories x)
            here (exists? (str path "/" part))
            _ (if (not here) (mkdir (str path "/" part)))
            ]
        (recur (inc x) (str path "/" part)))
      0)))

;;
(defn- download [images]
    (loop [x 0 downloads 0]
          (if (>= x (count images))
            downloads
            (let [adr (URL. (nth images x))
                  directories (split (.getFile adr))
                  _ (createDirectories directories)
                  dw (downloadImages adr (str "./images/" (.getFile adr)))]
              (recur (inc x) (if dw (inc downloads) downloads))))))

;;
(defn -main [& args]
  (let [images (getImageSources)
  		dwImages (download images)]
    (println "Downloaded " dwImages " images." )))

