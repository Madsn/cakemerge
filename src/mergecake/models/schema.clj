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
      [:id "varchar(20) PRIMARY KEY"]
      [:first_name "varchar(30)"]
      [:last_name "varchar(30)"]
      [:initials "varchar(6)"])))

(defn create-cakedays-table
  []
  (sql/with-connection db-spec
    (sql/create-table
      :cakedays
      [:id "varchar(20) PRIMARY KEY"]
      [:user "varchar(20) FOREIGN KEY users"]
      [:date "DATE"]
      [:description "varchar(500)"])))

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-users-table)
  (create-cakedays-table))
