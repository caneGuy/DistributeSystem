```
Doug lea并发代码
open jdk9
```
### BlockingQueue?
1. Linked vs Array?
Array支持fair锁，也就是严格的fifo，但是吞吐量低。
为什么linked不支持？理解是因为本身已经是读写锁分离模式，没必要在加个公平锁，降低吞吐
