(ns mergecake.db.preload.raw
  (:require clojure.java.io
            [mergecake.models.db :as db]
            [mergecake.models.schema :as schema]))

(defn slurp-file [filename]
  (slurp 
    (str (System/getProperty "user.dir") "/db/preload/raw/" filename) 
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
      (create-project-from-string entry))))

;(parse-project-string-from-html-section "<option value=\"994455\">994455 Test Project æøå</option>")

(db/reset-db)
;(schema/create-tables)

(load-projects-from-file "projects-from-dts.txt")
