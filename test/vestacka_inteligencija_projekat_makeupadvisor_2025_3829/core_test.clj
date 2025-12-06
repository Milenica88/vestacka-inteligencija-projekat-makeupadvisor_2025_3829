(ns vestacka_inteligencija_projekat_makeupadvisor_2025_3829.core-test
  (:require [clojure.test :refer :all]
            [vestacka_inteligencija_projekat_makeupadvisor_2025_3829.core :refer :all]))

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
    (is (= 5166 (average
                  10000
                  [0.57 0.38 0.60])))))