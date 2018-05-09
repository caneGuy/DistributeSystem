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

### ThreadLocal?
- 初步理解

```
1. threadlocal变量：通过这个变量可以调用threadlocal api。api底层是操作thread对应的threadlocalmap
2. threadlocalmap：每个thread持有一个hashmap保存了所有该线程使用的threadlocal变量值（这个和上面不一样的，这个变量是指的实际值，上面的理解为一个操作入口）
```

- 为什么要private static？

```
由于threadlocal变量本身操作的是线程对应的threadlocalmap，所以多个线程共享一个threadlocal变量没有关系。因为在不同的hashmap，key相同也没关系
```

