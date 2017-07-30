(ns linkscl.core
	(:gen-class)
	(:require [net.cgrand.enlive-html :as html]
        	  [clj-http.client :as client]))
(import [java.net URL])

;; define your app data so that it doesn't get over-written on reload
(def target "http://www.webbotsspidersscreenscrapers.com/page_with_broken_links.php")
(def base "http://www.webbotsspidersscreenscrapers.com/")

;;
(defn- loadTarget []
  (let [page (html/html-resource (URL. target))]
    page))
;
(defn- getLinkSources []
  	(loop [x 0
           links (html/select (loadTarget) [:a])
           data []]
        	(if (>= x (count links))
         		data
           		(recur (inc x) links (conj data (:href (:attrs (nth links x)))) ))))

;;
(defn- resolveLink [base link]
  (let [_ (println "Url to resolve: " link)
        hasProtocol (.contains link "http")
        hasWWW (.contains link "www")]
    (if (and hasWWW (not hasProtocol))
      	(str "http://" link)
       	(if (and (not hasWWW) (not hasProtocol))
            (str base link)
            link))))
;
(defn- resolveLinks [vLinks]
  	(loop [x 0
           data []]
        	(if (>= x (count vLinks))
         		data
           		(recur (inc x) (conj data (resolveLink base (nth vLinks x)))) )))
;
(defn- testLinks [vLinks]
  	(loop [x 0]
    	(if (>= x (count vLinks))
     		nil
       		(let [req (try (client/get (nth vLinks x) {:refer base}) (catch clojure.lang.ExceptionInfo e {:status 501}))
               	  _ (println "###########")
               	  _ (print (:status req) "       ")
                  _ (println (nth vLinks x))]
           		(recur (inc x))))))

;;
(defn -main [& args]
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (let [links (getLinkSources)]
  	(testLinks (resolveLinks links))))
