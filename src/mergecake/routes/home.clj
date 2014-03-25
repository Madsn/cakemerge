(ns mergecake.routes.home
  (:use compojure.core)
  (:require [mergecake.views.layout :as layout]
            [mergecake.util :as util]
            [mergecake.models.db :as db]
            [mergecake.models.schema :as schema]
            [postal.core :as postal]))

(defn trigger-table-creation []
  (schema/create-tables))

(defn add-user-page []
  (layout/render "add-user.html"))

(defn list-users-page []
  (layout/render "list-users.html"
                 {:users (db/get-all-users)}))

(defn list-cakedays-page []
  (layout/render "list-cakedays.html"
                 {:cakedays (db/get-all-cakedays)}))

(defn render-register-cakeday
  ([] (render-register-cakeday "" []))
  ([message] (render-register-cakeday message []))
  ([message params]
    (layout/render "register.html"
                   {:message message,
                    :users (db/get-all-users),
                    :projects (db/get-all-projects)
                    :params params})))

(defn render-cakeday-receipt [params]
  (layout/render "receipt.html" {:params params}))

(defn parse-date [date]
  (.parse (java.text.SimpleDateFormat. "dd-MM-yyyy") date))

(defn send-receipt [user cakeday projectname]
  (postal/send-message {:host "smtp.gmail.com"
                     :user "cakeportal@zpc.dk"
                     :pass "CENSORED"
                     :ssl :yes}
                    {:from "Cake Portal <cakeportal@zpc.dk>" 
                     :to (str (get user :initials) "@systematic.com")
                     :subject "Receipt for your cakeday registration" 
                     :body [{:type "text/html" 
                             :content (str "<h1>April fools!</h1>"
                             "<p>If you hadn't guessed already, the 'cake portal'"
                             " is an april fools joke for everybody in PS/INS.</p>"
                             "<p>You are of course still welcome to bring a treat for"
                             " the " projectname " project on " (get cakeday :date)
                             ". They would probably love that!</p>"
                             "<h3>Have a great day!</h3>")}]}))

(defn submit-cakeday [params]
  (let [date (parse-date (get params :date))
        proj (db/get-project (first (clojure.string/split (get params :proj) #" - ")))
        description (get params :description)
        user (db/get-user-with-initials (first (clojure.string/split (get params :user) #" - ")))
        projectname (get proj :projectname)
        projectid (get proj :id)]
    (if (db/cakeday-taken? date projectid)
      (render-register-cakeday "Sorry, there was a conflict. Choose another day." params)
      (do
        (let [new-cakeday {:userid (get user :id)
                           :date date
                           :description description
                           :projectid projectid}]
          (db/create-cakeday new-cakeday)
         (send-receipt user new-cakeday projectname)
          (render-cakeday-receipt {:user user
                                   :cakeday new-cakeday
                                   :projectname projectname}))))))

(defn submit-add-user [user]
  (do
    (db/create-user user)
    (list-users-page)))

(defn delete-user [id]
  (do
    (db/delete-user id)
    (list-users-page)))

(defn delete-cakeday [id]
  (do
    (db/delete-cakeday id)
    (list-cakedays-page)))

(defn add-project-page []
  (layout/render "add-project.html"))

(defn list-projects-page []
  (layout/render "list-projects.html"
                 {:projects (db/get-all-projects)}))

(defn submit-add-project [params]
  (do
    (db/create-project params)
    (list-projects-page)))

(defn delete-project [id]
  (do
    (db/delete-project id)
    (list-projects-page)))


(defroutes home-routes
  (GET "/" [] (render-register-cakeday))
  (POST "/submit-cakeday" {params :params} (submit-cakeday params))
  (GET "/list-cakedays" [] (list-cakedays-page))
  (GET "/cakedays/delete/:id" [id] (delete-cakeday id))
  
  (GET "/add-user" [] (add-user-page))
  (POST "/add-user" {params :params} (submit-add-user params))
  (GET "/list-users" [] (list-users-page))
  (GET "/users/delete/:id" [id] (delete-user id))
  
  (GET "/add-project" [] (add-project-page))
  (POST "/add-project" {params :params} (submit-add-project params))
  (GET "/list-projects" [] (list-projects-page))
  (GET "/projects/delete/:id" [id] (delete-project id))
  
  (GET "/create-tables" [] (trigger-table-creation))
  (GET "/reset-db" [] (db/reset-db)))
