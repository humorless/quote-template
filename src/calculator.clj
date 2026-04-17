(ns calculator
  "Computes line-item totals, subtotal, tax, and grand total from raw parsed items.
   Accepts raw-items as a seq of string-keyed maps {\"項目\" \"...\" \"數量\" \"N\" \"單價\" \"N\"}.")

(defn calculate
  "Given raw-items (from parser/parse-table) and a tax-rate (e.g. 0.05),
   returns a map:
     :items      — seq of {:desc :qty :price :total}
     :subtotal   — sum of all item totals
     :tax        — Math/round(subtotal * tax-rate)
     :tax-rate   — the input tax-rate
     :grand-total — subtotal + tax"
  [raw-items tax-rate]
  (let [items (map (fn [row]
                     (let [qty   (Integer/parseInt (get row "數量"))
                           price (Integer/parseInt (get row "單價"))
                           desc  (get row "項目")]
                       {:desc  desc
                        :qty   qty
                        :price price
                        :total (* qty price)}))
                   raw-items)
        subtotal    (reduce + (map :total items))
        tax         (Math/round (* (double subtotal) tax-rate))
        grand-total (+ subtotal tax)]
    {:items       items
     :subtotal    subtotal
     :tax         tax
     :tax-rate    tax-rate
     :grand-total grand-total}))
