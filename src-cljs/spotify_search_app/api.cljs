(ns spotify-search-app.api
  (:require [ajax.core :refer [GET POST]]
            [goog.string :as gstring]
            [goog.string.format]))

(defn spotify-url [query type]
  (gstring/format "https://api.spotify.com/v1/search?q=%s&type=%s" query type))

(defn spotify-url-track [id]
  (str "https://api.spotify.com/v1/tracks/" id))

(defn spotify-url-artist [id]
  (str "https://api.spotify.com/v1/artists/" id))

(defn get-info [name type app-state]
  (GET (spotify-url name type)
       :response-format :json
       :keywords? true
       :handler (fn [response]
                  (reset! app-state response))))

(defn get-artist [id app-state]
  (GET (spotify-url-artist id)
       :response-format :json
       :keywords? true
       :handler #(reset! app-state %)))

(defn get-track [id app-state]
  (GET (spotify-url-track id)
       :response-format :json
       :keywords? true
       :handler #(reset! app-state %)))

(defn get-tracks [name-track app-state]
  (get-info name-track "track" app-state))

(defn get-artists [name-artist app-state]
  (get-info name-artist "artist" app-state))

(defn get-tracks-and-artists [name app-state]
  (get-info name "track,artist" app-state))
