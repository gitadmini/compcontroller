### compcontroller
电脑遥控器

---

可以通过手机来远程控制电脑。

---

![操作说明](https://raw.githubusercontent.com/gitadmini/common/master/compcontroller.png)

---

### 安装与使用：
* 安装[java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)环境，版本号是jdk-8xxxxx，可以百度一下怎么[安装](https://jingyan.baidu.com/article/0202781175839b1bcc9ce529.html)。
* 安装maven，可以百度一下怎么[安装](https://jingyan.baidu.com/article/6c67b1d646ae842786bb1e7a.html)。
* 安装compcontroller。
 1. [下载](https://github.com/gitadmini/compcontroller/archive/master.zip)文件，并解压到任意目录。
 2. 编译并运行：运行cmd窗口，cd到compcontroller目录（例如：cd F:\compcontroller），切换磁盘（例如：F:），执行打包命令mvn clean package -Dmaven.test.skip=true，成功后cd到target目录（cd target），若不存在rec目录则新建一个（mkdir rec），运行程序java -jar relax-0.0.1-SNAPSHOT.jar。
* 将手机连入电脑所在的局域网中，打开浏览器输入共享电脑的局域网地址，例如：192.168.11.101。

