```
包含对于gfs论文的一些理解,会不断更新新的思考，以及与其他类似paper的对比。
```
## 论文思考
### 一、问题
#### 1、为什么存储三个副本？而不是两个或者四个？
在可用性和性能之间的trade off，2副本可用性低，4副本复制成本较高，性能会偏低一些。

#### 2、GFS主要支持追加（append）、改写（overwrite）操作比较少。为什么这样设计？如何基于一个仅支持追加操作的文件系统构建分布式表格系统Bigtable？
基于实际场景考虑，追加操作占比很多；另外，追加操作比较搞笑；一致性维护也相对简单，最多是独到过期数据而不是错误数据。
至于实现bigtable，bigtable基于gfs存储两种数据：操作日志和sstable文件数据。操作日志有唯一id，直接追加即可，后续可以通过id进行过滤；sstable也是追加存储，但是bigtable只会索引最新的数据，从而间接实现了overwrite

#### 3、ChunkServer重启后可能有一些过期的chunk,Master如何能够发现？
重启之后会给master汇报信息，chunk是包含版本信息的，master可以通过版本信息发现是否过期。

#### 4、为什么要将数据流和控制流分开？如果不分开，如何实现追加流程？
数据流河控制流分开是gfs设计的一大特点。有几点好处：
	
	- 可以更好的利用网络的拓扑结构，由于gfs是先push所有数据到所有副本（从master拿到的信息），可以选择拓扑上最近的节点先push数据
	- 可以pipeline，贴一下论文给出的理论最高值：Without network congestion, the ideal elapsed time for transferring B bytes to R replicas is B/T + RL where T is the network throughput and L is latency to transfer bytes between two machines. 

不分开的话可以采用类似于传统分布式系统的replica技术来实现。吞吐会变低。

#### 5、租约（Lease）是什么？在GFS起什么作用？它与心跳（heartbeat）有何区别？
参考论文：[leases](http://web.eecs.umich.edu/~mosharaf/Readings/Leases.pdf)

租约在gfs默认初始值时60s（经验值）。起到的作用是：

```
从论文来看，主要是减轻master的负载，预先颁发租约之后，client只需要去primary提交修改数据的操作。不需要每个操作都访问master。
```
心跳可以传递续约信息。心跳是client（slave）向server（master）汇报自己的状态；租约是server（master）给client（slave）颁发一定期限的数据修改权。

#### 6、假设服务一千万个文件，每个文件1GB,Master中存储的元数据大概占用多少内存？
```
 The master maintains less than 64 bytes of metadata for each 64 MB chunk.
```
1GB/64MB = 1024 / 64 = 16。总共需要16 * 10000000 * 64 B = 10GB

#### 7、Chunk的大小为何选择64MB？这个选择主要基于哪些考虑?
参考论文2.5节：

```
大一点chunk size好处：
1、减少了client去访问master的请求
2、一个chunk可以包含很多数据满足多种操作的需求，client避免重复去读取数据进行操作
3、减少metadata量
坏处：
部分小文件可能会成为热点数据，因为一个chunk就够了，多个client去访问这一个chunk。

```

#### 8、GFS有时会出现重复记录或者补零记录（padding），为什么？
参考论文3.3:由于在append的时候，可能有部分chunk server 副本失败了，client会重试，导致了重复记录；如果在写数据的时候发现写之前会超过chunk max size（默认64mb），那么会先padding，然后创建一个新的chunk。

#### 9、负载的影响因素有哪些？如何计算一台机器的负载值？
网络 io cpu等，具体的负载值计算论文没找到对应的reference，各部分权重应该根据场景做调整

#### 10、Master如何实现高可用性？
参考论文5.1小节：快速恢复和复制是gfs使用的保证master和chunkserver的高可靠性策略。

```
1、client通过类dns别名的方式访问master，所以master切换是透明的
2、通过shadow server来保证对数据实时性要求不是那么高的client的读操作的高可用性
3、我理解本质上master保存的信息不算很多，所以能保证在类supervisor的monitor的拉取下快速恢复
```

#### 11、GFS Master需要存储哪些信息？Master数据结构如何设计？
参考论文2.6小节：

```
namespace、文件到chunk的映射以及chunk的位置信息

namespace采用的是B-Tree，对于名称采用前缀压缩的方法，节省空间；（文件名，chunk index）到chunk的映射，hashmap；chunk到chunk的位置信息，用multi_hashmap，因为是一对多的映射。
```

#### 12、如果ChunkServer下线后过一会重新上线，GFS如何处理？
参考论文第4.5小节：

```
有一个chunk version的概念：
（1）如果下线期间chunk version改变了，那么master能够感知到，然后通过lazy的gc策略回收
（2）如果没有改变，那么就直接作为chunk replica使用
（3）为了更好的保证：chunk version会发送给client用语校验；master在gc之前，读取数据会根据chunk version过滤
```

#### 13、磁盘可能出现位翻转错误，chunkserver如何应对？
参考论文5.2小节：

```
我们可以收获到通过设计checksum机制来防止数据“腐坏”。
1、对于读操作：读取所有的block，对check sum做验证
这里涉及一个性能问题：gfs读取会尽量将涉及的block范围缩小；check sum计算可以数据读取并行，比如读取了block1就可以直接做校验同时i/o启动去读取block2
2、append：增量计算，所以最后读取的时候肯定能发现
3、overwrite：需要先整体验证，然后再覆写（只需验证第1个和最后一个block,因为这两个block可能有部分是不会被覆写的）
```

### 二、各个章节的思考
#### 2.6
```
2.6.3:
The checkpoint is in a compact B-tree like form that can be directly mapped into memory and used for namespace lookup without extra parsing.有什么含义和提示？
```
#### 3.3
at-least-once语义。有一个疑问，如果刚好primary的chunk可以存放当前的数据，但是second的不能呢？这里可以通过工程实现来避免。

#### 5.3
对于一个分布式系统，应该能够很好的记录内部的一些状态用于问题追踪。gfs给了很实际的例子，rpc事件和chunk server重启，关闭等事件的记录。

