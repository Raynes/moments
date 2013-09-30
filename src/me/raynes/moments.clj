(ns me.raynes.moments
  (:require [flatland.chronicle :as c]
            [clj-time.core :as t])
  (:import (java.util.concurrent Executors ScheduledThreadPoolExecutor TimeUnit)))

(def ^:private ms TimeUnit/MILLISECONDS)

(defn ^:private offset
  "Get the time in milliseconds between now and t."
  [t]
  (-> (t/now) (t/interval t) (t/in-millis)))

(defn executor
  "Create a ScheduledThreadPoolExecutor (lol) object
   with the specified thread pool size. Optional arguments
   can be :rejected-handler and :thread-factory."
  [pool-size & {:keys [rejected-handler thread-factory]}]
  (cond
    (and rejected-handler thread-factory)
    (ScheduledThreadPoolExecutor. pool-size rejected-handler thread-factory)
    (or rejected-handler thread-factory)
    (ScheduledThreadPoolExecutor. pool-size (or rejected-handler thread-factory))
    :else
    (ScheduledThreadPoolExecutor. pool-size)))

(defn ^:private sequential-scheduler
  "A self-sustaining scheduler function."
  [executor f times]
  (let [[start & rest] times]
    (fn []
      (.schedule executor
                 (sequential-scheduler executor f rest)
                 (offset start)
                 ms)
      (f))))

(defn schedule
  "Schedule a task to run based on a Chronicle specification."
  [executor spec f]
  (let [[start & rest] (c/times-for spec (t/now))]
    (.schedule executor
               (sequential-scheduler executor f rest)
               (offset start)
               ms)))
