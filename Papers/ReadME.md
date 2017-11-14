#### 分布式系统的几个关注点
参考mit 6.824的总结

- 实现

```
rpc，线程，并发控制
```

- 性能

```
	The dream: scalable throughput.
    Nx servers -> Nx total throughput via parallel CPU, disk, net.
    So handling more load only requires buying more computers.
  Scaling gets harder as N grows:
    Load im-balance, stragglers.
    Non-parallelizable code: initialization, interaction.
    Bottlenecks from shared resources, e.g. network.
```
- 容错

```
  1000s of servers, complex net -> always something broken
  We'd like to hide these failures from the application.
  We often want:
    Availability -- app can keep using its data despite failures
    Durability -- app's data will come back to life when failures are repaired
  Big idea: replicated servers.
    If one server crashes, client can proceed using the other(s).
```
- 一致性

```
General-purpose infrastructure needs well-defined behavior.
    E.g. "Get(k) yields the value from the most recent Put(k,v)."
  Achieving good behavior is hard!
    "Replica" servers are hard to keep identical.
    Clients may crash midway through multi-step update.
    Servers crash at awkward moments, e.g. after executing but before replying.
    Network may make live servers look dead; risk of "split brain".
  Consistency and performance are enemies.
    Consistency requires communication, e.g. to get latest Put().
    "Strong consistency" often leads to slow systems.
    High performance often imposes "weak consistency" on applications.
  People have pursued many design points in this spectrum.
```