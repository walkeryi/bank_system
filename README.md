# 银行网点管理系统 —— 使用与协作说明

Spring Boot 2.7 + MyBatis-Plus + Thymeleaf + Vue 3 + MySQL 8，内嵌 Tomcat，端口 8080。
本文档面向"拿到这份代码要把它跑起来、或者要继续改"的人。

---

## 零、先说两条分支，别搞混

| 分支 | 是什么 |
|---|---|
| `dev` | **本次协作的基线**，同学清理后的版本。要改代码、要提交，都在这里。 |
| `main` | 另一条独立线（带本地前端依赖副本、README、金融产品模块）。 |

两条线**互相独立、不合并**，但**共用同一个数据库名** `bank_account_trade`。

有一个坑必须知道：dev 的 `sql/bank.sql` **不建** `financial_product`、`product_order` 这两张表。
如果你用 dev 的 SQL 重建了库，又切到 `main` 那版代码跑，`/api/product/**` 会报 500。

---

## 一、第一次拿到代码（新电脑）

```bash
git clone -b dev https://github.com/walkeryi/bank_system.git
cd bank_system
```

仓库是公开的，**克隆不需要任何权限**；只有往上面推才需要（见第六节）。

等价写法：先 `git clone https://github.com/walkeryi/bank_system.git`，再 `git switch dev`
（默认落在 `main`，所以要切一下）。只想快点拿到源码可以用 `git clone --depth 1 -b dev ...`。
用网页 Download ZIP 也行，但那样以后没法 pull 更新。

## 二、环境三样，装完就能跑

**1）JDK 8 或更高**（编译目标是 Java 8，装 8/11/17/21 都能跑，推荐 17 或 21）。
命令行敲 `java -version` 能打印版本号就算成功。

**2）MySQL 5.7 或 8.0**（推荐 8.0），安装时设的 root 密码要记住。

**3）导入数据**：在项目根目录（能看到 `pom.xml`、`sql` 的那一层）执行

```bash
mysql -uroot -p < sql\bank.sql
```

回车后输入 root 密码，没有任何输出就是成功了。也可以用 Navicat / Workbench 把
`sql\bank.sql` 整个执行一遍，效果一样。脚本自己会建库建表、写入演示数据，不用手动建库。

> ⚠️ 脚本**第一行**是 `DROP DATABASE IF EXISTS bank_account_trade;`，
> 会先删掉同名数据库再重建。你机器上如果已经有这个库而且里面有数据，**先备份**。

## 三、数据库密码不是 123456 怎么办

程序里写的数据库账号是 `root` / `123456`。密码不一样**不用改代码**，启动时带参数：

```bash
java -jar target\bank-system-1.0.0.jar --spring.datasource.password=你的密码
```

如果连库名或端口也不一样，再加一个参数。下面这一串**必须用英文双引号整体包起来**，
因为里面有 `&`，在命令行里是"连接两条命令"的意思：

```bash
java -jar target\bank-system-1.0.0.jar --spring.datasource.password=你的密码 "--spring.datasource.url=jdbc:mysql://localhost:3306/你的库名?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
```

（直接改 `src/main/resources/application.yml` 对已经打好的 jar 没用，配置已经打进 jar 了，除非重新编译。）

## 四、启动

```bash
java -jar target\bank-system-1.0.0.jar
```

看到最后一行打印 `Started BankApplication in x.x seconds` 就是成功，浏览器打开
<http://localhost:8080/login>。命令行窗口不能关，关掉服务就停了；要停服务按 Ctrl+C。

- 端口被占（`Port 8080 was already in use`）：换个端口启动 `--server.port=9090`。
- 目录里**没有** `target\bank-system-1.0.0.jar` 时得自己编译一次（见第五节）。
- 用 IDEA：`File → Open` 选项目里的 `pom.xml`，等依赖加载完，运行 `com.bank.BankApplication`。
  IDEA 里如果报 **"不支持发行版本 5"**，把 Project SDK 和 language level 都设成 17 或 21。

## 五、没网怎么编译

```bash
mvn -DskipTests package
```

没网时用离线仓库（`.m2repo` 文件夹就是干这个的）：

```bash
mvn -DskipTests -Dmaven.repo.local=.m2repo package
```

注意 `.m2repo`（约 96 MB，依赖全在里面）**没有进 git**，新电脑要用 U 盘/网盘手工拷过去。
如果你只是运行别人打好的 jar，这个文件夹用不到。

## 六、以后更新代码

```bash
git pull --ff-only origin dev
```

`--ff-only` 的意思是只允许快进：远端有新提交就直接跟上；万一本地和远端都改过、没法快进，
它会停下来报错，而不是自己生成一个合并提交。

拉之前想先看会有什么变化（不改动任何文件）：

```bash
git fetch origin
git log --oneline HEAD..origin/dev
git diff --stat HEAD origin/dev
```

本地有未提交改动挡住拉取时（报 `Your local changes ... would be overwritten`）：

```bash
git stash push -- 那个文件
git pull --ff-only origin dev
git stash pop
```

