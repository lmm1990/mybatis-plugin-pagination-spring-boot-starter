# mybatis-plugin-pagination-spring-boot-starter

mybatis 分页插件
[![maven](https://img.shields.io/maven-central/v/com.github.lmm1990/jtemplate)](https://mvnrepository.com/artifact/io.github.lmm1990/mybatis-plugin-pagination-spring-boot-starter)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![java version](https://img.shields.io/badge/JAVA-8+-green.svg)

## 💿 快速开始

### Maven 资源

```xml
<dependency>
    <groupId>io.github.lmm1990</groupId>
    <artifactId>mybatis-plugin-pagination-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

## Gradle 依赖

```gradle
implementation 'io.github.lmm1990:mybatis-plugin-pagination-spring-boot-starter:1.0'
```

### 在代码中使用

```
//初始化分页参数
PaginationHelper.init(1,5);
//多表联查时，建议手写查询总数量sql性能更佳
PaginationHelper.init(2,10,"select 999;");
//分页结果
Page<TestInfo> result = testMapper.listByStatus(1);
```