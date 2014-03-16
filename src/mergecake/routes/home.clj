(ns mergecake.routes.home
  (:use compojure.core)
  (:require [mergecake.views.layout :as layout]
            [mergecake.util :as util]
            [mergecake.models.db :as db]
            [mergecake.models.schema :as schema]))

(defn trigger-table-creation []
  (schema/create-tables))

(defn register-page []
  (layout/render "register.html" {:users (db/get-all-users)}))

(defn add-user-page []
  (layout/render "add-user.html"))

(defn list-users-page []
  (layout/render "list-users.html" {:users (db/get-all-users)}))

(defn list-cakedays-page []
  (layout/render "list-cakedays.html" {:cakedays (db/get-all-cakedays)}))

(defn render-register-cakeday [message]
  (layout/render "register.html" {:message message, :users (db/get-all-users)}))

(defn render-cakeday-receipt [cakeday]
  (layout/render "receipt.html" {:cakeday cakeday}))

(defn parse-date [date]
  (.parse (java.text.SimpleDateFormat. "dd-MM-yyyy") date))

(defn submit-cakeday [{:keys [user date description]}]
  (if (db/cakeday-taken? (parse-date date))
    (render-register-cakeday "Sorry, there was a conflict. Choose another day.")
    (do
      (db/create-cakeday {:user user :date (parse-date date) :description description})
      (render-cakeday-receipt (db/get-cakeday-by-date (parse-date date))))))

(defn submit-add-user [user]
  (do
    (db/create-user user)
    (layout/render "add-user.html" {:message "TEST"})))

(defn delete-user [id]
  (do
    (db/delete-user id)
    (list-users-page)))

(defn delete-cakeday [id]
  (do
    (db/delete-cakeday id)
    (list-cakedays-page)))

(defroutes home-routes
  (GET "/" [] (register-page))
  (POST "/submit-cakeday" {params :params} (submit-cakeday params))
  (GET "/add-user" [] (add-user-page))
  (POST "/add-user" {params :params} (submit-add-user params))
  (GET "/list-users" [] (list-users-page))
  (GET "/list-cakedays" [] (list-cakedays-page))
  (GET "/create-tables" [] (trigger-table-creation))
  (GET "/users/delete/:id" [id] (delete-user id))
  (GET "/cakedays/delete/:id" [id] (delete-cakeday id)))
