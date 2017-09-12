### Lease协议实现
简单的租约协议实现，封装了租约管理，自动续约，异步回调租期内的操作等功能。

##### cane.distribute.lease.Lease
用于表示一个replica端的租约

##### cane.distribute.lease.LeaseManager
主要操作：new lease;update lease;remove lease

##### cane.distribute.lease.LeaseClient
replica端，可以向master申请租约，续约等
	
##### CallBack
LeaseClient端使用的，在每次调用client的