```
包含对于big table论文的一些理解,会不断更新新的思考，以及与其他类似paper的对比。
```
## 论文思考
#### 1.	GFS可能出现重复记录或者padding，Bigtable如何处理这种情况使得对外提供强一致性模型？
操作日志通过唯一序号去重；sstable只索引最后一条成功的数据。

#### 2. 为什么Bigtable设计成Root、Meta、User三级结构，而不是两级或者四级结构？
因为三层结构能够满足几乎所有的业务需求数据量。四层的话会增加额外的请求。假设表大小为128m，元数据为1kb，那么2层的结构，最多支持128m＊（128m/1kb）=16tb的数据量，很小。

#### 3. 读取某一行用户数据，最多需要几次请求？分别是什么？
6次。假设客户端缓存了用户表位置，元数据表位置，根表位置的信息。首先请求用户表，过期，再去读取元数据表，发现过期，又去读取根表，也过期。这就是3次请求了，然后接着得从chubby－》根表－》元数据表三次请求得到实际的用户表的位置。

#### 4. 如何保证同一个tablet不会被多台机器同时服务？
ts在启动时需要从chubby获取互斥锁，来保证一个子表只能被一台ts服务。

#### 5. minor、merging、major这三种compaction有什么区别？
minor是为了防止memtable占用内存过多，每次minor生成一个sstable；merging则是对sstable跟memtable进行compaction，为了防止sstable文件数过多；major则是合并所有sstable，不包含已经删除的信息。

#### 6. Tablet在内存中的数据结构如何设计？
(row:string, column:string, timestamp:int64) ->string map<RowKey, map<ColummnFamily:Qualifier, map<Timestamp, Value>>>

#### 7. Tablet Server的缓存如何实现？
分为两级：scan cache和block cache。scan cache缓存来自sstable返回的key－value值；block cache缓存从gfs读取的sstable。前者用于提高重复读的效率，后者提高读key附近key的效率。

#### 8. tablet分裂的流程是怎样的？
首先是索引信息分割；然后是实际数据分割，每次分割在metadata表中添加一条entry，同时上报给master。如果上报失败，再次load的时候tablet server会通知到master。这里注意无论是master还是tablet server失败，都会重新load。

