#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[parser    :as p]
         '[calculator :as c]
         '[assets    :as a]
         '[renderer  :as r])

(def ^:private required-fields
  [:client_name :client_tax_id :client_contact :client_phone :client_emails])

(defn- die [msg]
  (println (str "Error: " msg))
  (System/exit 1))

(defn- validate! [fm]
  (doseq [field required-fields]
    (when (nil? (get fm field))
      (die (str "缺少必填欄位 " (name field) "，請檢查 YAML front matter")))))

(defn- output-path [input-path]
  (let [fname (-> input-path
                  (str/split #"/")
                  last
                  (str/replace #"\.md$" ".html"))]
    (str "output/" fname)))

(defn- script-dir []
  (let [d (-> (System/getProperty "babashka.file")
              (str/split #"/")
              butlast
              (->> (str/join "/")))]
    (if (str/blank? d) "." d)))

(defn- load-seller [base-dir]
  (let [path (str base-dir "/seller.md")]
    (when-not (.exists (java.io.File. path))
      (die "找不到 seller.md，請複製 seller.example.md 並填入您的公司資訊：\n  cp seller.example.md seller.md"))
    (p/parse-front-matter (slurp path))))

(defn- main [input-path]
  (when (nil? input-path)
    (die "請指定輸入檔案，例如：bb quote.bb quotes/example.md"))
  (when-not (.exists (java.io.File. input-path))
    (die (str "找不到檔案 " input-path)))
  (let [content   (slurp input-path)
        fm        (p/parse-front-matter content)
        _         (validate! fm)
        raw-items (p/parse-table content)
        _         (when (empty? raw-items)
                    (die "找不到項目表格，請確認 Markdown table 格式正確"))
        calc      (c/calculate raw-items (:tax_rate fm))
        seller    (load-seller (script-dir))
        imgs      (a/load-assets (script-dir))
        html      (r/render (merge fm calc seller imgs))
        out-path  (output-path input-path)]
    (.mkdirs (java.io.File. "output"))
    (spit out-path html)
    (println (str "✓ 報價單已生成：" out-path))))

(main (first *command-line-args*))
