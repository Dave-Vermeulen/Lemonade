(ns blog-frontend.core
  (:require [reagent.core :as r]
            [cljs-http.client :as http]))

(defonce state (r/atom {:posts []}))

(defn fetch-posts []
  (http/get "http://localhost:3000/posts"
            {:with-credentials? false
             :json-params {}
             :handler #(reset! state {:posts (:body %)})
             :error-handler #(js/console.error "Failed to fetch posts:" %)}))

(defn post-component [post]
  [:div {:class "post"}
   [:h2 (:title post)]
   [:p (:content post)]
   [:small (str "Tags: " (clojure.string/join ", " (:tags post)))]])

(defn blog-view []
  (fetch-posts)
  (fn []
    [:div
     [:h1 "🚀 Welcome to My Retro Blog"]
     (for [post (:posts @state)]
       [post-component post])]))

(defn ^:export main []
  (r/render [blog-view] (.getElementById js/document "app")))
