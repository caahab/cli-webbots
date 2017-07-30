(ns searchcl.core
	(:gen-class)
	(:require [net.cgrand.enlive-html :as html]
        	  [clj-http.client :as client]))
(import [java.io StringReader])

;; define your app data so that it doesn't get over-written on reload
(def searchEngine "https://www.google.ch")
(def term "/search?q=webbots")
(def target "www.hackerrank.com")
(def h {"User-Agent" "Mozilla/5.0 (Windows NT 6.1;) Gecko/20100101 Firefox/13.0.1"}) ;;"Referer" "arsart"
(def pageCount (atom 10))

;;
(defn- loadTarget [dest]
  (let [resp (client/get dest {:headers h})
        page (html/html-resource (StringReader. (:body resp)))]
    page))
;
(defn- getLinkSources [page]
  	(loop [x 0
           links (html/select page [:a])
           data []]
        	(if (>= x (count links))
         		data
           		(let [ln (:href (:attrs (nth links x)))
                   	  hasCache (if ln (.contains ln "cache") true)
                      hasProtocol (if ln (.contains ln "http") false)
                      ]
               	(recur (inc x) links (if (and hasProtocol (not hasCache))(conj data (nth links x)) data) )))))

;
(defn- findTargetInLinks [links]
  (loop [x 0]
    (let [source (:href (:attrs (nth links x)))
          hasTarget (.contains source target)]
	    (if (and (< x (- (count links) 1)) (not hasTarget))
	        (recur (inc x))
         	x))))
;
(defn- getNextPage []
	(let [start "&start="
       	  end "&sa=N"
          link (str start @pageCount end)]
   		(reset! pageCount (+ @pageCount 10))
     	link))

;;
(defn- findPageRank [link]
  (loop [x 0
         notFound true
         searchTarget link]
    (if (or (not notFound) (= x 5))
      	(println (not notFound) " on page " x)
		(let [page (loadTarget searchTarget)
		      links (getLinkSources page)
        	nrLinks (- (count links) 1)
		      idx   (findTargetInLinks links)
		      followUp (getNextPage)]
			(println (- nrLinks idx) " of " nrLinks)
   			(recur (inc x) (= idx nrLinks) (str searchEngine term followUp))))))

;;
(defn -main [& args]
  (findPageRank (str searchEngine term)))
