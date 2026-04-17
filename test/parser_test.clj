(ns parser-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [parser :as p]))

(def sample-input
  "---\ndate: \"2025-06-10\"\nclient_name: 測試公司\nclient_tax_id: 12345678\nclient_contact: 王小明\nclient_phone: 02 1234 5678\nclient_emails:\n  - test@example.com\ntax_rate: 0.05\n---\n\n| 項目 | 數量 | 單價 |\n|------|------|------|\n| 服務 A | 2 | 1000 |\n| 服務 B | 1 | 500 |\n")

(deftest test-parse-front-matter
  (testing "parses required string fields"
    (let [fm (p/parse-front-matter sample-input)]
      (is (= "測試公司" (:client_name fm)))
      (is (= "12345678" (:client_tax_id fm)))
      (is (= "王小明" (:client_contact fm)))
      (is (= "02 1234 5678" (:client_phone fm)))))
  (testing "parses email list"
    (let [fm (p/parse-front-matter sample-input)]
      (is (= ["test@example.com"] (vec (:client_emails fm))))))
  (testing "parses numeric tax_rate"
    (let [fm (p/parse-front-matter sample-input)]
      (is (= 0.05 (:tax_rate fm)))))
  (testing "returns date as string"
    (let [fm (p/parse-front-matter sample-input)]
      (is (string? (:date fm)))
      (is (= "2025-06-10" (:date fm)))))
  (testing "defaults date to today when absent"
    (let [no-date "---\nclient_name: foo\nclient_tax_id: 1\nclient_contact: bar\nclient_phone: 123\nclient_emails:\n  - a@b.com\n---\n"
          fm (p/parse-front-matter no-date)]
      (is (string? (:date fm)))
      (is (re-matches #"\d{4}-\d{2}-\d{2}" (:date fm)))))
  (testing "defaults tax_rate to 0.05 when absent"
    (let [no-tax "---\nclient_name: foo\nclient_tax_id: 1\nclient_contact: bar\nclient_phone: 123\nclient_emails:\n  - a@b.com\n---\n"
          fm (p/parse-front-matter no-tax)]
      (is (= 0.05 (:tax_rate fm))))))

(deftest test-parse-table
  (testing "returns list of item maps"
    (let [items (p/parse-table sample-input)]
      (is (= 2 (count items)))))
  (testing "item maps contain 項目, 數量, 單價"
    (let [[item1 item2] (p/parse-table sample-input)]
      (is (= "服務 A" (get item1 "項目")))
      (is (= "2" (get item1 "數量")))
      (is (= "1000" (get item1 "單價")))
      (is (= "服務 B" (get item2 "項目")))))
  (testing "returns empty list when no table"
    (is (= [] (p/parse-table "---\nfoo: bar\n---\n\nNo table here.\n"))))
  (testing "parses table from content without front matter"
    (let [items (p/parse-table "| 項目 | 數量 | 單價 |\n|------|------|------|\n| 服務 X | 3 | 200 |\n")]
      (is (= 1 (count items)))
      (is (= "服務 X" (get (first items) "項目"))))))

(run-tests 'parser-test)
