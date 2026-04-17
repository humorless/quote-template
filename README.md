# 報價單產生器

用 Babashka 從 Markdown 輸入檔產生 HTML 報價單。生成的 HTML 為單一自含檔案（圖片以 Base64 內嵌），可直接用瀏覽器開啟或列印成 PDF。

## 需求

- [Babashka](https://babashka.org/) v1.12+

## 使用方式

### 0. 設定賣方資訊

複製 `seller.example.md` 為 `seller.md`，填入您的公司資訊（`seller.md` 已加入 `.gitignore`，不會被提交）：

```bash
cp seller.example.md seller.md
# 編輯 seller.md，填入您的公司名稱、統編、電話、匯款帳號等
```

### 1. 建立報價單輸入檔

在 `quotes/` 目錄下建立一個 `.md` 檔（可參考 `quotes/example.md`）：

```markdown
---
date: "2025-06-10"          # 可省略，預設為今天
client_name: 範例科技股份有限公司
client_tax_id: "12345678"   # 若有前導零請加引號
client_contact: 王小明 先生
client_phone: 02 1234 5678
client_emails:
  - contact@example.com
  - info@example.com
tax_rate: 0.05               # 可省略，預設為 0.05（5%）
---

| 項目 | 數量 | 單價 |
|------|------|------|
| 企業演講-軟體教育訓練 / 範例課程名稱 | 1 | 5000 |
```

### 2. 產生報價單

```bash
bb quote.bb quotes/你的檔案.md
```

輸出：`output/你的檔案.html`

### 3. 轉成 PDF（選用）

用瀏覽器開啟 HTML 後，選「列印 → 另存為 PDF」。

## 欄位說明

### 必填欄位

| 欄位 | 說明 |
|------|------|
| `client_name` | 客戶公司名稱 |
| `client_tax_id` | 客戶統一編號（若有前導零請加引號） |
| `client_contact` | 客戶聯絡人姓名 |
| `client_phone` | 客戶電話 |
| `client_emails` | 客戶 email，可填多個 |

### 選填欄位

| 欄位 | 預設值 | 說明 |
|------|--------|------|
| `date` | 今天日期 | 格式 `"YYYY-MM-DD"`（需加引號） |
| `tax_rate` | `0.05` | 營業稅率（5% = 0.05） |

### 項目表格

Markdown table，欄位名稱必須是「項目」、「數量」、「單價」：

```markdown
| 項目 | 數量 | 單價 |
|------|------|------|
| 服務說明 | 數量(整數) | 單價(整數, NTD) |
| 另一項服務 | 1 | 3000 |
```

費用（數量 × 單價）、小計、稅額、總計均自動計算。

## 執行測試

```bash
bb run test
```

## 專案結構

```
quote.bb              ← 主程式（入口點）
bb.edn                ← Babashka 專案設定
seller.example.md     ← 賣方資訊範本（複製為 seller.md 並填入真實資料）
seller.md             ← 賣方資訊（已加入 .gitignore，請自行建立）
src/
  parser.clj          ← 解析 YAML front matter 和 Markdown table
  calculator.clj      ← 計算小計、稅、總計
  assets.clj          ← 圖片 Base64 編碼
  renderer.clj        ← HTML 生成（hiccup）
test/
  parser_test.clj
  calculator_test.clj
  renderer_test.clj
assets/               ← 公司 logo 與印章（已加入 .gitignore，請自行準備）
  logo.png            ← 公司 logo（顯示於報表頭部中央）
  seal-small.png      ← 小章（顯示於報表右下角）
  seal-large.png      ← 大章（顯示於報表右下角）
quotes/               ← 報價單輸入檔（.md）放這裡
output/               ← 產生的 HTML 放這裡
```
