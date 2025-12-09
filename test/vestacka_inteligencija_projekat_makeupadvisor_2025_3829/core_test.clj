(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core-test
  (:require [clojure.test :refer :all]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core :refer :all]))

(deftest foundation-recommendation-test
  (testing "Preporuke za tipove koze"
    (is (= "Hydrating foundation" (recommend-foundation "suva")))
    (is (= "Oil-free matte foundation" (recommend-foundation "masna")))
    (is (= "Universal lightweight foundation" (recommend-foundation "nepoznato")))))

(deftest shade-test
  (testing
    (is (= 1 (shade -2 3)))
    (is (= 7 (shade 5 2)))))


(deftest average-test
  (testing
    (is (= (average
                  10000
                  [0.57 0.38 0.60])))))

(deftest test-recommend-lipstick
    (is (= "Rosy pink, Soft coral"
           (recommend-lipstick "svetao"))))

(deftest test-duzina-stringova-ruzeva
    (is (= 11 (duzina-stringova-ruzeva ["red" "nude" "pink"]))))

(deftest test-pretvori-u-velika
    (is (= '("RED" "NUDE" "PINK")
           (pretvori-u-velika ["red" "nude" "pink"]))))

(deftest test-ubaci-ruzeve
    (is (= ["a" "RUZ" "b" "RUZ" "c"]
           (ubaci-ruzeve "RUZ" ["a" "b" "c"]))))