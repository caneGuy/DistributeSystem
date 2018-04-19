```
<<SOSP'17 Monotasks: Architecting for Performance Clarity in Data Analytics Frameworks>> 阅读笔记
```
#### 中心思想
```
－ 目标：让框架可以掌握资源利用率从而更加方便定位性能瓶颈
```
```
－ 本文提出了Monotask的概念：将原来的一个task分割成若干个monotasks，每个monotask只使用一种资源。如此一来，对于同一资源的竞争就只会出现在使用同一种资源的多个monotasks之中。Framework可以通过在每个资源上安排Scheduler来掌控对每个资源的调度。通过将task分解成monotask，我们可以清楚的知道一个任务在每种资源上花费了多长时间，又有多大程度地受到了其他task的影响。整个过程更加清晰可控。
```
```
－ 将task分解成多个monotask的代价就是一个task无法同时使用多种资源（比如在网络传输的同时进行数据读取），会导致一个task的执行时间变长。作者提出的解决方法是将一个job分解成足够多的multitasks，从而使得同一job的多个独立的monotask可以构成pipelining。
```
#### 缺点
1. 系统需要很多monotask才能充分利用pipelining。如果一个job的task很少，则需要手动增加这个任务的task个数才能导出更多的monotask。

2. 当一个task的中间很大，无法放到内存中时，传统的做法是临时存到硬盘里，最后再合并。而MonoSpark的设计则要求所有数据必须能够存在内存里，当task数据很大时则需要将该job切割成更多的小规模的multistasks。

3. MonoSpark的 Disk Scheduling和Multitask Scheduling都比较简单。