(ns mergecake.db.preload.raw
  (:require [clojure.java.io :as io]
            [mergecake.models.schema :as schema]))

(defn get-raw-file [filename]
  (str (System/getProperty "user.dir") "/db/preload/raw/" filename))

(defn get-write-file [filename]
  (str (System/getProperty "user.dir") "/db/preload/" filename))

(defn slurp-file [filename]
  (slurp 
    (get-raw-file filename)
    :encoding "ISO-8859-1"))

(defn append-to-projects-file [line]
  (let [vals (clojure.string/split line #" ")]
    (if (not (empty? vals))
          (spit (str (get-write-file "projects.txt"))
                (str (first vals) "," (clojure.string/join " " (rest vals)) "\n") :append true))))

(defn load-projects-from-file [filename]
  (do
    (try 
       (io/delete-file (get-write-file "projects.txt"))
       (catch Exception e (str "Discarding exception: " (.getMessage e))))
    (doseq [entry (clojure.string/split (slurp-file filename) #"</option>|<option value=\"\d+\">")]
      (if (empty? entry)
        ()
        (append-to-projects-file (clojure.string/replace 
                                   (clojure.string/replace entry #"&amp;" "&") 
                                   #"\r?\n" ""))))))

(println "starting conversion")
(load-projects-from-file "projects-from-dts.txt")
(println "Done")
