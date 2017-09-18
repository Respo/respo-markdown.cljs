
(set-env!
  :resource-paths #{"src"}
  :dependencies '[[mvc-works/hsl  "0.1.2"]
                  [respo/ui       "0.1.9"]])

(def +version+ "0.1.4")

(deftask build []
  (comp
    (pom :project     'respo/markdown
         :version     +version+
         :description "Respo Markdown converter"
         :url         "https://github.com/Respo/respo-markdown"
         :scm         {:url "https://github.com/Respo/respo-markdown"}
         :license     {"MIT" "http://opensource.org/licenses/mit-license.php"})
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
