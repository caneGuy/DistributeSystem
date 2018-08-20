```
Google dremel paper 论文阅读思考笔记
```
## 数据模型
### Repetition Level
```
This is what repetition level is for: it is the level at which we have to create a new list for the current value. In other words, the repetition level can be seen as a marker of when to start a new list and at which level.
```

### Definition Level
```
This is what the definition level is for: from 0 at the root of the schema up to the maximum level for this column. When a field is defined then all its parents are defined too, but when it is null we need to record the level at which it started being null to be able to reconstruct the record.
```

### Parquet实现
#### 存储格式
一些概念：

```
- page:压缩和编码的单元，设计schema 的时候不需要考虑这个

- column chunk:表示一列，repetion level和define level
 
- row group:列块组成

```
列式存储：压缩率，skip无关列，适合olap常用模式（分组，排序，聚合）
#### 对象模型转换器
#### 对象模型

## 参考
http://blog.csdn.net/dc_726/article/details/41627613 dremel模型详解
https://blog.twitter.com/engineering/en_us/a/2013/dremel-made-simple-with-parquet.html twitter详解
