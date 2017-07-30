(ns pricescl.core
  (:gen-class))
(require '(net.cgrand [enlive-html :as html]))
(import [java.net URL])

;;
(defn- loadPage [url]
  (let [html (html/html-resource (URL. url))]
    html))
;
(defn- getPriceFromRow [rows]
  (loop [x 0
         data []]
    	(if (>= x (count rows))
       		data
         	(let [columns (html/select (nth rows x) [:td])
                nrColumns (count columns)]
	         	(recur (inc x) (if (= nrColumns 7) (conj data {:name (html/text (nth columns 1))
                                                               	:price (html/text (nth columns 4))
                                                               	:condition (html/text (nth columns 2))
                                                               	:weight (html/text (nth columns 3))
                                                                :id (html/text (nth columns 0))}) data))))))
;
(defn- getPrices []
  (let [pages (html/select (loadPage "http://www.webbotsspidersscreenscrapers.com/buyair/" ) [:table])]
    (loop [x 0
           data []]
          (if (>= x (count pages))
            data
              (let [page (nth pages x)
                      hasData (and (.contains (html/text page) "Products For Sale") (not (.contains (html/text page) "Atmospheric Products")))]
                  (recur (inc x) (if hasData (conj data (getPriceFromRow (html/select page [:tr]))) data)))))))

;;
(defn -main [& args]
  (let [indices (getPrices)]
    (println indices)))
