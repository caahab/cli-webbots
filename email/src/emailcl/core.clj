(ns emailcl.core
  (:gen-class)
  (:require [postal.core :refer :all]
            [clojure-mail.core :refer :all]
            [clojure-mail.gmail :as gmail]
            [clojure-mail.message :refer (read-message)]))


;;//##### RECEIVE #####//
(try 
  (let [gstore (gmail/store "user@gmail.com" "password")
        inbox-messages (inbox gstore)
        latest (read-message (first inbox-messages))]
    (:subject latest)
    (keys latest))
  (catch Exception e (println e)))

;;//##### SEND #####//
(try 
  (send-message { :from "me@draines.com"
                  :to ["mom@example.com" "dad@example.com"]
                  :cc "bob@example.com"
                  :subject "Hi!"
                  :body "Test."
                  :X-Tra "Something else"})
  (catch Exception e (println e)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
