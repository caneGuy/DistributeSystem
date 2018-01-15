```
<<Apache Flink™: Stream and Batch Processing in a Single Engine>> 阅读笔记
```
### 解决什么问题？
主要是解决之前各种框架或者实现在支持streaming计算的时候，存在要么有延时，要么有复杂度过高的问题。
同时，基于streaming的模型，抽象出对batch等运算的高效率支持。(这一点挺有意思，和之前spark的基于batch再去处理streaming很不一样)

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
对标spark的dag。

#### Operators?
##### DataSet
批处理作业，会通过一个查询优化器转化为底层dataflow的runtime。比较好奇query optimizer这里怎么实现的。以及它的cbo实现细节。
另外，有以下几点比较有意思：

```
- To handle arbitrary objects, Flink uses type inference and custom serialization mechanisms. 
- By keeping the data processing on binary representation and off-heap, Flink manages to reduce the garbage collection overhead, and use cache-efficient and robust algorithms that scale gracefully under memory pressure.
-  Flink introduces further novel optimisation techniques such as the concept of delta iterations, which can exploit sparse computational dependencies
```
##### DataStream
流式作业的api。主要下面几个概念比较有趣：

```
- Process time:节点的timestamp
- Event time:消息自带timestamp
- Ingres time:flink给输入的消息设置一个有序的timestamp

基于上面的几种时间来划分窗口，然后处理。注意在处理消息的时候会有如下问题，所以才引申出上面的概念：
－ 用户希望根据消息时间来分段
－ 消息无序的话，基于节点的时间来分段，会出现不同节点，可能两个消息在a节点是一个window，传递到b变成了两个window，有可能会导致结果出错。

```

#### data streams?
Flink’s intermediate data streams are the core abstraction for data-exchange between operators.

#### 如何进行容错的？
这个问题比较大，有另外一篇论文介绍。


### 疑问？
#### 如何基于checkpointing实现exactly once？
可以这么理解：checkpoint成功是最后一个状态操作符处理完barrier之后存储成功才算的。那么通过这个机制就可以保证每条记录在输出到结果之前只处理过一次（因为输出结果了就已经checkpoint了）。