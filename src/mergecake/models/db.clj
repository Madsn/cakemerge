(ns mergecake.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [mergecake.models.schema :as schema]
            [clojure.java.io :refer :all]
            [clojure-csv.core :refer :all]))

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

(defn get-user-with-initials [initials]
  (first (select users
                 (where {:initials initials})
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

;; PRELOADING DATA

(defn create-project-with-id [{:keys [id projectname]}]
  (insert projects
          (values {:id id
                   :projectname projectname})))

(defn parse-csv-to-map 
  "https://github.com/mihi-tr/csv-map
   parses a csv to a map
   ([csv & {:as opts}])
   passes options to clojure-csv
   "
  [csv columns & {:as opts}]
  (let [opts (vec (reduce concat (vec opts)))
        c (apply parse-csv csv opts)]
  (map (partial zipmap columns) (rest c))))

(defn slurp-preload-file [filename]
  (slurp (str (System/getProperty "user.dir") "/db/preload/" filename)))

(defn load-users-from-file []
  (doseq [line (parse-csv-to-map (slurp-preload-file "users.txt") [:initials :uname])]
    (create-user line)))

(defn load-projects-from-file []
  (doseq [line (parse-csv-to-map (slurp-preload-file "projects.txt") [:id :projectname])]
    (create-project-with-id line)))

(defn reset-db []
  (do
    (delete cakedays)
    (delete users)
    (delete projects)
    (load-projects-from-file)
    (load-users-from-file)
    "DB reset completed"))
    