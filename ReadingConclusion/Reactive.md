```
Reactive:
只是作为记录，对reactive不是很熟悉
```
## Reactive programing
- Reactive programming有三个关键字：异步，数据流和事件驱动
- Reactive programming属于一种编程范式，和面向对象，命令式编程是一个层次的
- 任何编程范式都需要考虑使用场景

## Reactive system
- 对分布式系统的更高层次的抽象
	- Responsive:分布式系统需要能及时响应
	- Resilient:分布式系统的故障恢复能力
	- Elastic:可扩展性
	- Message Driven:基于消息进行系统解耦
	
## Message Driven vs Event Driven
可以理解为：消息通信是模块间，而事件驱动可以理解为是单个进程内的

## Reactive programing vs Reactive system
没有直接关系，但是可以使用Reactive programing来实现Reactive system。
Akka是个好例子。

## 参考
[lightend white paper](https://www.lightbend.com/reactive-programming-versus-reactive-systems)




