(ns mergecake.routes.home
  (:use compojure.core)
  (:require [mergecake.views.layout :as layout]
            [mergecake.util :as util]
            [mergecake.models.db :as db]
            [mergecake.models.schema :as schema]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn register-page []
  (layout/render "register.html" {:users (db/get-all-users)}))

(defn get-user-from-id [id]
  "MIKMA")

(defn submit-cakeday [{:keys [user-id date description]}]
  (layout/render "register.html" {:message (str (get-user-from-id user-id) date description)}))

(defn submit-add-user [{:keys [first-name last-name initials]}]
  (layout/render "register.html" {:message (str (get-user-from-id user-id) date description)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/register" [] (register-page))
  (POST "/submit-cakeday" {params :params} (submit-cakeday params))
  (GET "/add-user" [] (add-user-page))
  (POST "/add-user" {params :params} (submit-add-user params)))
