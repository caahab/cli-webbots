(ns rsscl.core
	(:gen-class)
	(:require [net.cgrand.enlive-html :as html]
        	  [clj-http.client :as client]))
(import [java.io StringReader])

;; define your app data so that it doesn't get over-written on reload
(def h {"User-Agent" "Mozilla/5.0 (Windows NT 6.1;) Gecko/20100101 Firefox/13.0.1"}) ;;"Referer" "arsart"
(def sources ["http://www.ft.com/rss/home/uk" "http://www.startribune.com/rss/1557.xml" "http://www.lasvegassun.com/feeds/headlines/all"])

;;
(defn- loadTarget [target]
  (let [resp (client/get target {:headers h})
        page (html/html-resource (StringReader. (:body resp)))]
    page))
;
(defn- getRssData [page]
  (let [items (html/select (loadTarget page) [:item])]
  	(loop [x 0
	     	   data []]
  		(if (>= x (count items))
  			data
  			(let [item (nth items x)
  				    titel (:content (nth (html/select item [:title]) 0))
  				    link "";(html/text item)
  				    text (:content (nth (html/select item [:description]) 0))
        			date (:content (nth (html/select item [:pubDate]) 0))]
  				(recur (inc x) (conj data {:titel titel :text text :date date :link link})))))))

;;
(defn- displayRssFeeds [rssData]
  (loop [x 0]
    (if (>= x (count rssData))
      nil
      (let [feed (nth rssData x)
            titel (:titel feed)
            date (:date feed)
            text (:text feed)
            link (:link feed)
            _ (println "---------- RSS ------------")
            _ (println "Published on: " date)
            _ (println "Titel: "titel)
            _ (println "Description: " text)
            _ (println "###>>" link "<<###")]
        (recur (inc x))))))

;;
(defn- loadAndDisplayRssFeeds []
  (loop [x 0]
    (if (>= x (count sources))
      	nil
       	(do
          	(displayRssFeeds (getRssData (nth sources x)))
          	(recur (inc x))))))

;;
(defn -main [& args]
  (loadAndDisplayRssFeeds))
