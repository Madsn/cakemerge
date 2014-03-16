(ns mergecake.models.schema
  (:require [clojure.java.jdbc :as sql]
            [noir.io :as io]))

(def db-store "site")

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname (str "db/" db-store)
              :user "sa"
              :password ""
              :make-pool? true
              :naming {:keys clojure.string/lower-case
                       :fields clojure.string/upper-case}})
(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new java.io.File (str "db/" db-store ".h2.db"))))

(defn create-users-table
  []
  (sql/with-connection db-spec
    (sql/create-table
      :users
      [:id "BIGINT auto_increment PRIMARY KEY"]
      [:projectid "BIGINT"]
      [:name "varchar(30)"]
      [:initials "varchar(6)"])))

(defn create-cakedays-table
  []
  (sql/with-connection db-spec
    (sql/create-table
      :cakedays
      [:id "BIGINT auto_increment PRIMARY KEY"]
      [:userid "BIGINT"]
      [:projectid "BIGINT"]
      [:date "DATE"]
      [:description "varchar(500)"])))

(defn create-projects-table
  []
  (sql/with-connection db-spec
    (sql/create-table
      :projects
      [:id "BIGINT auto_increment PRIMARY KEY"]
      [:projectname "varchar(30)"])))

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-projects-table)
  (create-users-table)
  (create-cakedays-table))
