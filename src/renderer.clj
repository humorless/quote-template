(ns renderer
  "Generates a self-contained HTML quote document from parsed and calculated data.
   All images are inlined as Base64 data URIs — no external file dependencies."
  (:require [hiccup.core :as h]))

(def ^:private css
  "* { box-sizing: border-box; margin: 0; padding: 0; }
   body { font-family: 'Noto Sans TC', 'Microsoft JhengHei', 'PingFang TC', sans-serif;
          font-size: 14px; color: #333; }
   .page { max-width: 820px; margin: 0 auto; padding: 40px; }
   .header { display: flex; align-items: center; justify-content: space-between;
             margin-bottom: 8px; }
   .header-center img { height: 65px; }
   .header-left, .header-right { font-size: 15px; font-weight: bold; min-width: 160px; }
   .header-right { text-align: right; }
   hr { border: none; border-top: 2px solid #333; margin: 8px 0 28px; }
   h1 { text-align: center; font-size: 30px; margin-bottom: 28px; letter-spacing: 6px; }
   .contacts { display: grid; grid-template-columns: 1fr 1fr; gap: 20px;
               margin-bottom: 30px; }
   .contacts p { line-height: 2.0; }
   table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
   th { background: #f5f5f5; padding: 8px 14px; border: 1px dashed #bbb;
        text-align: center; }
   td { padding: 8px 14px; border: 1px dashed #bbb; vertical-align: top; }
   td:nth-child(2), td:nth-child(3), td:nth-child(4) { text-align: right;
                                                        white-space: nowrap; }
   th:nth-child(2), th:nth-child(3), th:nth-child(4) { text-align: right; }
   tr.subtotal-row td { border-top: 1px solid #999; }
   .payment { margin-bottom: 30px; line-height: 2.2; }
   .footer { display: flex; justify-content: flex-end; gap: 16px;
             align-items: flex-end; }
   .footer img { height: 110px; }
   @media print { body { font-size: 12px; } .page { padding: 20px; } }")

(defn render
  "Generates a complete HTML string for a quote document.
   data must contain:
     Client fields: :client_name, :client_tax_id, :client_contact, :client_phone,
       :client_emails (seq)
     Seller fields (from seller.md): :seller_name, :seller_contact, :seller_tax_id,
       :seller_phone, :seller_email
     Bank fields (from seller.md): :bank_name, :bank_code, :bank_branch,
       :bank_account, :bank_holder
     Calculated fields: :date, :items (seq of {:desc :qty :price :total}),
       :subtotal, :tax, :tax-rate, :grand-total
     Image fields: :logo, :seal-small, :seal-large (Base64 data URIs)"
  [data]
  (let [{:keys [date client_name client_tax_id client_contact client_phone
                client_emails items subtotal tax tax-rate grand-total
                logo seal-small seal-large
                seller_name seller_contact seller_tax_id seller_phone seller_email
                bank_name bank_code bank_branch bank_account bank_holder]} data
        tax-pct (str (int (* tax-rate 100)) "%")]
    (str "<!DOCTYPE html>"
         (h/html
           [:html {:lang "zh-TW"}
            [:head
             [:meta {:charset "UTF-8"}]
             [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
             [:title "報價單"]
             [:style css]]
            [:body
             [:div.page
              [:div.header
               [:div.header-left seller_name]
               [:div.header-center [:img {:src logo :alt "logo"}]]
               [:div.header-right (str "日期：" date)]]
              [:hr]
              [:h1 "報價單"]
              [:div.contacts
               [:div.contact-left
                [:p (str "聯絡人：" seller_contact)]
                [:p seller_name]
                [:p (str "統編：" seller_tax_id)]
                [:p (str "電話：" seller_phone)]
                [:p seller_email]]
               [:div.contact-right
                [:p (str "聯絡人：" client_contact)]
                [:p client_name]
                [:p (str "統編：" client_tax_id)]
                [:p (str "電話：" client_phone)]
                (map (fn [email] [:p email]) client_emails)]]
              [:table
               [:thead
                [:tr [:th "項目"] [:th "數量"] [:th "單價"] [:th "費用(NTD)"]]]
               [:tbody
                (map (fn [{:keys [desc qty price total]}]
                       [:tr [:td desc] [:td qty] [:td price] [:td total]])
                     items)
                (repeat 4 [:tr [:td {:colspan 4} "\u00a0"]])
                [:tr.subtotal-row
                 [:td {:colspan 2} ""] [:td "小計"] [:td subtotal]]
                [:tr
                 [:td {:colspan 2} ""] [:td (str "營業稅 " tax-pct)] [:td tax]]
                [:tr
                 [:td {:colspan 2} ""] [:td "總計"] [:td grand-total]]]]
              [:div.payment
               [:p [:strong "匯款資訊："]]
               [:p (str "銀行名：" bank_name)]
               [:p (str "銀行代碼：" bank_code)]
               [:p (str "分行別：" bank_branch)]
               [:p (str "帳號：" bank_account)]
               [:p (str "戶名：" bank_holder)]]
              [:div.footer
               [:img {:src seal-small :alt "小章"}]
               [:img {:src seal-large :alt "大章"}]]]]]))))
