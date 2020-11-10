
(ns respo-md.main
  (:require [respo.core :refer [render! clear-cache! realize-ssr!]]
            [respo-md.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [respo-md.schema :as schema]
            [respo.cursor :refer [update-states]]
            ["highlight.js/lib/core" :as hljs]
            ["highlight.js/lib/languages/clojure" :as clojure-lang]
            ["highlight.js/lib/languages/bash" :as bash-lang]
            [respo-md.config :as config]))

(defonce *store (atom schema/store))

(defn dispatch! [op op-data]
  (let [next-store (if (= op :states) (update-states @*store op-data) @*store)]
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
  (println "Running mode:" (if config/dev? "dev" "release"))
  (.registerLanguage hljs "clojure" clojure-lang)
  (.registerLanguage hljs "bash" bash-lang)
  (if ssr? (render-app! realize-ssr!))
  (render-app! render!)
  (add-watch *store :changes (fn [] (render-app! render!)))
  (println "App started!"))

(defn reload! [] (clear-cache!) (render-app! render!) (println "Code update."))
