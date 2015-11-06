(ns spotify-search-app.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [spotify-search-app.api :as api]
            [spotify-search-app.components :as comp])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li {:class (when (= page (session/get :page)) "active")}
   [:a {:href uri
        :on-click #(reset! collapsed? true)}
    title]])

(defn navbar []
  (let [collapsed? (atom true)]
    (fn []
      [:nav.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:button.navbar-toggle
          {:class         (when-not @collapsed? "collapsed")
           :data-toggle   "collapse"
           :aria-expanded @collapsed?
           :aria-controls "navbar"
           :on-click      #(swap! collapsed? not)}
          [:span.sr-only "Toggle Navigation"]
          [:span.icon-bar]
          [:span.icon-bar]
          [:span.icon-bar]]
         [:a.navbar-brand {:href "#/"} "spotify-search-app"]]
        [:div.navbar-collapse.collapse
         (when-not @collapsed? {:class "in"})
         [:ul.nav.navbar-nav
          [nav-link "#/" "Home" :home collapsed?]
          [nav-link "#/about" "About" :about collapsed?]]]]])))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     "this is the story of spotify-search-app... work in progress"]]])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to spotify-search-app"]
    [:p "Time to start building your site!"]
    [:p [:a.btn.btn-primary.btn-lg {:href "http://luminusweb.net"} "Learn more »"]]]
   [:div.row
    [:div.col-md-12
     [:h2 "Welcome to ClojureScript"]]]
   (when-let [docs (session/get :docs)]
     [:div.row
      [:div.col-md-12
       [:div {:dangerouslySetInnerHTML
              {:__html (md->html docs)}}]]])])

(def app-state (atom nil))
(def track-state (atom nil))
(def artist-state (atom nil))

(defn app []
  [:div.container
   [:div.row
    [:div.col-md-3]
    [:div.col-md-6
     [:div.input-group
      [comp/search app-state]
      [:span.input-group-btn
       [:button.btn.btn-default {:type "button"} "Go"]]]]
    [:div.col-md-3]]
   [:div.row
    [:div.col-md-3]
    [:div.col-md-6
     [comp/list app-state]]
    [:div.col-md-3]]])

(defn track []
  [:div.container
   [:div.jumbotron
    [:div.container
     [:h1 (:name @track-state)]
     (for [artist (:artists @track-state)]
       [:h4 [:a {:href (str "#/artist/" (:id artist))} (:name artist)]])
     [:iframe {:src (str "https://embed.spotify.com/?uri=spotify:track:"
                         (:id @track-state))
               :width 300 :height 80
               :frameborder 0 :allowtransparency true}]]]])

(defn artist []
  [:div.container
   [:div.jumbotron
    [:div.container
     [:h1 (:name @artist-state)]]]])

(def pages
  {:home #'home-page
   :about #'about-page
   :app #'app
   :track #'track
   :artist #'artist})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/home" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

(secretary/defroute "/" []
  (session/put! :page :app))

(secretary/defroute "/track/:id" [id]
  (do
    (reset! app-state nil)
    (api/get-track id track-state)
    (session/put! :page :track)))

(secretary/defroute "/artist/:id" [id]
  (do
    (reset! app-state nil)
    (api/get-artist id artist-state)
    (session/put! :page :artist)))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          EventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))

(defn mount-components []
  (reagent/render [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
