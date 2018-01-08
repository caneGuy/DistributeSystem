```
<<Apache Flink™: Stream and Batch Processing in a Single Engine>> 阅读笔记
```
### 解决什么问题？
主要是解决之前各种框架或者实现在支持streaming计算的时候，存在要么有延时，要么有复杂度过高的问题。
同时，基于streaming的模型，抽象出对batch等运算的高效率支持。(这一点挺有意思，和之前的基于batch再去处理streaming很不一样)

```
The contributions of this paper are as follows:
• we make the case for a unified architecture of stream and batch data processing, including specific optimizations
that are only relevant for static data sets,
• we show how streaming, batch, iterative, and interactive analytics can be represented as fault-tolerant
streaming dataflows (in Section 3),
• we discuss how we can build a full-fledged stream analytics system with a flexible windowing mechanism
(in Section 4), as well as a full-fledged batch processor (in Section 4.1) on top of these dataflows, by showing
how streaming, batch, iterative, and interactive analytics can be represented as streaming dataflows.
```
### 阅读
#### API怎么设计的?
core两个抽象：dataset和datastream，对应了batch和streaming

#### 执行流的抽象？
Although users can write Flink programs using a multitude of APIs, all Flink programs eventually compile down to a common representation: the dataflow graph.

#### 如何进行容错的？