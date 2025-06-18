(ns lemonade.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:gen-class))

(def posts
  [{:id 1 :title "Oh Hello there Clojure!" :content "This is my first post using Clojure!"}
   {:id 2 :title "Functional first!" :content "Clojure was the one that showed me that immutable data is a thing and OOP Java is not everything"}])

(defn layout [title body]
  (html5
   [:head
    [:title title]
    [:style "body { font-family: Arial; margin: 40px; }
            .post { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; }
            h1 { color: #333; }
            a { color: #0066cc; text-decoration: none; } "]]
   [:body body]))

(defn home-page []
  (layout "My Clojure Blog"
          [:div
           [:h1 "Welcome to my Bloggidy Blog Blog"]
           [:div
            (for [post posts]
              [:div.post
               [:h2 [:a {:href (str "/post/" (:id post))} (:title post)]]
               [:p (let [content (:content post)
                     preview (if (> (count content) 50)
                               (str (subs content 0 50) "...")
                               content)]
                 preview)]])]]))

(defn post-page [id]
  (let [post (first (filter #(= (:id %) (Integer/parseInt id)) posts))]
    (if post
      (layout (:title post)
              [:div
               [:h1 (:title post)]
               [:p (:content post)]
               [:a {:href "/"} "← Back to home"]])
      (layout "Not Found" [:h1 "Post not found"]))))

(defroutes app-routes
  (GET "/" [] (home-page))
  (GET "/post/:id" [id] (post-page id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main
  "the main man"
  []
  (println "Starting server on http://localhost:3000")
  (run-jetty app {:port 3000}))
