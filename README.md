# FUOJ Code Sandbox

FUOJ Code Sandbox 是一个基于 Spring Boot 的 Java 代码沙箱服务，用于接收用户提交的 Java 代码、编译执行代码，并返回每组输入对应的输出结果和基础判题信息。

当前服务接口默认使用 `JavaNativeCodeSandbox` 原生进程执行实现；项目中也保留了 `JavaDockerCodeSandbox` Docker 隔离执行实现，便于后续切换和增强安全隔离能力。

## 功能特性

- 提供 HTTP 接口执行 Java 代码
- 支持多组运行参数输入
- 自动保存、编译、运行和清理用户代码文件
- 支持运行超时控制，默认 5 秒
- 支持最大堆内存限制，原生执行命令中默认为 `-Xmx256m`
- 提供 Docker 版本沙箱实现示例
- 内置简单的接口鉴权请求头

## 技术栈

- Java 8
- Spring Boot 2.7.6
- Maven
- Hutool
- Docker Java 3.3.0
- Lombok

## 环境要求

请先安装：

- JDK 8
- Maven 3.x

如果需要运行 Docker 沙箱实现，还需要：

- Docker
- 可访问 Docker Daemon 的本地环境
- `azul/zulu-openjdk-alpine:8` 镜像，首次运行会尝试自动拉取

## 快速启动

在项目根目录执行：

```bash
mvn spring-boot:run
```

服务默认监听：

```text
http://localhost:8090
```

健康检查：

```bash
curl http://localhost:8090/health
```

正常返回：

```text
ok
```

## 接口说明

### 执行代码

```http
POST /executeCode
```

请求头：

| Header | 说明 | 示例 |
| --- | --- | --- |
| `Content-Type` | 请求体格式 | `application/json` |
| `auth` | 简单鉴权密钥 | `secretKey` |

请求体：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `code` | string | 是 | 用户提交的 Java 代码，主类名必须为 `Main` |
| `language` | string | 是 | 语言标识，当前实现主要支持 `java` |
| `inputList` | string[] | 是 | 多组命令行参数，每个字符串会作为一次运行参数 |

示例：

```bash
curl -X POST http://localhost:8090/executeCode \
  -H "Content-Type: application/json" \
  -H "auth: secretKey" \
  -d '{
    "language": "java",
    "inputList": ["1 2", "10 20"],
    "code": "public class Main { public static void main(String[] args) { int a = Integer.parseInt(args[0]); int b = Integer.parseInt(args[1]); System.out.println(a + b); } }"
  }'
```

响应示例：

```json
{
  "outputList": [
    "3",
    "30"
  ],
  "message": null,
  "status": 1,
  "judgeInfo": {
    "message": null,
    "memory": null,
    "time": 72
  }
}
```

响应字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `outputList` | string[] | 每组输入对应的标准输出 |
| `message` | string | 编译或运行异常信息 |
| `status` | integer | 执行状态，当前正常执行返回 `1` |
| `judgeInfo.time` | long | 多组运行中的最大耗时，单位毫秒 |
| `judgeInfo.memory` | long | 内存占用信息，原生执行实现暂未采集 |

## 项目结构

```text
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/fantasy/fuojcodesandbox
│   │   │   ├── controller
│   │   │   │   └── MainController.java
│   │   │   ├── model
│   │   │   │   ├── ExecuteCodeRequest.java
│   │   │   │   ├── ExecuteCodeResponse.java
│   │   │   │   ├── ExecuteMessage.java
│   │   │   │   └── JudgeInfo.java
│   │   │   ├── security
│   │   │   ├── utils
│   │   │   │   └── ProcessUtil.java
│   │   │   ├── CodeSandbox.java
│   │   │   ├── JavaCodeSandboxTemplate.java
│   │   │   ├── JavaNativeCodeSandbox.java
│   │   │   └── JavaDockerCodeSandbox.java
│   │   └── resources
│   │       ├── application.yml
│   │       └── testCode
│   └── test
│       └── java/com/fantasy/fuojcodesandbox
└── tmpCode
```

## 核心流程

`JavaCodeSandboxTemplate` 定义了代码执行模板流程：

1. 将用户代码保存到 `tmpCode/{uuid}/Main.java`
2. 使用 `javac -encoding utf-8` 编译代码
3. 对 `inputList` 中的每组输入参数分别执行 `java -cp ... Main ...`
4. 收集标准输出、错误输出、耗时等执行信息
5. 删除本次执行产生的临时代码目录

## 本地测试

运行测试：

```bash
mvn test
```

也可以直接使用 `src/main/resources/testCode` 下的示例代码构造请求，验证正常计算、超时、内存溢出、读写文件和执行系统命令等场景。

## 注意事项

- 当前 HTTP 接口默认注入的是原生进程沙箱，用户代码会在宿主机 Java 进程中派生执行，不能作为强安全隔离方案直接暴露到公网。
- `auth: secretKey` 只是演示级鉴权，生产环境应替换为更可靠的认证和授权机制。
- 用户提交代码的主类名必须是 `Main`，否则编译或运行会失败。
- `inputList` 中的每个元素都会作为命令行参数传递给 `main(String[] args)`。
- Docker 沙箱实现提供了内存、CPU、只读根文件系统和禁用网络等限制示例，但默认接口尚未切换到该实现。

## 相关配置

服务端口位于 `src/main/resources/application.yml`：

```yaml
server:
  port: 8090
```
