# Java Base Learning Project

这是一个基于 **Spring Boot 3.2.x** (由于 3.5.3 尚未发布，采用当前最新稳定版) 和 **JDK 17** 构建的进阶学习项目。

本项目严格遵守 `gemini.md` 工程规范，旨在帮助开发者从底层原理（集合、JUC）过渡到工程实践（Spring Boot）。

## 📚 学习路线图 (Learning Roadmap)

### Phase 1: 内功心法 (Collection & Data Structures)
- **目标**: 理解数据存储底层原理。
- **代码位置**: `com.learning.javabase.lab.collection`
- **实践**:
  - [x] 手写简易版 `ArrayList` (`MyArrayList`)
  - [ ] Debug `HashMap` 源码
  - [ ] 理解红黑树与链表的转换

### Phase 2: 并发编程 (JUC & Multithreading)
- **目标**: 掌握多线程与高并发基石。
- **代码位置**: `com.learning.javabase.lab.concurrent`
- **实践**:
  - [x] 自定义线程池配置 (`ThreadPoolConfig`)
  - [ ] 手写多线程下载器
  - [ ] 深入理解 `synchronized` 与 `Lock`

### Phase 3: 框架基石 (JVM & Reflection)
- **目标**: 理解 Spring 的 "黑魔法"。
- **代码位置**: `com.learning.javabase.lab.jvm`
- **实践**:
  - [ ] 自定义 ClassLoader
  - [ ] 模拟 Spring Bean 初始化流程

### Phase 4: Spring Boot 工程化
- **目标**: 掌握企业级应用开发规范。
- **代码位置**: `com.learning.javabase` (主工程结构)
- **结构**:
  - `controller`: 接口层
  - `service`: 业务逻辑层
  - `dal`: 数据访问层
  - `integration`: 外部集成
  - `common`: 公共组件

## 🛠 工程结构说明

```text
src/main/java/com/learning/javabase
├── common              // 公共组件
│   ├── constants       // 常量定义
│   ├── exception       // 全局异常
│   └── util            // 通用工具
├── config              // 配置类 (线程池、Web配置等)
├── controller          // Web 接口层
├── service             // 业务接口
│   └── impl            // 业务实现
├── dal                 // 数据层
│   ├── model           // 数据库模型 (DO)
│   └── dao             // 数据访问 (Mapper)
├── integration         // 外部系统集成 (Redis, MQ)
└── lab                 // [学习实验室] 用于存放 Phase 1-3 的练习代码
    ├── collection
    ├── concurrent
    └── jvm
```

## 🚀 快速开始

1. 确保安装 JDK 17+。
2. 运行 `JavaBaseApplication` 启动项目。
3. 访问 `http://localhost:8080` 验证服务。
