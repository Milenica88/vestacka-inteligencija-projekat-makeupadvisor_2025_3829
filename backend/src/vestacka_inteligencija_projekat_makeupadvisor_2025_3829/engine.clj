(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.engine
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn recommend-foundation [skin-type]
  (case skin-type
    "dry"         "Hydrating foundation"
    "oily"        "Oil-free matte foundation"
    "combination" "Semi-matte foundation"
    "sensitive"   "Hypoallergenic foundation"
    "Universal lightweight foundation"))

(defn recommend-lipstick [skin-tone]
  (case skin-tone
    "fair"   "Rosy pink, Soft coral"
    "medium" "Nude rose, Mauve"
    "deep"   "Deep berry, Burgundy"
    "Universal nude"))

(defn recommend-blush [undertone]
  (case undertone
    "warm" "Peach / coral blush"
    "cool" "Pink / mauve blush"
    "Soft rosy universal blush"))

(defn recommend-eyes [occasion]
  (case occasion
    "everyday" "Neutral nude shadows, thin liner"
    "work"     "Warm brown tones, subtle look"
    "evening"  "Smokey eye, light glitter"
    "party"    "Intense smokey, glitter, graphic liner"
    "wedding"  "Long-lasting glam look, waterproof mascara"
    "Natural, neutral look"))

(def desired-finish
  {"dry" "dewy" "oily" "matte" "combination" "satin"
   "sensitive" "natural" "normal" "natural"})

(def foundation-description
  {"dry"         "Hydrating foundation with a dewy finish"
   "oily"        "Oil-free matte foundation, long-lasting"
   "combination" "Semi-matte (satin) foundation, balances T-zone and cheeks"
   "sensitive"   "Hypoallergenic, lightweight, fragrance-free foundation"
   "normal"      "Universal lightweight foundation with a natural finish"})

(def occasion-intensity
  {"everyday" 1 "work" 2 "evening" 3 "party" 4 "wedding" 4})

(defn style-tip [{:keys [skin-type occasion]}]
  (let [intensity (get occasion-intensity occasion 1)]
    (str "For the '" (or occasion "everyday") "' occasion we recommend "
         (cond
           (>= intensity 4) "a bold, long-lasting look with primer and setting spray."
           (= intensity 3)  "emphasized eyes and a stronger lipstick shade."
           (= intensity 2)  "a clean, natural look suitable for work."
           :else            "a light, fresh daytime look.")
         (when (= skin-type "sensitive")
           " Choose hypoallergenic, fragrance-free products."))))

(defn- tags-of [product]
  (->> (:tag_list product)
       (remove nil?)
       (map (comp str/lower-case str))
       set))

(defn- round1 [x]
  (/ (Math/round (* (double x) 10.0)) 10.0))

(defn score-product [request product]
  (let [{:keys [skin-type preferences]} request
        tags          (tags-of product)
        prefs         (set (map (comp str/lower-case str) (or preferences [])))
        finish        (get desired-finish skin-type)
        rating        (or (:rating product) 0)
        price         (or (:price product) 0)
        matched-prefs (set/intersection prefs tags)
        finish-hit?   (boolean (and finish (contains? tags finish)))
        budget?       (and (contains? prefs "budget") (pos? price) (< price 12))
        pref-reasons  (map #(str "Matches preference: " %) matched-prefs)
        score (cond-> 0.0
                      finish-hit?         (+ 3.0)
                      (seq matched-prefs) (+ (* 2.0 (count matched-prefs)))
                      budget?             (+ 1.0)
                      true                (+ (* 0.5 rating)))
        reasons (cond-> []
                        finish-hit?        (conj (str "Finish matches skin (" finish ")"))
                        (seq pref-reasons) (into pref-reasons)
                        budget?            (conj "Affordable price")
                        (>= rating 4)      (conj "Highly rated by users"))]
    {:score (round1 score) :reasons reasons}))

(defn- present-product [product]
  {:id (:id product) :brand (:brand product) :name (:name product)
   :type (:product_type product) :price (:price product) :rating (:rating product)
   :image (:image_link product) :tags (vec (or (:tag_list product) []))
   :score (:skor product) :reasons (:razlozi product)})

(defn rank-products
  ([request products] (rank-products request products 6))
  ([request products n]
   (->> products
        (map (fn [p]
               (let [{:keys [score reasons]} (score-product request p)]
                 (assoc p :skor score :razlozi reasons))))
        (sort-by :skor >)
        (take n)
        (map present-product))))

(defn advise
  [{:keys [skin-type skin-tone undertone occasion preferences] :as request} products]
  {:profile  {:skin-type skin-type :undertone undertone :tone skin-tone
              :occasion occasion :preferences (vec (or preferences []))}
   :style    {:foundation {:name (recommend-foundation skin-type)
                           :description (get foundation-description skin-type
                                             "Universal lightweight foundation")}
              :lipstick (recommend-lipstick skin-tone)
              :blush    (recommend-blush undertone)
              :eyes     (recommend-eyes occasion)
              :tip      (style-tip request)}
   :products (rank-products request products)})