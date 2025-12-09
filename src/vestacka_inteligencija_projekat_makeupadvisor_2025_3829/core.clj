(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core)

(defn recommend-foundation
  "Vraca preporuku za puder na osnovu tipa koze"
  [skin-type]
  (cond
    (= skin-type "suva") "Hydrating foundation"
    (= skin-type "masna") "Oil-free matte foundation"
    (= skin-type "mesovita") "Semi-matte foundation"
    (= skin-type "osetljiva") "Hypoallergenic foundation"
    :else "Universal lightweight foundation"))

(defn shade
  [podton svetlina]
  (int (max 1 (min 10 (+ podton svetlina)))))

(defn average
  [populacija procenti]
  (let [avg (/ (reduce + procenti) (count procenti))]
    (int (* populacija avg))))

(defn recommend-lipstick
  [skin-tone]
  (cond
    (= skin-tone "svetao")  "Rosy pink, Soft coral"
    (= skin-tone "neutralan") "Nude rose, Mauve"
    (= skin-tone "taman")   "Deep berry, Burgundy"
    :else "Universal nude"))

(defn duzina-stringova-ruzeva
  [ruzevi]
  (reduce (fn [acc name]
            (+ acc (count name)))
          0
          ruzevi))

(defn pretvori-u-velika
  [ruzevi]
  (map clojure.string/upper-case
       (filter identity ruzevi)))

(defn ubaci-ruzeve
  [ruz sekvenca]
  (pop
    (reduce (fn [acc x]
              (conj acc x ruz))
            []
            sekvenca)))
