(ns spotify-search-app.app
  (:require [spotify-search-app.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
