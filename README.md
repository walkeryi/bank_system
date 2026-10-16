# 银行网点管理系统（作业02：实体层 / 持久层 / 视图层设计与编码）

技术栈（严格按作业要求）：

- 后端：**Spring Boot 2.7 + MyBatis-Plus 3.5（MyBatis）**，JDK 1.8，Maven，内嵌 Tomcat
- 前端：**Thymeleaf 模板 + Vue 3 + Bootstrap 5 + axios**（左右布局，左侧为按角色动态加载的菜单）
- 数据库：MySQL 8（库名 `bank_account_trade`，排序 utf8mb4）
- 开发环境：IntelliJ IDEA（Maven Project）

## 一、运行步骤

1. 创建数据库并导入数据：
   ```bash
   mysql -uroot -p < sql/bank.sql
   ```
   脚本在作业01 的 14 张表基础上，新增了 `menu`、`role_menu` 两张菜单授权表，并写入 5 类角色的菜单授权。
2. 按需修改 `src/main/resources/application.yml` 中的数据库账号密码（默认 root/123456）。
3. IDEA 中打开本 Maven 工程，运行 `com.bank.BankApplication`；或命令行：
   ```bash
   mvn spring-boot:run
   ```
4. 浏览器访问 <http://localhost:8080/login>。

演示账号（口令统一为 `123456`）：

| 账号 | 姓名 | 角色 |
|---|---|---|
| T2026001 | 张三 | 柜员 |
| C2026001 | 李四 | 信贷员 |
| M2026001 | 王五 | 分行经理 |
| X2026001 | 赵六 | 风控/审计员 |
| S2026001 | 陈七 | 客户 |
| admin | 系统管理员 | 分行经理（演示全菜单） |

## 二、按角色动态菜单（视图层核心要求）

登录成功后，`AuthController` 调用
`menu → role_menu → user_role → user` 关联查询当前用户拥有的菜单（见
`MenuDao.xml#selectMenusByUserId`），写入 Session；`templates/fragments.html`
中的左侧栏用 Thymeleaf `th:each="m : ${session.menuList}"` 渲染到页面左侧，
右侧为内容区（左右布局）。不同角色登录看到的菜单不同，菜单完全由数据库配置，无需改代码。

## 三、分层结构（MVC）

- 实体层 `com.bank.entity`：与 16 张表一一对应的实体类（MyBatis-Plus 注解）。
- 持久层 `com.bank.dao` + `resources/mapper/*.xml`：
  - `common.BaseDao<T>` 继承 MyBatis-Plus `BaseMapper<T>`，单表 CRUD 零 SQL；
  - 各 `XxxDao` 继承 `BaseDao`，关联查询写在同名 Mapper XML（即“接口 + 映射文件”）。
- 业务层 `com.bank.service` / `service.impl`：`XxxService` 接口 + `XxxServiceImpl`
  实现类（MyBatis-Plus `IService/ServiceImpl`），核心业务含事务与审计留痕。
- 控制层 `com.bank.controller`：
  - `PageController` 返回 Thymeleaf 页面；`AuthController` 负责登录与菜单；
  - 各 `XxxController` 为 REST 接口（`/api/**`，统一 `Result` 返回）。
- 视图层 `resources/templates`：登录页、首页（左右布局）、改密页，以及账户、交易、
  大额取款、贷款、还款、产品、订单、审批、审计、状态变更、通知、用户、角色的列表/录入页。

## 四、已实现的关键业务

开户、账户冻结/解冻/销户（留痕）、存款/取款/转账（余额校验、双边记账、事务）、
大额取款申请—经理授权—扣款、贷款申请—初审—终审—放款、金融产品下单扣款与赎回、
审计日志自动记录、登录拦截与按角色菜单。
