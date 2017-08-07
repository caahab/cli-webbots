(ns imacro.core
  (:gen-class))
(import 'java.lang.Runtime)


;
(def macroHeader "##########################################
# Header (defaults, etc.)
##########################################
SET !TIMEOUT 240
SET !ERRORIGNORE YES
SET !EXTRACT_TEST_POPUP NO
FILTER TYPE=IMAGES STATUS=ON
CLEAR
TAB T=1
TAB CLOSEALLOTHERS")

;
(defn- createMacro "Do your macro creation magic here TODO" []
  (let [macroContent (str macroHeader
                          	 "WAIT 3
GOTO https://nodejs.org/api/child_process.html#child_process_child_process_spawn_command_args_options
WAIT 8
GOTO http://www.tropicalmba.com/getstartedqanda/")]
        (with-open [w (clojure.java.io/writer "bottask.iim")]
        	(.write w macroContent))))

;
(defn- executeMacro []
  (try
    (. (Runtime/getRuntime) exec "firefox http://run.imacros.net/?m=bottask.iim")
    (catch Exception e (println "Could not execute iMacro"))))

;
(defn -main [& args]
  (do
    (createMacro)
    (executeMacro)))