IDEA 里对应的是 `Git → Pull`（或 Ctrl+T 的 Update Project），注意别拉错分支。

## 七、把改好的代码传上来

**1）第一次在这台机器上提交，先配好身份**（不配的话提交作者是空的或者不对）：

```bash
git config --global user.name "你的名字"
git config --global user.email "你的邮箱"
```

**2）提交前先 `git status` 看一眼，别用 `git add -A` 一把梭。**
`target/`、`.m2repo/`、`.vendor/`、`*.log` 已经被 `.gitignore` 挡住了，但
`实验报告/`、`_xxx.py` 这类临时文件**没有**被挡，一把梭会把它们一起提交进仓库。

```bash
git add 你改动的文件
git commit -m "改了什么"
git pull --rebase origin dev
git push origin dev
```

**3）权限**：往 `walkeryi/bank_system` 推送需要 Write 权限。
在 GitHub 仓库的 `Settings → Collaborators` 里加人（选 Write）；
如果对方不方便加人，就 fork 一份再提 Pull Request。

**4）推之前先 `git pull --rebase origin dev`**，能省掉大部分冲突。

## 八、演示账号（口令全部是 123456）

| 账号 | 角色 |
|---|---|
| `admin` | 系统管理员（全菜单，演示用） |
| `T2026001` | 柜员 |
| `C2026001` | 信贷员 |
| `M2026001` | 营业经理 |
| `X2026001` | 风控 / 审计员 |
| `S2026001` | 客户 |

左侧菜单是按角色从数据库加载的（`menu → role_menu → user_role → user`），
不同角色登录看到的菜单不一样，菜单配置在库里，不用改代码。

## 九、常见报错

| 现象 | 原因和处理 |
|---|---|
| `java 不是内部或外部命令` | Java 没装或没配 PATH，重装并勾选 Add to PATH。 |
| `Port 8080 was already in use` | 8080 被占用，加 `--server.port=9090` 换端口。 |
| `Communications link failure` | MySQL 服务没启动。`services.msc` 里找 MySQL 服务点启动。 |
| `Access denied for user 'root'@'localhost'` | 数据库密码不对，按第三节带参数启动。 |
| `Unknown database 'bank_account_trade'` | sql 没导入成功，回到第二节重做，注意命令行要在项目根目录。 |
| `Table 'bank_account_trade.xxx' doesn't exist` | sql 只导了一半，完整重新执行一次 `sql\bank.sql`。 |
| `mysql 不是内部或外部命令` | 命令行工具不在 PATH。用完整路径，例如 `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot -p < sql\bank.sql`，或者干脆用图形界面导入。 |
| IDEA 报"不支持发行版本 5" | Project SDK 和 language level 设成 17/21。 |
| 页面样式正常但没有数据 | 数据没导全，重新执行 `sql\bank.sql`。 |

## 十、已知问题（2026-10-17 实测）

冒烟测试结论：20 个页面、11 个列表接口、登录/登出/增删/分页/权限拦截都正常
（63 项断言通过 61 项，2 项失败是下面的第 2 条）。以下是已知缺陷，都还没修：

1. **接口层没有角色校验。** 只要登录成功，任何账号都能调用全部 `/api/**`：
   能读用户表（含身份证号）、审计日志、全部账户，也能写角色/公告。
   实测用 `C2026001`（信贷员）调 `POST /api/role/add` 能写库成功。
   左侧菜单按角色隐藏只是前端的门，接口的门没关。要真隔离得在拦截器里加角色判断。
2. **注册接口有两处空指针。** `/api/register` 传一个库里合法、但代码里那个 map 没有的角色码
   （例如 `ADMIN`），`roleMap.get()` 返回 null 触发 NPE；不传 `idCard` 时
   `idCard.length()` 触发 NPE。两者都返回 `系统异常：...`，**并把 Java 异常原文回显给浏览器**。
   正常走页面表单碰不到（有必填和下拉框）。
3. **五个路由没有对应模板**，手输 URL 会返回 500 白页：
   `/role/add`、`/product/list`、`/product/toAdd`、`/order/list`、`/order/toAdd`。
   侧边栏和首页已经没有这些链接，只有手输才会触发。
4. **前端 js/css 从 `cdn.jsdelivr.net` 加载**（Bootstrap、Vue、axios），本地 `static/` 下只有
   `app.css`、`common.js`、`dict.js`。**断网时页面没样式、Vue 不加载、表格是空的。**
   要离线演示得把这三个库下载到本地并改 `templates/fragments.html` 的引用。
5. 上面第零节说的 `financial_product` / `product_order` 两张表的问题。

## 十一、目录结构

```
src\                  源代码（Java + Thymeleaf 页面 + 静态样式脚本）
sql\bank.sql          建库建表 + 演示数据，必须先导入（会先删同名库！）
pom.xml               Maven 配置，用 IDEA 打开它即可导入工程
target\               编译产物，bank-system-1.0.0.jar 就是要运行的程序
.m2repo\              离线 Maven 仓库，没网时编译用（没进 git，要手工拷）
app.log               程序运行日志，出问题看这里
```
