(ns mergecake.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [mergecake.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity users)

(defn create-user [{:keys [uname initials]}]
  (let [user {:name uname
             :initials initials}]
  (insert users
          (values user))))

(defn update-user [id uname initials]
  (update users
  (set-fields {:name uname
               :initials initials})
  (where {:id id})))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

(defn delete-user [id]
  (delete users
          (where {:id id})))

(defn get-all-users []
  (select users))


(defentity cakedays
  (belongs-to users {:fk :user}))

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

(defn get-all-cakedays []
  (select cakedays (with users)))

(defn cakeday-taken? [date]
  (not (empty? (select cakedays (where {:date date})))))

(defn delete-cakeday [id]
  (delete cakedays
          (where {:id id})))
