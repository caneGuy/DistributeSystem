```
Doug lea并发代码
open jdk9
```
### BlockingQueue?
- Linked vs Array?

```
Array支持fair锁，也就是严格的fifo，但是吞吐量低。
为什么linked不支持？理解是因为本身已经是读写锁分离模式，没必要在加个公平锁，降低吞吐
```
- 使用Array的场景？

```
给这么一个场景，消费者必须要每次最多消费5个，然后后来线程必须要fifo的等待。这个时候可以考虑使用array
```

