(ns spotify-search-app.components
  (:require [spotify-search-app.api :as api]))

;; ------------------------
;; Utils

(defn search-value [this app-state]
  (let [value (-> this .-target .-value)]
    (if (zero? (count value))
      (reset! app-state nil)
      (api/get-tracks-and-artists value app-state))))

(def default-icon-url
  "http://icons.iconarchive.com/icons/artcore-illustrations/artcore-4/512/spotify-icon.png")

;; -------------------------
;; Components


(defn card [item]
  (let [img-url (if (seq (:images item))
                  (-> item :images first :url)
                  default-icon-url)]
    [:div.media
     [:div.media-left
      [:img.media-object {:src img-url
                          :width 64
                          :height 64}]]
     [:div.media-body [:p.media-heading (:name item)]]]))


(defn search [app-state]
  [:div.input-group
   [:span.input-group-btn
    [:input.form-control {:type "text"
                          :placeholder "Search an artist"
                          :on-change #(search-value % app-state)}]]])

(defn list [app-state]
  [:div
   (when-let [artist-items (seq (-> @app-state :artists :items))]
     [:div.col-md-6
      [:div.panel.panel-default
       [:div.panel-heading "Artists"]
       [:ul.list-group
        (for [item artist-items]
          ^{:keys (:id item)}
          [:a.list-group-item
           {:href (str "#/artist/" (:id item))}
           [card item]])]]])
   (when-let [track-items (seq (-> @app-state :tracks :items))]
     [:div.col-md-6
      [:div.panel.panel-default
       [:div.panel-heading "Tracks"]
       [:ul.list-group
        (for [item track-items]
          ^{:keys (:id item)}
          [:a.list-group-item
           {:href (str "#/track/" (:id item))}
           [card item]])]]])])
