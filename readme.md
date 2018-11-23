# 简介

> 为深入学习httpClient而创建的学习项目,httpClient版本为(4.5.x)

项目下包含demo-server文件夹，是使用go语言编写的简单服务器。主要是为了配合
httpClient去请求服务器，以得到我想要的内容

> 不使用java编写是因为go的编写执行都很简单，不需要做太多的步骤，打开就写。

项目代码是大部分来自于httpClient的官方文档，一章一章的编写（抄）代码。系统的学习一下http client。

包按照章节顺序排序，每个包的类也使用编号为前缀，是为了更好的一章一章整理复习。

每个类包含响应注释（官方文档机翻）及自己的笔记。后续可能会有自己的代码

# 编译&运行

每个类包含一个main方法用于执行，在执行前[./demo-server/bin](./demo-server/bin)下面对应系统（linux/mac/win）的可执行文件即可开启demo服务器。

java方面查看需要的章节需要的类，直接执行main方法即可。