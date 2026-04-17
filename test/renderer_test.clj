(ns renderer-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.string :as str]
            [renderer :as r]))

(def sample-data
  {:date "2025-06-10"
   :client_name "測試股份有限公司"
   :client_tax_id "12345678"
   :client_contact "王小明"
   :client_phone "02 1234 5678"
   :client_emails ["test@example.com"]
   :items [{:desc "服務 A" :qty 2 :price 1000 :total 2000}]
   :subtotal 2000
   :tax 100
   :tax-rate 0.05
   :grand-total 2100
   :logo "data:image/png;base64,LOGO"
   :seal-small "data:image/png;base64,SMALL"
   :seal-large "data:image/png;base64,LARGE"
   :seller_name "範例資訊有限公司"
   :seller_contact "測試聯絡人"
   :seller_tax_id "87654321"
   :seller_phone "0900000000"
   :seller_email "seller@example.com"
   :bank_name "範例銀行"
   :bank_code "000"
   :bank_branch "範例分行 (0000)"
   :bank_account "00000000000000"
   :bank_holder "範例資訊有限公司"})

(deftest test-render
  (testing "returns a string"
    (is (string? (r/render sample-data))))
  (testing "contains DOCTYPE"
    (is (str/includes? (r/render sample-data) "<!DOCTYPE html>")))
  (testing "contains client name"
    (is (str/includes? (r/render sample-data) "測試股份有限公司")))
  (testing "contains seller name"
    (is (str/includes? (r/render sample-data) "範例資訊有限公司")))
  (testing "contains date"
    (is (str/includes? (r/render sample-data) "2025-06-10")))
  (testing "contains item description"
    (is (str/includes? (r/render sample-data) "服務 A")))
  (testing "contains grand total"
    (is (str/includes? (r/render sample-data) "2100")))
  (testing "contains logo data URI"
    (is (str/includes? (r/render sample-data) "data:image/png;base64,LOGO")))
  (testing "contains seal data URIs"
    (let [html (r/render sample-data)]
      (is (str/includes? html "data:image/png;base64,SMALL"))
      (is (str/includes? html "data:image/png;base64,LARGE"))))
  (testing "contains payment bank info"
    (is (str/includes? (r/render sample-data) "範例銀行")))
  (testing "contains tax rate display"
    (is (str/includes? (r/render sample-data) "5%"))))

(run-tests 'renderer-test)
