(ns mergecake.db.preload.raw
  (:require [clojure.java.io :as io]
            [mergecake.models.db :as db]
            [mergecake.models.schema :as schema]))

(defn get-file [filename]
  (str (System/getProperty "user.dir") "/db/preload/raw/" filename))

(defn slurp-file [filename]
  (slurp 
    (get-file filename)
    :encoding "ISO-8859-1"))

(defn create-project-from-string [projectstring]
  (let [vals (clojure.string/split projectstring #" ")]
    (db/create-project-with-id 
      {:id (first vals) 
       :projectname (str (clojure.string/join " " (rest vals)))})))

(defn load-projects-from-file [filename]
  (doseq [entry (clojure.string/split (slurp-file filename) #"</option>|<option value=\"\d+\">")]
    (if (empty? entry)
      ()
      (create-project-from-string (clojure.string/replace entry #"&amp;" "&")))))



(defn create-user-from-string [userstr]
  (let [splitstr (clojure.string/split userstr #",")]
    (db/create-user {:initials (clojure.string/upper-case (get splitstr 0)) 
                     :uname (get splitstr 1)})))

(defn load-users-from-file [filename]
  (with-open [rdr (io/reader (get-file filename))]
    (doseq [line (line-seq rdr)]
      (create-user-from-string line))))

;(db/reset-db)
(schema/create-tables)

(load-projects-from-file "projects-from-dts.txt")
(load-users-from-file "initials-and-names-from-sharepoint.txt")
