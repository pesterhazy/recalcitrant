(ns demo.brepl
  (:require [weasel.repl :as repl]))

(defonce conn
  (when-not (repl/alive?)
    (js/console.log "Connecting...")
    (repl/connect "ws://localhost:9001")))

(if (repl/alive?)
  (js/console.log "alive"))
