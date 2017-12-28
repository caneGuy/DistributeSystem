```
<<Spanner: Become a sql system>>阅读笔记
```

## 基本概念
#### 1、distributed union
```
目的是为了让scan操作涉及的shard越少越好，其实分布式sql引擎这个是基本的优化点，只是各家实现会有差异。
```

#### 2、query range extraction（sharding裁剪核心）
```
- Distributed range extraction figure out which table shards are referenced by a query.
- Seek range extraction determines what fragments of a relevant shard to read from the underlying storage stack.避免scan一个大range。
- Lock range extraction determines what fragments of a table are to be locked (pessimistic txns) or checked for potential pending modifications (snapshot txns).将锁的粒度细化。
```
**小结：**

```
使用了两种方式：compile time的rewrite和run time的filter tree.
简单理解就是，compile time先生成一系列相关的scan，到了runtime再对实际的scan进行过滤。
```

#### 3、distributed apply
```
常用场景：索引表和数据表join（分布式场景下出现的）
 － 将input做batch发送到remote shard server
 － 在remote shard server做local join
```

#### 4、query restart
```
支持查询重启，当然是对于瞬时的异常容错：cpu、network，值得一提的是spanner本身会不断的loadbalance，所以数据的move导致的失败也是属于瞬时异常。

难度：
1、动态分区
2、非幂等操作
3、不同版本的兼容性
```

## 核心问题
#### 1、如何保证**distributed**查询的效率？
#### 2、如何决定去哪些server执行查询？如何减少scan的范围？如何细化锁的粒度？
基于range extraction。
#### 3、为什么要支持query restart？
paper中提了很多点。主要从用户使用和系统设计本身简洁性做了考虑。比较重要的是，支持了restart必然有其他限制和不能提供的保证等等。这些细节没说，但是思路是值得注意的。在设计系统的时候，需要做好这种trade off。

## Reference
1、[the morning paper](https://blog.acolyer.org/2017/07/03/spanner-becoming-a-sql-system/)