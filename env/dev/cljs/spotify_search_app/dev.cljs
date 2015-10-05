(ns ^:figwheel-no-load spotify-search-app.app
  (:require [spotify-search-app.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :on-jsload core/mount-components)

(core/init!)
