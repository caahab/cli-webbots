(ns login.core
	(:gen-class)
	(:require [net.cgrand.enlive-html :as html]
        	  [clj-http.client :as client]))
(import [java.io StringReader])


;;
(defn- getTarget [target options]
  (let [resp (client/get target options)
        _ (println "Authentication request status: " (:status resp))]
    resp))
(defn- postTarget [target options]
  (let [resp (client/post target options)
        _ (println "Authentication request status: " (:status resp))]
    resp))

;;
;;
(defn- hasSession [res]
  (let [loc (:Location (:headers res))
        hasSession (.contains loc "session")]
    (if hasSession (println "Has session: " (.substring loc (inc (.indexOf loc "?")))) (println "No session contained"))))

;;
(defn- basicAuth []
  (let [target "http://www.WebbotsSpidersScreenScrapers.com/basic_authentication/index.php"
        base "http://www.WebbotsSpidersScreenScrapers.com/basic_authentication"
        res (getTarget target {:basic-auth "webbot:sp1der3"})]
    (println "Done basic")))

(defn- queryAuth []
  (let [target "http://www.WebbotsSpidersScreenScrapers.com/query_authentication/index.php"
        base "http://www.WebbotsSpidersScreenScrapers.com/query_authentication"
        res (postTarget target {:form-params {:enter "Enter" :username "webbot" :password "sp1der3"} :follow-redirects true :force-redirects false :headers {:Referer base}})]
    (hasSession res)
    (println "Done query")))

(defn- cookieAuth []
  (let [cs (clj-http.cookies/cookie-store)
        target "http://www.WebbotsSpidersScreenScrapers.com/cookie_authentication/index.php"
        base "http://www.WebbotsSpidersScreenScrapers.com/cookie_authentication"
        res (postTarget target {:form-params {:enter "Enter" :username "webbot" :password "sp1der3"} :cookie-store cs :follow-redirects true :force-redirects true :headers {:Referer base}})]
    (clojure.pprint/pprint (clj-http.cookies/get-cookies cs))
    (println "Done cookie")))


;;
(defn -main [& args]
  (basicAuth)
  (queryAuth)
  (cookieAuth))
