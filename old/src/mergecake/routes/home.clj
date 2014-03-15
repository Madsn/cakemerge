(ns mergecake.routes.home
  (:use compojure.core)
  (:require [mergecake.views.layout :as layout]
            [mergecake.util :as util]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn register-page []
  (layout/render "register.html"))

(defn get-user-from-id [id]
  (:initials (db/get-user id)))

(defn submit-cakeday [{:keys [user-id date description]}]
  (layout/render "register.html" {:message (str (get-user-from-id user-id) date description)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/register" [] (register-page))
  (POST "/submit-cakeday" {params :params} (submit-cakeday params)))
