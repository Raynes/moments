# moments

Moments is a simple task scheduling library (cronish) that uses
[chronicle](https://github.com/flatland/chronicle) and Java executors under the
hood.

## Usage

![version](https://clojars.org/me.raynes/moments/latest-version.svg)

Moments has several different functions for doing scheduling-related things.
Let's look at them individually.

### `executor`

`executor` is a convenience function for creating an instance of
`ScheduledThreadPoolExecutor` because nobody wants to type that crap every time
they create an executor. The rest of this section will assume you've done the
following:

```clojure
user=> (require '[me.raynes.moments :as m])
nil
user=> (def executor (m/executor 10))
#'user/executor
```

This creates an executor with a thread pool size of 10 (for no particular
reason).

### `schedule`

I suggest looking at the [chronicle](https://github.com/flatland/chronicle)
README for an idea of how the specifications work. They're very similar to cron
jobs, but represented as Clojure maps. Here is an example usage. I'd like for
my REPL to say `"Hi!"` to me every 5 minutes. Let's make that happen.

```clojure
user=> (m/schedule executor {:minute (range 0 60 5)} #(println "Hi!"))
#<ScheduledFutureTask java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@23975e4d>
```

We pass `schedule` the executor, a chronicle spec, and a function to execute at
times specified by the spec.

There isn't a good way to prove things happened after I ran the above code, but
I can assure you that it did indeed print out `"Hi!"` every 5 minutes, as
expected.

### `schedule-at`

This one is even simpler. It just schedules a task to happen at a specific date
and time.

```clojure
user=> (require '[clj-time.core :as t])
nil
user=> (m/schedule-at executor (t/plus (t/now) (t/seconds 5)) #(println "Triggered!"))
#<ScheduledFutureTask java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@32d8c8a9>
```

It takes a Joda `DateTime` object and schedules a task for that time. In this
case it is 5 seconds after right now.

### `schedule-every`

This function schedules a task to run every `n` milliseconds, possibly with an
initial delay of `init-delay`.

```clojure
user=> (m/schedule-every executor 2000 5000  #(println "Yes!"))
#<ScheduledFutureTask java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@2f731def>
```

In this case, we're scheduling a task to run every 2000 milliseconds (two
seconds), starting after an initial delay of 5000 milliseconds. If you do not
want an initial delay, you can just omit that argument.

## License

Copyright © 2013 Anthony Grimes

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
