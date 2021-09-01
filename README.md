# xpocket-plugin-jol
The JOL Tool plugin for xpocket.

###简介
JOL(Java Object Layout)是分析 JVM 中对象布局的小工具箱。这些工具大量使用 Unsafe、JVMTI 和 Serviceability Agent (SA) 来解码实际的对象布局、足迹和引用。这使得 JOL 比依赖堆转储、规范假设等的其他工具更加准确.

###操作指南
####使用
`use jol@OPENJDK`
####帮助
`help`
####可用选项
`internals                      Show the object internals: field layout and default contents, object header(显示对象内部:字段布局和默认内容 ，对象头).`

`externals                      Show the object externals: the objects reachable from a given instance(显示对象外部:从实例可达的对象).`

`footprint                      Estimate the footprint of all objects reachable from a given instance(估计从实例可达的对象的引用信息).`

`estimates                      Simulate the class layout in different VM modes(模拟在不同VM模式下类的布局).`

`heapdumpstats                  Consume the heap dump and print the most frequent instances(使用heap dump并打印最频繁的实例).`

`shapes                         Dump the object shapes present in JAR files or heap dumps(Dump JAR文件或heap dumps中存在的对象结构).`

`string-compress                Consume the heap dumps and figures out the savings attainable with compressed strings(使用heap dumps并计算 压缩字符串可获得的节省).`

####示例
Linux/macOS环境 : internals -path:/home/user/test.jar ClassX

Windows环境 : internals -path:D:\\test.jar ClassX \n\t 或者 internals -path:D:////test.jar ClassX

---
JOL开源地址:https://github.com/openjdk/jol

XPocket项目地址:https://github.com/PerfMa/XPocket

XPocket官网:https://xpocket.perfma.com
