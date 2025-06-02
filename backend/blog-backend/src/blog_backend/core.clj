(ns blog-backend.core
  (:require [clojure.java.jdbc :as jdbc]
            [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]])
  (:gen-class))
;; This is the main entry point for the blog-backend application.

(def db-spec
  {:dbtype "postgresql"
   :dbname (or (System/getenv "DB_NAME") "blog_db")
   :host (or (System/getenv "DB_HOST") "localhost")
   :port (or (System/getenv "DB_PORT") "5432")
   :user (or (System/getenv "DB_USER") "postgres")
   :password (or (System/getenv "DB_PASSWORD") "12345")})

(defn test-connection []
  (try
    (jdbc/get-connection db-spec)
    (println "✅ DATABASE CONNECTION SUCCESSFUL")
    (catch Exception e
      (println "❌ CONNECTION FAILED:" (.getMessage e)))))

(defn pg-array->vec [pg-array]
  (when pg-array
    (vec (.getArray pg-array))))

;; query
(defn get-posts []
  (->> (jdbc/query db-spec ["SELECT * FROM posts"])
       (map #(update % :tags pg-array->vec))))

;;Routes
(defroutes app-routes
  (GET "/posts" [] {:status 200 :headers {} :body (get-posts)})
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#"http://localhost:3449"] ; Frontend port
                 :access-control-allow-methods [:get :post])
      (wrap-json-body {:keywords? true})
      (wrap-json-response {:pretty true :escape-non-ascii true})))

(defn -main [& args]
  (test-connection)
  (println "Starting Jetty server on port 3000")
  (jetty/run-jetty app {:port 3000 :join? false}))
