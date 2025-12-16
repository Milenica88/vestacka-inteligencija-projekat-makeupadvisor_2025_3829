(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core :refer :all]))

;(deftest foundation-recommendation-test
;  (testing "Preporuke za tipove koze"
;    (is (= "Hydrating foundation" (recommend-foundation "suva")))
;    (is (= "Oil-free matte foundation" (recommend-foundation "masna")))
;    (is (= "Universal lightweight foundation" (recommend-foundation "nepoznato")))))
;
;(deftest shade-test
;  (testing
;    (is (= 1 (shade -2 3)))
;    (is (= 7 (shade 5 2)))))
;
;
;(deftest average-test
;  (testing
;    (is (= (average
;                  10000
;                  [0.57 0.38 0.60])))))
;
;(deftest test-recommend-lipstick
;    (is (= "Rosy pink, Soft coral"
;           (recommend-lipstick "svetao"))))
;
;(deftest test-duzina-stringova-ruzeva
;    (is (= 11 (duzina-stringova-ruzeva ["red" "nude" "pink"]))))
;
;(deftest test-pretvori-u-velika
;    (is (= '("RED" "NUDE" "PINK")
;           (pretvori-u-velika ["red" "nude" "pink"]))))
;
;(deftest test-ubaci-ruzeve
;    (is (= ["a" "RUZ" "b" "RUZ" "c"]
;           (ubaci-ruzeve "RUZ" ["a" "b" "c"]))))
;
;(deftest test-dodaj-preporuku
;  (let [novi-db (dodaj-preporuku makeup-db :suva :svetao "Hydrating foundation")]
;    (is (= (get-in novi-db [:suva :svetao])
;           ["Hydrating foundation"]))))


(facts "recommend-foundation"
       (recommend-foundation "suva") => "Hydrating foundation"
       (recommend-foundation "masna") => "Oil-free matte foundation"
       (recommend-foundation "mesovita") => "Semi-matte foundation"
       (recommend-foundation "osetljiva") => "Hypoallergenic foundation"
       (recommend-foundation "nepoznato") => "Universal lightweight foundation")

(facts "shade funkcija"
       (shade 3 4) => 7
       (shade 10 5) => 10   ;; gornja granica
       (shade -5 1) => 1)   ;; donja granica

(facts "average"
       (average 100 [0.1 0.2 0.3]) => 20
       (average 50 [0.5 0.5]) => 25)

(facts "recommend-lipstick"
       (recommend-lipstick "svetao") => "Rosy pink, Soft coral"
       (recommend-lipstick "neutralan") => "Nude rose, Mauve"
       (recommend-lipstick "taman") => "Deep berry, Burgundy"
       (recommend-lipstick "xyz") => "Universal nude")

(facts "dodaj-preporuku"
       (get-in (dodaj-preporuku makeup-db :suva :svetao "Ruz A")
               [:suva :svetao])
       => ["Ruz A"])

(facts "duzina-stringova-ruzeva"
       (duzina-stringova-ruzeva ["Red" "Pink"]) => 7)

(facts "pretvori-u-velika"
       (pretvori-u-velika ["red" nil "pink"])
       => ["RED" "PINK"])

(facts "ubaci-bez-ruzeva"
       (ubaci-ruzeve nil [1 2 3]))