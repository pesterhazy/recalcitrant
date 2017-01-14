(set-env! :resource-paths #{"src"}
          :dependencies '[]
          :repositories
          (partial map (fn [[k v]] [k (cond-> v (#{"clojars"} k) (assoc :username (System/getenv "CLOJARS_USER"),
                                                                        :password (System/getenv "CLOJARS_PASS")))])))

(task-options! pom
               {:project 'recalcitrant/recalcitrant
                :description "Reagent toolbox for building simple components",
                :url "https://github.com/pesterhazy/recalcitrant",
                :scm {:url "https://github.com/pesterhazy/recalcitrant"},
                :license {"MIT"
                          "https://github.com/pesterhazy/recalcitrant/blob/master/LICENSE"}})
(defn get-version []
  (read-string (slurp "release.edn")))

(defn format-version [{:keys [version]}]
  (clojure.string/join "." version))

(deftask bump
  []
  (with-pass-thru [_]
    (let [version (update-in (get-version) [:version 2] inc)]
      (boot.util/info "Bumped to version: %s\n" (format-version version))
      (spit "release.edn" version))))

(deftask print-version
  []
  (with-pass-thru [_]
    (boot.util/info "Current version: %s\n" (slurp "release.edn"))))

(deftask build [] (comp (pom :version (format-version (get-version))) (jar) (install)))

(deftask deploy
         []
         (comp (build)
               (push :repo
                     "clojars"
                     :gpg-sign
                     false)))

(deftask confirm []
  (with-pass-thru [_]
    (println)
    (print "Enter \"Yes\" (without the quotes) to continue: ")
    (flush)
    (when (not= (read-line) "Yes")
      (throw (ex-info "Interrupted" {})))))

(deftask release []
  (comp
   (print-version)
   (confirm)
   (deploy)))
