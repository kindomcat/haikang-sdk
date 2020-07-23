#注意事项
1.由于海康的sdk HcNetSDK类 内容不一样，不能将windows和linux两套代码合成一个开发。

2.开发时最好配置一个maven私库来引入外包jar包，不然打包非常麻烦。

3.测试时记得去海康摄像机界面设置启动布防，不然无法收到布防消息。

4.如果有其它问题可通过qq:361117441联系我。

5.如果需要查看效果，请通过docker操作。
拉取镜像： docker pull registry.cn-chengdu.aliyuncs.com/xzlxzl/haikangsdk-linux-demo 
启动镜像： docker run docker run -d -p 8888:8888 --name haikang [容器ID]
测试链接：
    get请求    http://192.168.1.180:8888/liunx/test1?username=admin&password=Senscape&ip=192.168.1.64
    username:摄像机登录名
    password:摄像机登录密码
    ip:摄像机ip
