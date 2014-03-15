(ns mergecake.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [mergecake.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity users)

(defn create-user [user]
  (insert users
          (values user)))

(defn update-user [id first-name last-name initials]
  (update users
  (set-fields {:first_name first-name
               :last_name last-name
               :initials initials})
  (where {:id id})))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

(defn get-all-users []
  (select users))


(defentity cakedays)

(defn create-cakeday [cakeday]
  (insert cakedays
          (values cakeday)))

(defn update-cakeday [id user date description]
  (update users
  (set-fields {:user user
               :date date
               :description description})
  (where {:id id})))

(defn get-cakeday [id]
  (first (select cakedays
                 (where {:id id})
                 (limit 1))))
