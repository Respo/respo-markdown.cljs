
(set-env!
  :asset-paths #{"assets/"}
  :resource-paths #{"src" "polyfill"}
  :dependencies '[[org.clojure/clojure       "1.8.0"       :scope "test"]
                  [org.clojure/clojurescript "1.9.542"     :scope "test"]
                  [adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                  [adzerk/boot-reload        "0.4.13"      :scope "test"]
                  [cljsjs/highlight          "9.6.0-0"     :scope "test"]
                  [andare                    "0.5.0"       :scope "test"]
                  [mvc-works/hsl             "0.1.2"]
                  [respo/ui                  "0.1.8"]
                  [respo                     "0.3.38"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]])

(def +version+ "0.1.3")

(task-options!
  pom {:project     'respo/markdown
       :version     +version+
       :description "Respo Markdown converter"
       :url         "https://github.com/Respo/respo-markdown"
       :scm         {:url "https://github.com/Respo/respo-markdown"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(deftask dev []
  (comp
    (watch)
    (reload :on-jsload 'respo-markdown.main/on-jsload!
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target :no-clean true)))

(deftask build-advanced []
  (comp
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5
                             :pseudo-names true
                             :static-fns true
                             :parallel-build true
                             :optimize-constants true
                             :source-map true})
    (target :no-clean true)))

(deftask build []
  (comp
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
