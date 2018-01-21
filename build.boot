
(defn read-password [guide]
  (String/valueOf (.readPassword (System/console) guide nil)))

(set-env!
  :resource-paths #{"src" "polyfill"}
  :dependencies '[]
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                     :username "jiyinyiyong"
                                     :password (read-password "Clojars password: ")}]))

(def +version+ "0.1.8")

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
