(ns mergecake.routes.home
  (:use compojure.core)
  (:require [mergecake.views.layout :as layout]
            [mergecake.util :as util]
            [mergecake.models.db :as db]
            [mergecake.models.schema :as schema]))

(defn home-page []
    (layout/render
      "home.html" {:content (util/md->html "/md/docs.md")}))

(defn trigger-table-creation []
  (schema/create-tables))

(defn about-page []
  (layout/render "about.html"))

(defn register-page []
  (layout/render "register.html" {:users (db/get-all-users)}))

(defn add-user-page []
  (layout/render "add-user.html"))

(defn list-users-page []
  (layout/render "list-users.html" {:users (db/get-all-users)}))

(defn list-cakedays-page []
  (layout/render "list-cakedays.html" {:cakedays (db/get-all-cakedays)}))

(defn submit-cakeday [{:keys [user date description]}]
  (do
    (db/create-cakeday {:user user :date (.parse
      (java.text.SimpleDateFormat. "dd-MM-yyyy") date) :description description})
    (layout/render "register.html"
                 {:message (get (db/get-user user) :initials)
                  :users (db/get-all-users)})))

(defn submit-add-user [user]
  (do
    (db/create-user user)
    (layout/render "add-user.html" {:message "TEST"})))

(defn delete-user [id]
  (do
    (db/delete-user id)
    (list-users-page)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/register" [] (register-page))
  (POST "/submit-cakeday" {params :params} (submit-cakeday params))
  (GET "/add-user" [] (add-user-page))
  (POST "/add-user" {params :params} (submit-add-user params))
  (GET "/list-users" [] (list-users-page))
  (GET "/list-cakedays" [] (list-cakedays-page))
  (GET "/create-tables" [] (trigger-table-creation))
  (GET "/users/delete/:id" [id] (delete-user id)))
