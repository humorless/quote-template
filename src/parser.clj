(ns parser
  "Parses YAML front matter and Markdown tables from quote template input."
  (:require [clojure.string :as str]
            [clj-yaml.core :as yaml]))

(defn- today-str []
  (let [sdf (java.text.SimpleDateFormat. "yyyy-MM-dd")]
    (.format sdf (java.util.Date.))))

(defn- format-date [d]
  (cond
    (nil? d) (today-str)
    (string? d) d
    (instance? java.util.Date d)
    (let [sdf (java.text.SimpleDateFormat. "yyyy-MM-dd")]
      (.setTimeZone sdf (java.util.TimeZone/getTimeZone "UTC"))
      (.format sdf d))
    :else (str d)))

(defn- split-document [content]
  ;; Returns [yaml-str body-str]. Content must start with "---\n".
  (let [parts (str/split content #"---\n" 3)]
    (if (= 3 (count parts))
      [(second parts) (nth parts 2)]
      [nil content])))

(defn parse-front-matter
  "Parses YAML front matter from a document string.
   Returns a map with keys :date, :tax_rate, and all other YAML fields.
   Defaults :date to today and :tax_rate to 0.05 when absent.
   String fields that YAML parses as numbers are coerced back to strings.
   NOTE: client_tax_id must be quoted in YAML (e.g. \"01234567\") if it
   has leading zeros, or YAML will drop them before coercion."
  [content]
  (let [[yaml-str _] (split-document content)
        raw (if yaml-str (yaml/parse-string yaml-str) {})
        date (format-date (:date raw))
        tax-rate (or (:tax_rate raw) 0.05)
        ;; Ensure numeric-looking fields stay as strings even if the YAML
        ;; source is unquoted. Only fields that may look like numbers to
        ;; YAML need this treatment.
        str-fields [:client_tax_id :client_phone]]
    (reduce
     (fn [m k]
       (if (contains? m k)
         (update m k str)
         m))
     (-> raw
         (assoc :date date)
         (assoc :tax_rate tax-rate))
     str-fields)))

(defn- parse-cells [row]
  (->> (str/split row #"\|")
       (map str/trim)
       (filter seq)))

(defn- separator-row? [row]
  (re-matches #"\|[-: ]+(?:\|[-: ]+)*\|" (str/trim row)))

(defn parse-table
  "Parses a Markdown table from a document string.
   Returns a list of maps where keys are column headers and values are cell strings.
   Returns [] when no table is found."
  [content]
  (let [[_ body] (split-document content)
        body (or body content)
        lines (->> (str/split-lines body)
                   (filter #(str/starts-with? (str/trim %) "|")))
        data-lines (remove separator-row? lines)]
    (if (< (count data-lines) 2)
      []
      (let [[header & rows] data-lines
            headers (parse-cells header)]
        (->> rows
             (map (fn [row]
                    (let [cells (parse-cells row)]
                      (zipmap headers cells))))
             (filter #(= (count %) (count headers))))))))
