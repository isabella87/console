# console --swing编写的后台界面

```
一，界面的编写及事件处理规则
  1，在resources目录下编写  *.rc 文件，并排版界面；（该rc文件的名称与后期编写的界面初始化及事件处理的java类的文件名要相同，是根据同名找到对应的界面）
  2，调用该方法initUi初始化界面，如果有特殊操作重写该方法；
  3，调用done方法处理响应事件，该方法默认处理结果是关闭当前窗口。如果有特殊操作或者需要弹出其他界面，则重写该方法。
 
二，一般任务分四步，三个线程走完。(串行异步，链式异步)
  1,连接服务的http线程，比如此处的触发signIn方法；
  2，处理异步回调数据的公共线程；
  3，根据异步回调结果数据更新界面的UI线程。
  4，拦截异常并处理异常的公共线程；
  
三，系统内分4类线程
1，主线程只有一个负责启动系统，然后一直处于等待状态（等待界面被关闭，调用system.exit(),关闭所有线程并退出）；
2，http线程，负责http连接并响应相关交互；
3，一般线程（实际为线程池），用于解析数据，处理数据转换事务；
4，UI线程（实际为线程池），用于处理界面相关事务。

```

