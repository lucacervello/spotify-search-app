(ns spotify-search-app.components
  (:require [spotify-search-app.api :as api]))

;; ------------------------
;; Utils

(defn search-value [this app-state]
  (let [value (-> this .-target .-value)]
    (if (zero? (count value))
      (reset! app-state nil)
      (api/get-artists value app-state))))

;; -------------------------
;; Components

(defn search [app-state]
  [:div.input-group
   [:span.input-group-btn
    [:input.form-control {:type "text"
                          :placeholder "Search an artist"
                          :on-change #(search-value % app-state)}]]])

(defn list [app-state]
  [:ul.list-group
   (for [item (:items (:artists @app-state))]
     ^{:keys (:id item)} [:a.list-group-item {:href (str "#/artist/" (:id item))}
                                        ;(:name item)
                                        ;[:img {:src (-> item :images first :url)}]
                                        ;[:span.badge (:popularity item)]
                          [card item]
                          ])])

(defn card [item]
  (let [img-url (if (seq (:images item))
              (-> item :images first :url)
              "http://icons.iconarchive.com/icons/artcore-illustrations/artcore-4/512/spotify-icon.png")]
    [:div.media
     [:div.media-left
      [:img.media-object {:src img-url
                          :width 64
                          :height 64}]]
     [:div.media-body [:p.media-heading (:name item)]]]))
