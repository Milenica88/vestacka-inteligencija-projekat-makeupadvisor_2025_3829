(ns vestacka_inteligencija_projekat_makeupadvisor_2025_3829.core)

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


