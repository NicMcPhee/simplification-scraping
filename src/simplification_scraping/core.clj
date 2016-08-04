(ns simplification-scraping.core
  (require [clojure.string :as str]
           [clojure.edn :as edn])
  (:gen-class))

;; {:genome '(....)
;;  :success-generation 53
;;  :training-cases [[case 1] [case 2] [case 3] ...]
;;  :testing-cases [[test 1] [test 2] [test 3] ...]
;;  :whatever-else-we-said-we'd-collect :stuff
;;  ...
;; }

(defn generate-edn-filename [log-filename]
  (str/replace log-filename ".txt" ".edn"))

(defn add-case [data case-type match-text]
  (assoc-in data [case-type (count (case-type data))]
            (edn/read-string match-text)))

(defn update-best-genome [data match-text]
  (assoc data :genome (edn/read-string match-text)))

(defn update-success-generation [data match-text]
  (assoc data
    :success-generation (edn/read-string match-text)
    :success true))

(defn process-line [data line]
  (condp #(nth (re-matches %1 %2) 1) line
    #"Train Case:\s+\d+ \| Input/Output: (.+)" :>> (partial add-case data :training-cases)
    #"Test Case:\s+\d+ \| Input/Output: (.+)" :>> (partial add-case data :test-cases)
    #"Best genome: (.+)" :>> (partial update-best-genome data)
    #"SUCCESS at generation (\d+)" :>> (partial update-success-generation data)
    data))

(def empty-scraped-data
  {:success false
   :training-cases []
   :test-cases []})

(defn scrape [log-contents]
  (let [lines (str/split-lines log-contents)
        scraped-data (reduce process-line empty-scraped-data lines)]
    (when (:success scraped-data)
      scraped-data)))

(defn scrape-log-file [log-filename]
  "Scrape the relevant content out of the given log file, generating
   a corresponding EDN file."
  (let [edn-filename (generate-edn-filename log-filename)
        log-contents (slurp log-filename)
        edn-data (scrape log-contents)]
    (when edn-data
      (spit edn-filename (pr-str edn-data)))))

(defn -main
  "Arguments are log files to scrape, each of which will generate an EDN file."
  [& args]
  (doseq [log-file args]
    (scrape-log-file log-file)))

