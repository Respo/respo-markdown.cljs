
(ns respo-markdown.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span textarea]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo-markdown.comp.md-article :refer [comp-md-article]]))

(defn update-state [state k v] (assoc state k v))

(def style-container {:align-items :stretch})

(defn init-state [& args] {:draft ""})

(def style-text {})

(defn render [store]
  (fn [state mutate!]
    (div
     {:style (merge ui/global ui/fullscreen ui/row style-container)}
     (textarea
      {:style (merge ui/textarea ui/flex style-text),
       :event {:input (fn [e dispatch!] (mutate! :draft (:value e)))},
       :attrs {:placeholder "Some markdown content", :value (:draft state)}})
     (comp-md-article (:draft state) {}))))

(def comp-container (create-comp :container init-state update-state render))
