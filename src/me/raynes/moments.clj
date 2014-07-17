(ns me.raynes.moments
  (:require [flatland.chronicle :as c]
            [clj-time.core :as t]
            [clj-time.local :as l])
  (:import (java.util.concurrent Executors ScheduledThreadPoolExecutor TimeUnit)))

(def ^:private ms TimeUnit/MILLISECONDS)

(defn ^:private offset
  "Get the time in milliseconds between now and t."
  [t]
  (-> (l/local-now) (t/interval t) (t/in-millis)))

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

(defn ^:private chronicle-scheduler
  "A self-sustaining scheduler function."
  [executor f times]
  (let [[start & rest] times]
    (fn []
      (.schedule executor
                 (chronicle-scheduler executor f rest)
                 (offset start)
                 ms)
      (f))))

(defn schedule-every
  "Schedule a task to run every n milliseconds starting n
   milliseconds from now. If init-delay is passed, the first
   run of the task will be delayed for that many milliseconds."
  ([executor n f]
   (schedule-every executor n 0 f))
  ([executor n init-delay f]
   (.scheduleWithFixedDelay executor f init-delay n ms)))

(defn schedule-at
  "Schedule a task to run at a specific time exactly once."
  [executor date f]
  (.schedule executor f (offset date) ms))

(defn schedule
  "Schedule a task to run based on a Chronicle specification."
  [executor spec f]
  (let [[start & rest] (c/times-for spec (l/local-now))]
    (schedule-at executor start (chronicle-scheduler executor f rest))))
