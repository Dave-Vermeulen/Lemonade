(ns blog-frontend.core
  (:require [reagent.core :as r]
            [cljs-http.client :as http]
            [clojure.string :as str]))

(defonce state (r/atom {:posts []
                        :loading? true
                        :error nil}))

(defn fetch-posts []
  (swap! state assoc :loading? true :error nil)
  (http/get "http://localhost:3000/posts"
            {:with-credentials? false
             :headers {"Accept" "application/json"}
             :handler (fn [response]
                        (swap! state assoc 
                               :posts (:body response)
                               :loading? false))
             :error-handler (fn [error]
                              (swap! state assoc 
                                     :error (str "Failed to fetch posts: " (:status error))
                                     :loading? false))}))

(defn post-component [post]
  [:div {:class "post retro-box"}
   [:h2 {:class "retro-title"} (:title post)]
   [:div {:class "retro-meta"}
    [:span {:class "retro-date"} (-> (:published_at post) (str/split #"T") first)]
    [:span {:class "retro-tags"} 
     (str "Tags: " (str/join ", " (:tags post)))]
   [:p {:class "retro-content"} (:content post)]
   [:div {:class "retro-footer"} 
    [:a {:href (str "/post/" (:slug post))} "Read more →"]]]])

(defn blog-view []
  (r/create-class
   {:component-did-mount fetch-posts  ; Fetch posts on mount
    :reagent-render
    (fn []
      [:div {:class "retro-container"}
       [:header {:class "retro-header"}
        [:h1 "🚀 Welcome to My Retro Blog"]
        [:p "A blast from the past with Clojure power!"]]
       
       (cond
         (:loading? @state) 
         [:div {:class "retro-loading"} "⌛ Loading posts..."]
         
         (:error @state) 
         [:div {:class "retro-error"} "❌ " (:error @state)]
         
         :else
         [:div {:class "retro-posts"}
          (for [post (:posts @state)]
            ^{:key (:id post)} [post-component post])])])}))

(defn ^:export main []
  (r/render [blog-view] (.getElementById js/document "app")))
