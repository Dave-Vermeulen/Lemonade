(ns blog-backend.core
  (:require [clj-postgresql.core :as pg]
            [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]])
  (:gen-class))
;; This is the main entry point for the blog-backend application.

(def db-spec
  {:dbtype "postgresql"
   :dbname "blog_db"
   :host "localhost"
   :port 5432
   :user "postgres"
   :password "12345"})

;; query)
(defn get-posts []
 (pg/query db-spec ["SELECT * FROM posts"]))

;;Routes
(defroutes app-routes
  (GET "/posts" [] {:status 200 :headers {} :body (get-posts)})
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      (wrap-json-response {:pretty true :escape-non-ascii true})))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000 :join? false}))
