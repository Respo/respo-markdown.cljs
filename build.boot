
(defn read-password [guide]
  (String/valueOf (.readPassword (System/console) guide nil)))

(set-env!
  :resource-paths #{"src"}
  :dependencies '[[mvc-works/hsl  "0.1.2"]
                  [respo/ui       "0.1.9"]]
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                     :username "jiyinyiyong"
                                     :password (read-password "Clojars password: ")}]))

(def +version+ "0.1.6")

(deftask deploy []
  (comp
    (pom :project     'respo/markdown
         :version     +version+
         :description "Respo Markdown converter"
         :url         "https://github.com/Respo/respo-markdown"
         :scm         {:url "https://github.com/Respo/respo-markdown"}
         :license     {"MIT" "http://opensource.org/licenses/mit-license.php"})
    (jar)
    (push :repo "clojars" :gpg-sign false)))
