#### maven打包springboot项目的插件配置概览
##### jar包的术语背景：
1. normal jar: 普通的jar，用于项目依赖引入，不能通过java -jar xx.jar执行，一般不包含其它依赖的jar包。
2. fat jar: 也叫做uber jar，是一种可执行的jar(executable jar)，既包含自己代码中的class ，也包含第三方依赖的jar。
3. 不可执行，但包含第三方依赖的jar包，避免生成的jar与第三方引入后出现依赖版本冲突。
##### POM文件中的内置属性(Maven预定义可以直接使用)：
 ````
  ${basedir} 项目根目录
  ${version} 表示项目版本;
  ${project.basedir} 同${basedir};
  ${project.version} 表示项目版本,与${version}相同;
  ${project.build.directory} 构建目录，缺省为target;
  ${project.build.sourceEncoding} 表示主源码的编码格式;
  ${project.build.sourceDirectory} 表示主源码路径;
  ${project.build.finalName} 表示输出文件名称;
  ${project.build.outputDirectory} 构建过程输出目录，缺省为target/classes
 ````
 
##### 1. 第一类需求: 生成单个fat jar
使用springboot提供的maven打包插件spring-boot-maven-plugin即可，方便快捷，pom文件的配置如下:
````
<!-- 测试本地jar包引入和打包 -->
<!-- 项目管理的角度，尽量不使用本地jar包，搭建maven私服可以统一更新管理自研jar包 -->
<dependency>
    <groupId>cn.henry.test</groupId>
    <artifactId>local_test</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/local_lib/local_test.jar</systemPath>
</dependency>
<build>
    <plugins>
        <!-- 常规打包，flat jar，打出来的jar很大，不易于修改部分文件后增量发布 -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <!-- 作用:项目打成jar的同时将本地jar包也引入进去 -->
            <configuration>
                <includeSystemScope>true</includeSystemScope>
            </configuration>
        </plugin>
    </plugins>
</build>
````
##### 2. 第二类需求: 启动项和依赖包分离的fat jar
项目文件和依赖的jar包分离，因为引用的jar变动较少，项目发布时只需替换项目jar包或class即可，使用常规maven打包插件，
maven-jar-plugin, maven-dependency-plugin，输出为可执行的jar和lib包，pom文件的配置如下:
````
<!-- 测试本地jar包引入和打包 -->
<!-- 项目管理的角度，尽量不使用本地jar包，搭建maven私服可以统一更新管理自研jar包 -->
<dependency>
    <groupId>cn.henry.test</groupId>
    <artifactId>local_test</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/local_lib/local_test.jar</systemPath>
</dependency>
<build>
    <plugins>
        !-- 配置文件，依赖jar和可执行jar分离的包，便于文件替换发布 -->
        <!-- 复制依赖的jar包到指定的文件夹里，相当于：mvn dependency:copy-dependencies -DoutputDirectory=lib -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-dependencies</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>${project.build.directory}/lib</outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <!-- 配置依赖和启动类-->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <configuration>
                <archive>
                    <manifest>
                        <addClasspath>true</addClasspath>
                        <!-- MANIFEST.MF 中 Class-Path 加入前缀 -->
                        <classpathPrefix>lib/</classpathPrefix>
                        <!--指定入口类 -->
                        <mainClass>cn.henry.study.FileMessageServer</mainClass>
                    </manifest>
                    <manifestEntries>
                        <!-- 在Class-Path下添加配置文件的路径，本地jar的引入方式: dependency中的artifactid-version.jar -->
                        <Class-Path>lib/local_test-1.0.0.jar</Class-Path>
                        <!--这里表示jar路径加入到MANIFEST.MF-->
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>
````
##### 3. 第三类需求: 提供给第三方使用的，包含所有依赖的普通jar
解决项目jar中依赖与引用方jar包版本冲突的问题，提高jar包的易用性和独立性，缺点是打出来的包较大，jar包内置依赖不透明。
使用maven打包插件maven-shade-plugin，pom文件的配置如下:
````
在cmd中切换到local_test.jar包所在的目录，执行
mvn install:install-file "-DgroupId=cn.henry.frame" "-DartifactId=local_test" "-Dversion=1.0.0" "-Dpackaging=jar" "-Dfile=local_test.jar"
其中：
-DgroupId 为maven依赖的groupId
-DartifactId 为maven依赖的artifactId
-Dversion 为maven依赖的version
-Dfile 为local_test.jar包的文件名

引入安装后的local_test.jar包，maven依赖如下：
<!-- 项目管理的角度，尽量不使用本地jar包，搭建maven私服可以统一更新管理自研jar包 -->
<dependency>
	<groupId>cn.henry.frame</groupId>
	<artifactId>local_test</artifactId>
	<version>1.0.0</version>
</dependency>

<build>
    <plugins>
        <!-- 提供给第三方使用的，包含所有依赖的普通jar -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <!-- 加入启动类 -->
                        <!--<transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>xxx.xxx</mainClass>
                            </transformer>
                        </transformers>-->
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <filters>
                            <filter>
                                <artifact>*:*</artifact>
                                <excludes>
                                    <exclude>META-INF/*.SF</exclude>
                                    <exclude>META-INF/*.DSA</exclude>
                                    <exclude>META-INF/*.RSA</exclude>
                                </excludes>
                            </filter>
                        </filters>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

````
##### 4. 第四类需求: 提供给第三方使用的，仅包含项目代码的普通jar
通用模式，如果项目中有使用到第三方依赖，需要提供说明，否则会直接报class not found exception。
使用maven打包插件maven-jar-plugin，pom文件的配置如下:
````
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
            </manifest>
        </archive>
    </configuration>
</plugin>
````
注意打包插件的版本选择，尽量使用高版本，官方可能修复了已存在的问题，按项目的需求选取打包方式。<br>
maven的打包插件官网: http://maven.apache.org/plugins<br>
插件打包的原理：读取xml配置，组装成规范的jar文件<br>
jar规范可以阅读oracle的官方文档：https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html<br> 
###### 重点关注的部分: JAR文件格式中META-INF目录下的MANIFEST.MF文件，用于定义扩展和包相关的数据。主属性: Manifest-Version(MF文件版本号), Main-Class(包含main方法的类), Class-Path(执行这个jar包时的ClassPath，第三方依赖)
````
Manifest-Version: 1.0 
Main-Class: test.Main 
Class-Path: ./ ./lib/commons-collections-3.2.jar ./lib/commons-lang-2.3.jar ./lib/commons-logging-1.1.jar 
````
打包的一般做法是扫描项目所依赖的包，按照规范拼接Class-Path的value值，写入MF文件。但这种做法不够灵活，框架通常会从MF文件获取自定义信息，使用classloader动态加载依赖的jar包，并控制文件的合并，过滤等规则。
