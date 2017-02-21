## mybatis总是提示绑定不了
因为spring-boot发布方式的特殊性，mybatis的mapper配置映射文件不会自动打包到发布
jar包里边。此时，请求总是会提示如下错误。

```
org.apache.ibatis.binding.BindingException: Invalid bound statement (not found)
```
应该在pom.xml的 build 节点下添加如下配置。
```
<resources>
    <resource>
        <directory>src/main/java</directory>
        <includes>
            <include>**/*.xml</include>
        </includes>
    </resource>
    <resource>
        <directory>src/main/resources</directory>
    </resource>
</resources>
```