# moments

Moments is a simple task scheduling library (cronish) that uses
[chronicle](https://github.com/flatland/chronicle) and Java executors under the
hood.

Note that this library is designed for things that are meant to be run at
specific times, days, etc, and works at minute (not millisecond) granularity.
You cannot, for example, schedule something to run every 3 seconds.

## Usage

Usage is pretty easy. I suggest looking at the
[chronicle](https://github.com/flatland/chronicle) README for an idea of how the
specifications work. They're very similar to cron jobs, but represented as
Clojure maps. Here is an example usage. I'd like for my REPL to say `"Hi!"` to
me every 5 minutes. Let's make that happen.

```clojure
user=> (require '[me.raynes.moments :as m])
nil
user=> (def executor (m/executor 10))
#'user/executor
user=> (m/schedule executor {:minute (range 0 60 5)} #(println "Hi!"))
#<ScheduledFutureTask java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@23975e4d>
```

First of all we create an executor to schedule things on. `executor` is a
convenience function for creating an instance of
`ScheduledThreadPoolExecutor`because nobody wants to type that crap every time
they create an executor. Anyways, we're creating one with a thread pool size of
10\. Next, we schedule our task. We pass `schedule` the executor, a chronicle
spec, and a function to execute at times specified by the spec.

There isn't a good way to prove things happened after I ran the above code, but
I can assure you that it did indeed print out `"Hi!"` every 5 minutes, as
expected.

## License

Copyright © 2013 Anthony Grimes

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
