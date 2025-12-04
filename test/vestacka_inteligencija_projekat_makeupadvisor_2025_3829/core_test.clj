(ns vestacka_inteligencija_projekat_makeupadvisor_2025_3829.core-test
  (:require [clojure.test :refer :all]
            [vestacka_inteligencija_projekat_makeupadvisor_2025_3829.core :refer :all]))

(deftest foundation-recommendation-test
  (testing "Preporuke za tipove koze"
    (is (= "Hydrating foundation" (recommend-foundation "suva")))
    (is (= "Oil-free matte foundation" (recommend-foundation "masna")))
    (is (= "Universal lightweight foundation" (recommend-foundation "nepoznato")))))
