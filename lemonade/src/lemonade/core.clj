(ns lemonade.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [garden.core :refer [css]])
  (:gen-class))

(def posts
  [{:id 1 :title "Oh Hello there Clojure!" :content "This is my first post using Clojure!"}
   {:id 2 :title "Functional first!" :content "Clojure was the one that showed me that immutable data is a thing and OOP Java is not everything"}])

(def styles
  (css 
    [:body {:font-family "'Inter', -apple-system, sans-serif"
            :line-height "1.6"
            :color "#2d3748"
            :max-width "800px"
            :margin "0 auto"
            :padding "20px"
            :background-color "#f7fafc"}]
    
    [:.header {:text-align "center"
               :margin-bottom "3rem"
               :padding "2rem 0"
               :border-bottom "2px solid #e2e8f0"}]
    
    [:.site-title {:color "#1a202c"
                   :font-size "2.5rem"
                   :font-weight "700"
                   :margin "0"}]
    
    [:.subtitle {:color "#718096"
                 :font-size "1.1rem"
                 :margin "0.5rem 0 0 0"}]
    
    [:.post-list {:display "grid"
                  :gap "1.5rem"}]
    
    [:.post-card {:background "white"
                  :border-radius "12px"
                  :padding "1.5rem"
                  :box-shadow "0 4px 6px rgba(0,0,0,0.1)"
                  :transition "transform 0.2s, box-shadow 0.2s"
                  :border "1px solid #e2e8f0"}
     [:&:hover {:transform "translateY(-2px)"
                :box-shadow "0 8px 15px rgba(0,0,0,0.15)"}]]
    
    [:.post-title {:margin "0 0 0.5rem 0"
                   :font-size "1.5rem"
                   :font-weight "600"}
     [:a {:color "#2d3748"
          :text-decoration "none"}
      [:&:hover {:color "#4299e1"}]]]
    
    [:.post-preview {:color "#718096"
                     :margin "0"}]
    
    [:.post-content {:background "white"
                     :border-radius "12px"
                     :padding "2rem"
                     :box-shadow "0 4px 6px rgba(0,0,0,0.1)"
                     :margin-bottom "2rem"}]
    
    [:.back-link {:display "inline-block"
                  :color "#4299e1"
                  :text-decoration "none"
                  :font-weight "500"
                  :padding "0.5rem 1rem"
                  :border "2px solid #4299e1"
                  :border-radius "6px"
                  :transition "all 0.2s"}
     [:&:hover {:background-color "#4299e1"
                :color "white"}]]
    
    [:.post-full-title {:color "#1a202c"
                        :font-size "2rem"
                        :margin "0 0 1rem 0"}]
    
    [:.post-full-content {:font-size "1.1rem"
                          :line-height "1.7"}]))

(defn layout [title body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:meta {:name "description" :content "A vibrant Clojure blog exploring functional programming"}]
    [:link {:href "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" 
             :rel "stylesheet"}]
    [:title (str title " | Lemonade 🍋")]
    [:link {:href "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" 
             :rel "stylesheet"
             :crossorigin "anonymous"}]
    [:link {:rel "preconnect" :href "https://fonts.gstatic.com"}]
    [:style {:type "text/css"} styles]]
   [:body
    [:a.skip-link {:href "#main"} "skip to "]]
   [:body body]))

(defn home-page []
  (layout "My Clojure Blog"
    [:div
     [:header.header
      [:h1.site-title "My Clojure Blog"]
      [:p.subtitle "Exploring functional programming, one problem at a time"]]
     
     [:main.post-list
      (for [post posts]
        [:article.post-card
         [:h2.post-title 
          [:a {:href (str "/post/" (:id post))} (:title post)]]
         [:p.post-preview 
          (let [content (:content post)
                preview (if (> (count content) 100)
                          (str (subs content 0 100) "...")
                          content)]
            preview)]])]]))

(defn post-page [id]
  (let [post (first (filter #(= (:id %) (Integer/parseInt id)) posts))]
    (if post
      (layout (:title post)
        [:div
         [:article.post-content
          [:div.tag-list
           [:span.tag "Clojure"]
           [:span.tag "Functional"]
           [:span.tag "Programming"]]
          [:h1.post-full-title (:title post)]
          [:div.post-full-content (:content post)]]
         [:div {:style "text-align: center; margin-top: 2rem;"}
          [:a.back-link {:href "/"} "← Back to all posts"]]])
      (layout "Not Found" 
        [:div.post-content.not-found
         [:h1 "Post not found"]
         [:p "The post you're looking for doesn't exist."]
         [:div {:style "margin-top: 2rem;"}
          [:a.back-link {:href "/"} "← Back to all posts"]]]))))

(defroutes app-routes
  (GET "/" [] (home-page))
  (GET "/post/:id" [id] (post-page id))
  (route/not-found 
   (html5
    [:head
     [:title "Not Found"]
     [:style styles]]
    [:body
     [:div.post-content.not-found
      [:h1 "404 - Page Not Found"]
      [:p "The page you requested doesnt exist. Voetsek!"]
      [:div {:style "margin-top: 2rem;"}
       [:a.back-link {:href "/"} "← Back to home"]]]])))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main
  "the main man"
  []
  (println "Starting server on http://localhost:3000")
  (run-jetty app {:port 3000 :join? false}))
