
(ns respo-md.render
  (:require [respo.render.html :refer [make-string]]
            [shell-page.core :refer [make-page spit slurp]]
            [respo-md.comp.container :refer [comp-container]]
            [respo-md.schema :as schema]
            [cljs.reader :refer [read-string]]
            ["highlight.js" :as hljs]))

(def base-info
  {:title "Markdown",
   :icon "http://cdn.tiye.me/logo/respo.png",
   :inline-styles [(slurp "./node_modules/highlight.js/styles/dark.css")
                   (slurp "./entry/main.css")],
   :ssr nil})

(defn dev-page [] (make-page "" (merge base-info {:styles [], :scripts ["/main.js"]})))

(defn highligher [code lang]
  (let [result (.highlight hljs lang code)]
    (comment .log js/console "Result" result code lang hljs)
    (.-value result)))

(def preview? (= "preview" js/process.env.prod))

(defn prod-page []
  (let [html-content (make-string (comp-container schema/store highligher))
        assets (read-string (slurp "dist/assets.edn"))
        cdn (if preview? "" " http://cdn.tiye.me/respo-md/")
        prefix-cdn (fn [x] (str cdn x))]
    (make-page
     html-content
     (merge
      base-info
      {:styles [], :scripts (map #(-> % :output-name prefix-cdn) assets), :ssr "respo-ssr"}))))

(defn main! []
  (if (= js/process.env.env "dev")
    (spit "target/index.html" (dev-page))
    (spit "dist/index.html" (prod-page))))
