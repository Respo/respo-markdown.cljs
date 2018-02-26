
(ns respo-md.main
  (:require [respo.core :refer [render! clear-cache! realize-ssr!]]
            [respo-md.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [respo-md.schema :as schema]
            [respo.cursor :refer [mutate]]
            ["highlight.js/lib/highlight" :as hljs]
            ["highlight.js/lib/languages/clojure" :as clojure-lang]
            ["highlight.js/lib/languages/bash" :as bash-lang]))

(defonce *store (atom schema/store))

(defn dispatch! [op op-data]
  (let [next-store (if (= op :states) (update @*store :states (mutate op-data)) @*store)]
    (reset! *store next-store)))

(defn highligher [code lang]
  (try
   (let [result (.highlight hljs lang code)]
     (comment .log js/console "Result" result code lang js/hljs)
     (.-value result))
   (catch js/Error e (.error js/console e) (str "<code>" code "</code>"))))

(def mount-target (.querySelector js/document ".app"))

(defn render-app! [renderer]
  (renderer mount-target (comp-container @*store highligher) dispatch!))

(def ssr? (some? (.querySelector js/document "meta.respo-ssr")))

(defn main! []
  (.registerLanguage hljs "clojure" clojure-lang)
  (.registerLanguage hljs "bash" bash-lang)
  (if ssr? (render-app! realize-ssr!))
  (render-app! render!)
  (add-watch *store :changes (fn [] (render-app! render!)))
  (println "App started!"))

(defn reload! [] (clear-cache!) (render-app! render!) (println "Code update."))

(set! (.-onload js/window) main!)
