(ns data-lineage-viz.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [reitit.frontend :as reitit]
              [clerk.core :as clerk]
              [accountant.core :as accountant]
              [ajax.core :as ajax]))

(def state (atom {}))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn load-menu-items! []
  (ajax/GET "/menu-items"
            {:handler (fn [response]
                        (swap! state assoc :menu-items response))
             :response-format :json
             :keywords? true}))

(defn get-graph-string!
  ([] (get-graph-string! nil))
  ([node] (ajax/GET (str "/graph" (if (nil? node) "" (str "/" node)))
             {:handler (fn [response]
                         (swap! state assoc :graph (:graph response)))
              :response-format :json
              :keywords? true})))

(defn menu-group-heading [group-key group-val]
  (let [group-name (:label group-val)]
    [:li.pure-menu-heading group-name]))

(defn menu-item [item active-item]
  (let [[item-key item-val] item
        item-name (:label item-val)]
    [:li.pure-menu-item
     [:a.pure-menu-link {:href "#"
                         :class (when (= item-key active-item)
                                  "pure-menu-selected")
                         :on-click (fn []
                                     (get-graph-string! (name item-key))
                                     (swap! state assoc :selected-menu-item item-key))}
       item-name]]))

(defn sidebar []
  (fn []
    (.log js/console (:menu-items @state))
    (let [active-item (:selected-menu-item @state)]
      [:div.pure-menu.custom-restricted-width
       [:ul.pure-menu-list
        (for [group (:menu-items @state)]
          (let [[group-key group-val] (first group)]
            [:<>
             [menu-group-heading group-key group-val]
             (for [item (:items group-val)]
               [menu-item item active-item])]))]])))

(defn index-page []
  (fn []
    (let [graph (:graph @state)]
      [:div#graph
       [:img {:src (str "data:image/svg+xml;base64," graph)}]])))

;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'index-page))

;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      ["div"
       [sidebar]
       [:div.content
        [page]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (load-menu-items!)
  (get-graph-string!)
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
