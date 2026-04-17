---
seller_name: 您的公司名稱
seller_contact: 聯絡人姓名
seller_tax_id: "12345678"
seller_phone: "0900000000"
seller_email: you@yourcompany.com
bank_name: 銀行名稱
bank_code: "000"
bank_branch: 分行名稱 (0000)
bank_account: "00000000000000"
bank_holder: 您的公司名稱
---

複製此檔案為 `seller.md`，填入您的公司資訊。

```bash
cp seller.example.md seller.md
```

`seller.md` 已加入 `.gitignore`，不會被提交到版本控制。

## 欄位說明

| 欄位 | 說明 |
|------|------|
| `seller_name` | 賣方公司名稱 |
| `seller_contact` | 賣方聯絡人姓名 |
| `seller_tax_id` | 賣方統一編號（若有前導零請加引號） |
| `seller_phone` | 賣方電話（若有前導零請加引號） |
| `seller_email` | 賣方 email |
| `bank_name` | 銀行名稱 |
| `bank_code` | 銀行代碼（請加引號） |
| `bank_branch` | 分行名稱（含代碼） |
| `bank_account` | 帳號（請加引號） |
| `bank_holder` | 戶名 |
