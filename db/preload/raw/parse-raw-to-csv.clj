(ns mergecake.db.preload.raw
  (:require clojure.java.io
            [mergecake.models.db :as db]))

(defn slurp-file [filename]
  (slurp (str (System/getProperty "user.dir") "/db/preload/raw/" filename)))

(defn parse-project-map-from-string [projectstring]
  (let [vals (clojure.string/split projectstring #" ")]
    (db/create-project-with-id 
      {:id (first vals) 
       :projectname (str (clojure.string/join " " (rest vals)))})))

(defn parse-project-string-from-html-section [section]
  (doseq [line (clojure.string/split section #"</option>|<option value=\"\d+\">")]
    (if (empty? line)
      ()
      (parse-project-map-from-string line))))

;;(parse-project-string-from-html-section "<option value=\"994455\">994455 Test Project æøå</option>")

;;(db/reset-db)