```
<<<Designing data instensive>>>阅读笔记
```
## Chapter1
```
主要介绍了一个好的数据密集型系统的三个基本准则：可靠，可扩展，可维护。对三个方面分别进行了展开，介绍了一些基本的概念。
```
### 1、可靠（Reliability）
在硬件，软件，人为错误发生的情况下，系统要能够依旧保证一个正常的状态。
通过软件设计，完备的测试集来避免软硬件错误。
### 2、可扩展（Scalability）
要保证可扩展，需要有方法去衡量系统的负载。常用的有：吞吐量和响应时间（注意和延时的区别）。
对于响应时间，percent line是个比较好用的衡量方式。
思考一个问题：
延时降低一定能够提高吞吐量吗？（答案是不能，延时分为处理时间和网络延时等等，降低了网络延时并不能提高系统的吞吐量）
### 3、可维护（Maintainability）
提了一个概念，“抽象”。系统要有一个好的抽象，就能够更好的实现可扩展性。

## Chapter2
```
介绍了数据模型：层级模型－》网络模型/关系模型－》文档模型－》基于图的数据模型
介绍了查询语言的类型：imperative vs declarative。
其实基本点就是：在关系模型和文档模型做trade off；在imperative和declarative之间做trade off。
```
### 1、Relational vs Document model
关系型模型很好用，但是有一些场景会不太方便。

```
- 可扩展性
- 有些query性能低
- schema less数据模型
- 开源
```
文中举了linkde的resume作为例子：对于一些自定义的object，关系模型需要自己写orm或者是利用orm框架来完成某些字段的映射，但是文档模型就可以直接表示对象了。

各种数据模型最大的争论就是如何处理好join之类的操作。总结一下模型之间的区别（主要是文档模型和关系模型）

```
1. 文档模型和关系模型和以前的模型区别在于，都定义了identify id用于做reference方便runtime做join操作。
2. 文档模型却没法很好的支持many-many和many-one情况之下的嵌套数据的reference。
3. 文档模型更好的支持one-many关系。
4. 文档模型是scheme-on-read的模型，也就是read的时候需要额外操作去处理scheme相关内容，可以实现query向后兼容性；关系模型可以理解为是scheme－on－write，所以scheme的变化会引起部分query失效。
5. 文档模型能更好的利用data locality，但是update的时候需要全部更新。其实这里个人理解也是需要根据业务场景做trade off。
```
### 2、Query language
1、 Declarative vs Imperative

```
An imperative language tells the computer to perform certain operations in a certain order. You can imagine stepping through the code line by line, evaluating conditions, updating variables, and deciding whether to go around the loop one more time.In a declarative query language, like SQL or relational algebra, you just specify the pattern of the data you want—what conditions the results must meet, and how you want the data to be transformed (e.g., sorted, grouped, and aggregated)—but not how to achieve that goal. It is up to the database system’s query optimizer to decide which indexes and which join methods to use, and in which order to execute various parts of the query.
可以理解就是declarative将部分优化交给了query engine。也就是常说的query optimizer。
```

2、 MapReduce

```
ref: mapreduce论文
一种编程模型，这小节主要是distribution sql比较有趣。
```

### 3、Graph-like models
最后小节介绍了图模型，对于many-many relation，图数据库是比较好的db。同时介绍了基于图数据库的query语言，这里没细看。

###### 总结
提出的观点就是在使用db之前，先理解自己数据的relation，才能选择合适的db。另外，各种模型本质上对scheme的要求就是application是在读还是在写的时候对scheme有一个强要求。

## Chapter3
```
从数据库的实现来理解数据的存储和查询
```
### 1、Data Structures That Power Your Database
#### 1、hash index
```
介绍了hash索引：
常见使用是结合append only的data file使用。添加或者更新key value数据都是append的模式。
好处：
－ append的快
－ 并发控制方便，不会说update一半导致旧数据和新数据的不可理解（gfs的undefined）行为
－ 可以不断的merge fragment来控制占用空间的大小
坏处：
－ range查询慢
－ 如果索引不能完全存储在内存，会严重影响性能（因为hash table在disk存储的话性能很差）

append only常见的做法就是：分段＋后台的compaction。
```

#### 2、sstable/lsm-tree
```
sstable第一次看到是在bigtable的论文里面。ddia中这一小节介绍了很多系统都使用了这个概念：cassandre hbase lucene leveldb rocksdb等等。
基本思想：
- sorted key：基于内存的avl 或者是 rb－tree。组成memtable
- memtable超过阈值之后就刷入disk形成segment
- 为了防止segment过多，后台进行compaction

提到了**bloom filter**，这个在分布式系统很常见的技术。

```

#### 3、b-tree
```
- 固定size的page来管理索引数据
- 通过wal来保证数据的一致性

如何优化性能？
- copy on write方式来避免wal
- 兄弟指针（叶子节点包含前后节点的指针，避免回溯到父节点）
- 平铺叶子page，避免过多的disk seek（但是管理麻烦－lsm的后台merge就能很好的自动将数据顺序化）

```

#### 4、B-tree vs log-structre
```
2个知识点：write amplification；ssd对于over write的次数有限制（如果要耗尽了）
b-tree的缺点：
- 相对于lsm有更多的write  amplification
- 压缩比不如lsm
- lsm顺序写比b tree的随机写要高效

lsm缺点：
- compaction和实时读写会强占disk的吞吐
- lsm可能存在一个key在多个segment（append模式），一致性可能有问题
- segment可能过多，由于磁盘吞吐限制不够
- 高峰时期的性能不可预测（可能看avg指标很好，其实高峰期特别慢）

```
### OLTP VS OLAP

## Chapter 4
```
主要讲在分布式系统中，如何设计好的编解码方式。重点考虑:
1. 兼容性
2. 空间存储
3. 性能
```	
