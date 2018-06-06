```
<<Class loader>>
会从源码分析class loader。对类加载机制大部分参考《深入java虚拟机》
```
### Class.forName vs ClassLoader.loadClass
- Class.forName()除了加载到jvm之外，会进行解释，且执行static块
- ClassLoader.loadClass()：只把.class文件加载到jvm，只在newInstance才会执行static块
- Class.forName(name, initialize, loader)带参可以控制是否加载static

### 双亲委派的意义？
- 将所有类都往父classloader加载，避免出现多份共用的类
- 出于安全考虑，可以避免用户修改安全的类，随意加载

