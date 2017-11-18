```
mapreduce 论文阅读的一些感受。先说几个大的方面：
mr最主要目标是为了提高编程的灵活性和易用性
```
#### 1、mr给用户抽象了哪些内容？
- worker分配和调度
- 跟进每个worker上的task进度
- 数据传递（shuffle）
- failure透明化（容错）

#### 2、mr的scalaiblity？
线性可扩展，从模型也能看出来，有多少台机器就可以并行的增加多少个worker

#### 3、mr作业的性能瓶颈是什么？
shuffle。一般来说，我们考虑cpu memory disk network这几个方面。mr作业瓶颈主要是在shuffle过程的网络数据交换。
作者在04年提出过

#### 4、map task和reduce task基本点？
- 论文提出数据来源是gfs
- task数目会可能比worker数目多
- worker执行完一个task之后会继续被调度其他map task（假如还有的话）
- map task输出是分成r（r是reduce task数目）个存到worker的本地磁盘
- 所有map task执行完才会触发reduce task
- reduce task通过master得到maptask具体的输出，然后通过rpc去获取
- reduce task会生成r份输出到gfs

#### 5、mr框架对网络使用的优化？
- 一般mr worker是和gfs进行混布的，所以通常会利用locality去获取输入数据
- map生成的中间数据只会通过网络传输一次
- map输出的一个文件会包含很多个key，避免过多小文件传输

#### 6、mr load balance？
- 尽量使得每个task处理的数据在16m到64m之内
- reduce task的数目受限于用户的输出要求
- task数目大大多于worker的数目好处是，有一台慢机器可以少处理一些数据，总时间缩短

```
There are practical bounds on how large M and R can
be in our implementation, since the master must make
O(M + R) scheduling decisions and keeps O(M ∗ R)
state in memory as described above. (The constant factors
for memory usage are small however: the O(M ∗R)
piece of the state consists of approximately one byte of
data per map task/reduce task pair.)
Furthermore, R is often constrained by users because
the output of each reduce task ends up in a separate output
file. In practice, we tend to choose M so that each
individual task is roughly 16 MB to 64 MB of input data
(so that the locality optimization described above is most
effective), and we make R a small multiple of the number
of worker machines we expect to use. We often perform
MapReduce computations with M = 200, 000 and
R = 5, 000, using 2,000 worker machines
```

#### 7、容错？
论文3.3节

- master容错，checkpoint文件。但是大部分时候不是master挂，所以可以不开启备份机制。而是客户端retry
- worker容错

```
1、只重新执行失败的map task和reduce task。主要是master不挂，很多信息都记录了。
2、记住一个关键点：操作的幂等性，如果非幂等的怎么重试？
```

#### 8、reduce在写结果中间失败呢？
GFS has atomic rename that prevents output from being visible until complete.so it's safe for the master to re-run the Reduce tasks somewhere else.
    
#### 9、如果master分配同一个map task到两个worker呢？
master只会记录其中一个的结果

#### 10、如果master分配了同一个reduce task到两个worker呢？
写结果的时候gfs能够保证只有一个成功

#### 11、mr不适合的场景？
```
  Small data, since overheads are high. E.g. not web site back-end.
  Small updates to big data, e.g. add a few documents to a big index
  Unpredictable reads (neither Map nor Reduce can choose input)
  Multiple shuffles, e.g. page-rank (can use multiple MR but not very efficient)
  More flexible systems allow these, but more complex model.
  ```


