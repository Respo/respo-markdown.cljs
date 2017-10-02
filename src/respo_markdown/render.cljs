
(ns respo-markdown.render
  (:require [respo.render.html :refer [make-string]]
            [shell-page.core :refer [make-page spit slurp]]
            [respo-markdown.comp.container :refer [comp-container]]
            [respo-markdown.schema :as schema]))

(def base-info
  {:title "Markdown", :icon "http://logo.respo.site/respo.png", :ssr nil, :inner-html nil})

(defn dev-page []
  (make-page
   ""
   (merge
    base-info
    {:styles [],
     :scripts ["https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/highlight.min.js"
               "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/languages/clojure.min.js"
               "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/languages/bash.min.js"
               "/main.js"
               "/browser/lib.js"
               "/browser/main.js"]})))

(def hljs (js/require "highlight.js"))

(defn highligher [code lang]
  (let [result (.highlight hljs lang code)]
    (comment .log js/console "Result" result code lang hljs)
    (.-value result)))

(def preview? (= "preview" js/process.env.prod))

(defn prod-page []
  (let [html-content (make-string (comp-container schema/store highligher))
        manifest (.parse js/JSON (slurp "dist/assets-manifest.json"))
        cljs-manifest (.parse js/JSON (slurp "dist/manifest.json"))
        cdn (if preview? "" " http://repo-cdn.b0.upaiyun.com/respo-markdown/")
        prefix-cdn (fn [x] (str cdn x))]
    (make-page
     html-content
     (merge
      base-info
      {:styles [(prefix-cdn (aget manifest "main.css"))],
       :scripts (map
                 prefix-cdn
                 [(aget manifest "main.js")
                  (-> cljs-manifest (aget 0) (aget "js-name"))
                  (-> cljs-manifest (aget 1) (aget "js-name"))]),
       :ssr "respo-ssr"}))))

(defn main! []
  (if (= js/process.env.env "dev")
    (spit "target/index.html" (dev-page))
    (spit "dist/index.html" (prod-page))))
