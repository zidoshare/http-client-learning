# 简介

> 为深入学习httpClient而创建的学习项目,httpClient版本为(4.5.x)

项目下包含[demo-server](./src/main/resources/demo-server)文件夹，是使用go语言编写的简单服务器。主要是为了配合
httpClient去请求服务器，以得到我想要的内容

> 所有需要服务器的测试代码，都会[自动启动服务器](./src/main/java/site/zido/httpclient/ServerUtils.java)。无需手动启动

> 不使用java编写是因为go的编写执行都很简单，不需要做太多的步骤，打开就写。

为了跨平台和使用简单，主分支上默认将已经编译好的服务端添加到了[demo-server](./src/main/resources/demo-server)下的bin目录中
如果你自己安装了go，可自行编译。因为二进制文件略大导致clone较慢，我提供了一个no-bin分支,可以执行
`git clone -b no-bin https://github.com/zidoshare/http-client-learning.git` 进行更快的clone

> 分支内没有二进制文件，所以需要手动进行构建.确保go版本为1.11+后直接执行[./src/main/resources/demo-server/build.sh](./src/main/resources/demo-server/build.sh)即可。

项目代码是大部分按照httpClient的官方文档编写（抄）代码。系统的学习一下http client。

每个类包含响应注释（官方文档机翻）及自己的笔记。后续可能会有自己的代码

所有的笔记在[docs](./docs)目录下

# 编译&运行

查看需要的test方法,执行即可