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