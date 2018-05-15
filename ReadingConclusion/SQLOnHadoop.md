```
SQL on hadoop systems?
```
#### 1.基于MPP VS 基于DAG

```
- mpp中间数据不需要落盘，完全的pipeline
- mpp容错性没有dag做的好
```

#### 2.SQL ON HADOOP系统的基本模块
和传统rdbms其实很多地方很像，关键点在于之前的一些优化思路没有考虑分布式场景。而这个也是大部分sql on hadoop系统重点需要考虑的。

##### 执行计划解析器
query－》解析－》逻辑计划－》物理计划。这一步语法解析有成熟的方案和开源库。不需要自己造轮子
##### 优化器
```
－ join的优化是很有看点的一部分。不过基本的优化思路大部分系统也差不多。map side/smj/broadcast join等。大部分系统都支持
－ 如何合理使用cbo也是一大看点。目前大部分系统都还在初级阶段
```
##### 提高底层执行效率
cpu和io

```
以spark为例子：
－ code gen
－ 向量化
```
##### 存储
sql on hadoop支持比较好的是列式存储。能比较好的支持olap分析场景