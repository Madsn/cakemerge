(ns mergecake.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [mergecake.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity projects)

(defentity users
  (belongs-to projects {:fk :projectid}))

(defentity cakedays
  (belongs-to users {:fk :userid})
  (belongs-to projects {:fk :projectid}))

;; PROJECTS

(defn create-project [{:keys [pname]}]
  (if (empty?
        (select projects
                (where {:projectname pname})))
    (insert projects
            (values {:projectname pname}))))

(defn update-project [id pname]
  (update projects
          (set-fields {:projectname pname})
          (where {:id id})))

(defn get-project [id]
  (first (select projects
                 (where {:id id})
                 (limit 1))))

(defn delete-project [id]
  (do
    (delete cakedays
            (where {:projectid id}))
    (delete projects
            (where {:id id}))))

(defn get-all-projects []
  (select projects))

;; USERS

(defn create-user [{:keys [uname initials proj]}]
  (let [user {:name uname
              :projectid proj
              :initials initials}]
    (insert users
            (values user))))

(defn update-user [id uname initials proj]
  (update users
          (set-fields {:name uname
                       :projectid proj
                       :initials initials})
          (where {:id id})))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

(defn delete-user [id]
  (do
    (delete cakedays
            (where {:userid id}))
    (delete users
            (where {:id id}))))

(defn get-all-users []
  (select users))

;; CAKEDAYS

(defn create-cakeday [cakeday]
  (insert cakedays
          (values cakeday)))

(defn update-cakeday [id user date description proj]
  (update users
          (set-fields {:userid user
                       :date date
                       :projectid proj
                       :description description})
          (where {:id id})))

(defn get-cakeday [id]
  (first (select cakedays
                 (where {:id id})
                 (limit 1))))

(defn get-cakeday-by-date [date]
  (first (select cakedays
                 (with users)
                 (where {:date date})
                 (limit 1))))

(defn get-all-cakedays []
  (select cakedays (with users)
          (with projects)))

(defn cakeday-taken? [date proj]
  (not (empty? (select cakedays
                       (where {:date date
                               :projectid proj})))))

(defn delete-cakeday [id]
  (delete cakedays
          (where {:id id})))
