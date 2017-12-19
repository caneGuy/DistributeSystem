```
<<Spanner: Become a sql system>>阅读笔记
```
## 核心问题
#### 1、如何保证**distributed**查询的效率？
#### 2、如何决定去哪个server执行查询？
#### 3、为什么要支持query restart？

## 基本概念
#### 1、distributed union
```
目的是为了让scan操作涉及的shard越少越好，其实分布式sql引擎这个是基本的优化点，只是各家实现会有差异。
```

#### 2、query range extraction（sharding裁剪核心）
```
- Distributed range extraction figure out which table shards are referenced by a query.
- Seek range extraction determines what fragments of a relevant shard to read from the underlying storage stack.
- Lock range extraction determines what fragments of a table are to be locked (pessimistic txns) or checked for potential pending modifications (snapshot txns).
```

#### 3、distributed apply
```
常用场景：索引表和数据表join（分布式场景下出现的）
 － 将input做batch发送到remote shard server
 － 在remote shard server做local join
```

## Reference
1、[the morning paper](https://blog.acolyer.org/2017/07/03/spanner-becoming-a-sql-system/)