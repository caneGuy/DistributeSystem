```
java 网络相关知识和jdk 源码
```
### 网络请求处理的基本流程？
```
- read request
- decode request
- process service
- encode response
- send response
```
### 哪些阶段会阻塞？
```
- 监听请求：accept
- 读取数据：read input
- 发送数据：send output
```

### 如何实现可扩展I/O?
目标:

```
- 高负载情况下的优雅降级
- 硬件的升级能持续地给系统带来性能提升
- 当然也包含可用性和性能的目标：低延迟、高负载等
```

### Reactor模式？
### NIO如何实现非阻塞？
首先，搞清楚：阻塞和非阻塞，同步和异步的区别。这个在(unix network programming volume 1 the sockets networking i/o models)中有详细介绍。也可以参考[同步 异步 阻塞 非阻塞](https://blog.csdn.net/historyasamirror/article/details/5778378)。

其次，搞清楚nio的实现。首先我们要明确，java nio是new io，它也可以实现阻塞的通信模型。**只不过它支持非阻塞，这和大部分资料直接上来就说non-blocking不一样**。至于说什么时候使用阻塞通信，那是另一个话题。

关键类，selector，channel，buffer。最后底层都是调用的native，对native方法做了一些抽象。native方法调用的是poll epoll等接口

题外话，jdk1.4以前，调用的是select接口，所以是同步阻塞的。适用于连接数少对资源要求高的（个人理解nio能hold住这个场景，只不过是历史包袱）

### zero copy?

### Reference
[Doug Lea讲解NIO](http://gee.cs.oswego.edu/dl/cpjslides/nio.pdf)
