(require '[cljs.build.api :as b]
         '[cljs.repl :as repl]
         '[weasel.repl.websocket :as w]
         '[clojure.java.shell :refer [sh]]
         '[cljs.repl.browser :as browser])

(cljs.build.api/build "src"
                      {:main 'mies-demo.core
                       :output-to "out/mies_demo.js"
                       :output-dir "out"
                       :verbose true})

(sh "open" "--background" "index.html")

(repl/repl (w/repl-env)
           :output-dir "out")
