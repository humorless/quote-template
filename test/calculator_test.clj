(ns calculator-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [calculator :as c]))

(def raw-items
  [{"項目" "服務 A" "數量" "2" "單價" "1000"}
   {"項目" "服務 B" "數量" "1" "單價" "500"}])

(deftest test-calculate
  (testing "coerces string qty and price to integers"
    (let [{:keys [items]} (c/calculate raw-items 0.05)]
      (is (= 2 (:qty (first items))))
      (is (= 1000 (:price (first items))))))
  (testing "computes item total"
    (let [{:keys [items]} (c/calculate raw-items 0.05)]
      (is (= 2000 (:total (first items))))
      (is (= 500 (:total (second items))))))
  (testing "computes subtotal as sum of item totals"
    (let [{:keys [subtotal]} (c/calculate raw-items 0.05)]
      (is (= 2500 subtotal))))
  (testing "computes tax rounded to nearest integer"
    (let [{:keys [tax]} (c/calculate raw-items 0.05)]
      (is (= 125 tax))))
  (testing "computes grand total as subtotal + tax"
    (let [{:keys [grand-total]} (c/calculate raw-items 0.05)]
      (is (= 2625 grand-total))))
  (testing "example from PDF: subtotal=4762, tax=238, total=5000"
    (let [{:keys [subtotal tax grand-total]}
          (c/calculate [{"項目" "演講" "數量" "1" "單價" "4762"}] 0.05)]
      (is (= 4762 subtotal))
      (is (= 238 tax))
      (is (= 5000 grand-total)))))

(run-tests 'calculator-test)
