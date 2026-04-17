(ns assets
  "Loads image files from the assets/ directory and returns Base64 data URIs
   suitable for inlining in HTML. Produces self-contained output with no
   external file dependencies.")

(defn file->data-uri
  "Reads a PNG file at path and returns a data URI string:
   \"data:image/png;base64,<base64-encoded-bytes>\""
  [path]
  (let [bytes (java.nio.file.Files/readAllBytes
               (java.nio.file.Paths/get path (into-array String [])))
        b64   (.encodeToString (java.util.Base64/getEncoder) bytes)]
    (str "data:image/png;base64," b64)))

(defn load-assets
  "Given the project base directory, returns a map of Base64 data URIs:
     :logo       — company logo
     :seal-small — small company seal
     :seal-large — large company seal"
  [base-dir]
  {:logo        (file->data-uri (str base-dir "/assets/logo.png"))
   :seal-small  (file->data-uri (str base-dir "/assets/seal-small.png"))
   :seal-large  (file->data-uri (str base-dir "/assets/seal-large.png"))})
