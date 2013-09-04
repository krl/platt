(ns platt.core
  (:require [taoensso.nippy :as nippy])
  (:import [org.iq80.leveldb Options]
           [org.iq80.leveldb.impl Iq80DBFactory]))

(def default-options (. (Options.) createIfMissing true))
(def ^:dynamic *current-db*)

(defn open-database
  "Opens a database at path and returns a reference."
  [path]
  (.open (Iq80DBFactory.) (java.io.File. path) default-options))

(defn close-database
  "Closes an open database"
  [db]
  (.close db))
  
(defmacro with-db
  "Sets the database to use within this block"
  [db & body]
  `(binding [*current-db* ~db]
     ~@body))

(defmacro transact
  "Sets up a transaction, either all writes in this block succeed or all fail."
  [& body]
  `(let [db#    *current-db*
         batch# (.createWriteBatch db#)]
     (binding [*current-db* batch#]
       (try
         (do ~@body
             (.write db# *current-db*))
         (finally
           (.close batch#))))))

(defn put
  "Associates a key with a value in the current database."
  [key value]
  (.put *current-db* (nippy/freeze key) (nippy/freeze value)))

(defn fetch
  "Gets the value associated with the key in the current database."
  [key & [default]]
  (if-let [value (.get *current-db* (nippy/freeze key))]
    (nippy/thaw value)
    default))