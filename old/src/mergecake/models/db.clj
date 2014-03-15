(ns mergecake.models.db
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]))

;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to hardcoded mongohq db
(let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://systematic:12345@oceanic.mongohq.com:10014/cakemerge")]
  (mg/connect-via-uri! uri))

(defn get-user [id]
  )

(defn create-cakeday [cakeday]
  (mc/insert "cakeday" cakeday))

(defn update-cakeday [id baker date description]
  (mc/update "cakeday" {:id id}
             {$set {:baker baker
                    :date date
                    :description description}}))

(defn get-cakeday [id]
  (mc/find-one-as-map "cakeday" {:id id}))
