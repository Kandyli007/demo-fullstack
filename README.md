# TaskBoard 全栈示例

前后端分离的任务管理示例项目：后端提供任务 CRUD REST API，前端使用 Element Plus 构建管理界面。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3.5、Java 21、Spring Web、Jakarta Validation、JUnit 5 + MockMvc |
| 前端 | Vue 3、TypeScript、Vite 7、Element Plus、Axios |

## 目录结构

```
demo-fullstack/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/example/demo/
│       │   │   ├── TaskBoardApplication.java
│       │   │   ├── config/WebConfig.java          # CORS 配置
│       │   │   ├── controller/TaskController.java # REST API
│       │   │   ├── dto/TaskRequest.java           # 请求体 + 参数校验
│       │   │   ├── exception/                     # 统一异常处理
│       │   │   ├── model/                         # Task / TaskStatus
│       │   │   └── service/TaskService.java       # 内存存储
│       │   └── resources/application.yml
│       └── test/java/com/example/demo/
│           └── TaskApiIntegrationTest.java
└── frontend/                         # Vue 3 前端
    ├── package.json
    ├── vite.config.ts                # 开发代理 /api -> :8080
    ├── tsconfig.json
    └── src/
        ├── App.vue
        ├── main.ts
        ├── api/                      # Axios 封装与任务接口
        ├── components/TaskBoard.vue  # 任务管理主界面
        ├── styles/main.css
        └── types/task.ts
```

## 环境要求

- JDK 21+（项目以 Java 21 为编译目标）
- Maven 3.9+
- Node.js 20.19+ 或 22.12+
- npm 10+

## 启动项目

### 1. 启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

验证接口：

```bash
curl http://localhost:8080/api/tasks
```

### 2. 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

打开 http://localhost:5173 。Vite 已配置 `/api` 代理到 `http://localhost:8080`，无需额外配置环境变量。

## REST API

基础路径：`/api/tasks`

| 方法 | 路径 | 说明 | 成功状态码 |
| --- | --- | --- | --- |
| GET | `/api/tasks` | 获取任务列表 | 200 |
| GET | `/api/tasks/{id}` | 获取单个任务 | 200 |
| POST | `/api/tasks` | 创建任务 | 201 |
| PUT | `/api/tasks/{id}` | 更新任务 | 200 |
| DELETE | `/api/tasks/{id}` | 删除任务 | 204 |

请求体示例：

```json
{
  "title": "完成首页开发",
  "description": "实现统计卡片与任务列表",
  "status": "IN_PROGRESS"
}
```

`status` 可选值：`TODO`、`IN_PROGRESS`、`DONE`。标题必填，最长 100 字符；描述最长 500 字符。

错误响应统一为：

```json
{
  "timestamp": "2026-11-09T04:15:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "标题不能为空",
  "path": "/api/tasks"
}
```

## 构建与测试

```bash
# 后端：运行集成测试并打包
cd backend
mvn test
mvn package

# 前端：类型检查 + 生产构建
cd frontend
npm run build
npm run preview
```

## 说明

- 后端使用 `ConcurrentHashMap` 在内存中保存数据，重启后数据会重置为 3 条示例任务。
- 生产部署时可将 `frontend/dist` 交给 Nginx 等静态服务器托管，并将 `/api` 反向代理到后端。
