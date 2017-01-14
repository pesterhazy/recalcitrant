(require '[cljs.build.api :as b]
         '[cljs.repl :as repl]
         '[weasel.repl.websocket :as w]
         '[clojure.java.shell :refer [sh]]
         '[cljs.repl.browser :as browser])

(cljs.build.api/build "src"
                      {:main 'demo.app
                       :output-to "out/app.js"
                       :output-dir "out"
                       :verbose true})

(println "Opening browser...")

(sh "open" "--background" "index.html")

(println "Starting REPL...")

(repl/repl (w/repl-env)
           :output-dir "out")

(println "Done")
(System/exit 0)
