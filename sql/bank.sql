DROP DATABASE IF EXISTS bank_account_trade;
CREATE DATABASE bank_account_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bank_account_trade;

CREATE TABLE `role` (
                        role_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
                        role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
                        role_desc VARCHAR(255) COMMENT '角色描述',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='角色表';

CREATE TABLE `user` (
                        user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一ID',
                        user_no VARCHAR(32) NOT NULL UNIQUE COMMENT '工号/客户号',
                        user_name VARCHAR(50) NOT NULL COMMENT '姓名',
                        id_card VARCHAR(18) UNIQUE COMMENT '身份证号',
                        phone VARCHAR(20) COMMENT '手机号',
                        password_hash CHAR(32) NOT NULL COMMENT '口令MD5摘要',
                        status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='用户表';

CREATE TABLE user_role (
                           ur_id INT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT NOT NULL,
                           role_id INT NOT NULL,
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE KEY uk_user_role (user_id, role_id),
                           FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                           FOREIGN KEY (role_id) REFERENCES `role`(role_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='用户角色表';

CREATE TABLE account (
                         account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         account_no VARCHAR(32) NOT NULL UNIQUE COMMENT '账号',
                         user_id BIGINT NOT NULL COMMENT '归属客户',
                         balance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
                         account_status TINYINT NOT NULL COMMENT '0正常 1冻结 2销户',
                         open_teller_id BIGINT COMMENT '开户柜员',
                         open_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                         close_time DATETIME NULL,
                         remark VARCHAR(255),
                         update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='账户表';

CREATE TABLE trade_record (
                              trade_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              trade_no VARCHAR(64) NOT NULL UNIQUE COMMENT '流水号',
                              account_id BIGINT NOT NULL,
                              trade_type TINYINT NOT NULL COMMENT '1存款 2取款 3转出 4转入',
                              trade_amount DECIMAL(18,2) NOT NULL,
                              trade_status TINYINT NOT NULL COMMENT '0待处理 1成功 2失败 3已冲正',
                              counter_user_id BIGINT,
                              target_account_id BIGINT NULL,
                              trade_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              remark VARCHAR(255),
                              FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE ON UPDATE CASCADE,
                              FOREIGN KEY (target_account_id) REFERENCES account(account_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='交易流水表';

CREATE TABLE large_withdraw_apply (
                                      apply_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      apply_no VARCHAR(64) NOT NULL UNIQUE COMMENT '申请单号',
                                      account_id BIGINT NOT NULL,
                                      withdraw_amount DECIMAL(18,2) NOT NULL,
                                      apply_teller_id BIGINT NOT NULL,
                                      apply_status TINYINT NOT NULL COMMENT '0待授权 1已授权 2已拒绝 3已撤销',
                                      approve_manager_id BIGINT NULL,
                                      approve_time DATETIME NULL,
                                      approve_comment VARCHAR(255) NULL,
                                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='大额取款申请表';

CREATE TABLE approve_record (
                                approve_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                business_type VARCHAR(32) NOT NULL COMMENT '大额取款/贷款申请/大额销售',
                                business_id BIGINT NOT NULL,
                                apply_user_id BIGINT NOT NULL,
                                approve_user_id BIGINT NULL,
                                approve_status TINYINT NOT NULL COMMENT '0待审批 1通过 2驳回 3归档',
                                approve_opinion VARCHAR(500),
                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                approve_time DATETIME NULL
) COMMENT='审批记录表';

CREATE TABLE audit_log (
                           log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           operator_user_id BIGINT NOT NULL,
                           operate_module VARCHAR(50) NOT NULL,
                           operate_type VARCHAR(50) NOT NULL,
                           business_key VARCHAR(128),
                           operate_content TEXT,
                           operate_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='审计日志表';

CREATE TABLE notice (
                        notice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        receive_user_id BIGINT NULL COMMENT 'NULL=全体',
                        notice_title VARCHAR(200) NOT NULL,
                        notice_content TEXT,
                        is_read TINYINT DEFAULT 0 COMMENT '0未读 1已读',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='通知公告表';

CREATE TABLE account_status_change (
                                       change_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       account_id BIGINT NOT NULL,
                                       before_status TINYINT NOT NULL,
                                       after_status TINYINT NOT NULL,
                                       operate_user_id BIGINT NOT NULL,
                                       change_reason VARCHAR(255) NOT NULL,
                                       change_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='账户状态变更表';

CREATE TABLE loan_apply (
                            loan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            apply_no VARCHAR(64) NOT NULL UNIQUE,
                            user_id BIGINT NOT NULL,
                            loan_amount DECIMAL(18,2) NOT NULL,
                            loan_term INT NOT NULL COMMENT '期限(月)',
                            rate DECIMAL(6,4) NOT NULL COMMENT '年利率',
                            loan_purpose VARCHAR(255),
                            apply_status TINYINT NOT NULL COMMENT '0待初审 1待终审 2通过 3拒绝 4已放款 5已结清',
                            credit_user_id BIGINT NULL,
                            manager_user_id BIGINT NULL,
                            approve_time DATETIME NULL,
                            loan_start_time DATETIME NULL,
                            loan_end_time DATETIME NULL,
                            remark VARCHAR(255),
                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                            FOREIGN KEY (credit_user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                            FOREIGN KEY (manager_user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='贷款申请表';

CREATE TABLE repay_record (
                              repay_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              loan_id BIGINT NOT NULL,
                              repay_no VARCHAR(64) NOT NULL UNIQUE,
                              repay_amount DECIMAL(18,2) NOT NULL,
                              principal_amount DECIMAL(18,2) NOT NULL,
                              interest_amount DECIMAL(18,2) NOT NULL,
                              repay_status TINYINT NOT NULL COMMENT '0待扣款 1成功 2失败',
                              repay_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              remark VARCHAR(255),
                              FOREIGN KEY (loan_id) REFERENCES loan_apply(loan_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='还款记录表';

CREATE TABLE financial_product (
                                   product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   product_code VARCHAR(32) NOT NULL UNIQUE,
                                   product_name VARCHAR(100) NOT NULL,
                                   product_type TINYINT NOT NULL COMMENT '1理财 2基金 3保险',
                                   risk_level TINYINT NOT NULL COMMENT '1低~5高',
                                   min_invest DECIMAL(18,2) NOT NULL COMMENT '起投金额',
                                   expected_rate DECIMAL(6,4),
                                   product_term INT COMMENT '期限(天)',
                                   status TINYINT NOT NULL COMMENT '0下架 1在售',
                                   remark VARCHAR(255),
                                   create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='金融产品表';

CREATE TABLE product_order (
                               order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               order_no VARCHAR(64) NOT NULL UNIQUE,
                               user_id BIGINT NOT NULL,
                               product_id BIGINT NOT NULL,
                               account_id BIGINT NOT NULL,
                               buy_amount DECIMAL(18,2) NOT NULL,
                               order_status TINYINT NOT NULL COMMENT '0待扣款 1持仓中 2已赎回 3失败',
                               sell_user_id BIGINT NULL,
                               order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                               redeem_time DATETIME NULL,
                               remark VARCHAR(255),
                               FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                               FOREIGN KEY (product_id) REFERENCES financial_product(product_id) ON DELETE CASCADE ON UPDATE CASCADE,
                               FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE ON UPDATE CASCADE,
                               FOREIGN KEY (sell_user_id) REFERENCES `user`(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='销售订单表';

-- 菜单表：菜单项及链接，登录后按角色查询显示在页面左侧
CREATE TABLE menu (
                      menu_id INT PRIMARY KEY AUTO_INCREMENT,
                      menu_name VARCHAR(50) NOT NULL,
                      menu_url VARCHAR(200),
                      parent_id INT DEFAULT 0,
                      icon VARCHAR(50),
                      sort_no INT DEFAULT 0
) COMMENT='菜单表';

CREATE TABLE role_menu (
                           role_id INT NOT NULL,
                           menu_id INT NOT NULL,
                           PRIMARY KEY (role_id, menu_id),
                           FOREIGN KEY (role_id) REFERENCES `role`(role_id) ON DELETE CASCADE ON UPDATE CASCADE,
                           FOREIGN KEY (menu_id) REFERENCES menu(menu_id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='角色菜单表';

-- ============================ 基础数据 ============================
INSERT INTO `role`(role_name,role_desc) VALUES
                                            ('柜员','柜面交易：开户、存取款、转账、发起大额取款申请'),
                                            ('信贷员','信贷销售：贷款录入初审、产品推荐、查询客户持仓'),
                                            ('分行经理','授权审批：大额取款授权、贷款终审、大额订单审批'),
                                            ('风控/审计员','风控审计：交易监控、合规抽查、审计报表'),
                                            ('客户','个人用户，办理账户与交易业务');

INSERT INTO `user`(user_no,user_name,id_card,phone,password_hash,status) VALUES
                                                                             ('T2026001','张三','440106199001011234','13800138001','e10adc3949ba59abbe56e057f20f883e',1),
                                                                             ('C2026001','李四','440106199202022345','13800138002','e10adc3949ba59abbe56e057f20f883e',1),
                                                                             ('M2026001','王五','440106198803033456','13800138003','e10adc3949ba59abbe56e057f20f883e',1),
                                                                             ('X2026001','赵六','440106198504044567','13800138004','e10adc3949ba59abbe56e057f20f883e',1),
                                                                             ('S2026001','陈七','440106199505055678','13800138005','e10adc3949ba59abbe56e057f20f883e',1),
                                                                             ('admin','系统管理员','440106199001019999','13800138000','e10adc3949ba59abbe56e057f20f883e',1);

INSERT INTO user_role(user_id,role_id) VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,3);

INSERT INTO account(account_no,user_id,balance,account_status,open_teller_id,remark) VALUES
                                                                                         ('6222021202012345001',5,10000.00,0,1,'客户陈七活期账户'),
                                                                                         ('6222021202012345002',5,25000.00,0,1,'客户陈七二类账户'),
                                                                                         ('6222021202012345003',2,80000.00,0,1,'客户李四储蓄账户'),
                                                                                         ('6222021202012345004',3,5000.00,1,1,'王五账户司法冻结'),
                                                                                         ('6222021202012345005',4,0.00,2,1,'赵六已销户账户');

INSERT INTO trade_record(trade_no,account_id,trade_type,trade_amount,trade_status,counter_user_id,target_account_id,remark) VALUES
                                                                                                                                ('TR202609120001',1,1,5000.00,1,1,NULL,'柜台存款'),
                                                                                                                                ('TR202609120002',1,2,2000.00,1,1,NULL,'柜台取款'),
                                                                                                                                ('TR202609120003',1,3,3000.00,1,1,3,'转账转出给李四'),
                                                                                                                                ('TR202609120004',3,4,3000.00,1,1,1,'转账转入来自陈七'),
                                                                                                                                ('TR202609120005',2,2,10000.00,2,1,NULL,'取款失败余额不足');

INSERT INTO large_withdraw_apply(apply_no,account_id,withdraw_amount,apply_teller_id,apply_status,approve_manager_id,approve_comment) VALUES
                                                                                                                                          ('LWA20260912001',1,80000.00,1,0,NULL,NULL),
                                                                                                                                          ('LWA20260912002',1,100000.00,1,1,3,'同意大额取款'),
                                                                                                                                          ('LWA20260912003',3,150000.00,1,2,3,'资金用途不清晰驳回'),
                                                                                                                                          ('LWA20260912004',2,60000.00,1,3,NULL,'客户主动撤销申请'),
                                                                                                                                          ('LWA20260912005',1,95000.00,1,0,NULL,NULL);

INSERT INTO approve_record(business_type,business_id,apply_user_id,approve_user_id,approve_status,approve_opinion) VALUES
                                                                                                                       ('大额取款',2,1,3,1,'同意大额取款'),
                                                                                                                       ('大额取款',3,1,3,2,'材料不全驳回'),
                                                                                                                       ('贷款申请',1,2,3,1,'贷款终审通过'),
                                                                                                                       ('大额销售',1,2,3,2,'风险过高驳回'),
                                                                                                                       ('大额取款',1,1,NULL,0,NULL);

INSERT INTO audit_log(operator_user_id,operate_module,operate_type,business_key,operate_content) VALUES
                                                                                                     (1,'柜面交易','开户','6222021202012345001','为客户陈七开立活期账户'),
                                                                                                     (1,'柜面交易','交易冲正','TR202609120005','冲正失败取款流水'),
                                                                                                     (3,'授权审批','大额取款授权','LWA20260912002','审批通过8万取款申请'),
                                                                                                     (4,'风控审计','账户冻结','6222021202012345004','冻结王五账户'),
                                                                                                     (2,'信贷','贷款录入','LA20260912001','录入客户李四贷款申请');

INSERT INTO account_status_change(account_id,before_status,after_status,operate_user_id,change_reason) VALUES
                                                                                                           (4,0,1,4,'司法冻结'),(5,0,2,1,'客户主动销户'),(1,0,0,1,'无变更测试记录'),
                                                                                                           (3,0,0,1,'正常账户，无状态变更'),(2,0,1,4,'临时风控冻结');

INSERT INTO loan_apply(apply_no,user_id,loan_amount,loan_term,rate,loan_purpose,apply_status,credit_user_id,manager_user_id) VALUES
                                                                                                                                 ('LA20260912001',2,200000.00,24,0.0420,'装修',2,2,3),
                                                                                                                                 ('LA20260912002',5,100000.00,12,0.0380,'购车',0,2,NULL),
                                                                                                                                 ('LA20260912003',2,500000.00,36,0.0450,'经营周转',3,2,3),
                                                                                                                                 ('LA20260912004',5,300000.00,60,0.0400,'创业',4,2,3),
                                                                                                                                 ('LA20260912005',5,150000.00,24,0.0360,'医疗',1,2,NULL);

INSERT INTO repay_record(loan_id,repay_no,repay_amount,principal_amount,interest_amount,repay_status,remark) VALUES
                                                                                                                 (1,'RP20260912001',9200.00,8000.00,1200.00,1,'第一期还款成功'),
                                                                                                                 (4,'RP20260912002',5500.00,5000.00,500.00,1,'放款后首期扣款'),
                                                                                                                 (1,'RP20260912003',9200.00,8000.00,1200.00,0,'待扣款'),
                                                                                                                 (1,'RP20260912004',9200.00,8000.00,1200.00,2,'余额不足扣款失败'),
                                                                                                                 (4,'RP20260912005',5500.00,5000.00,500.00,1,'第二期还款成功');

INSERT INTO financial_product(product_code,product_name,product_type,risk_level,min_invest,expected_rate,product_term,status,remark) VALUES
                                                                                                                                         ('FP001','稳健理财A',1,2,10000.00,0.0320,90,1,'低波动理财'),
                                                                                                                                         ('FP002','指数基金A',2,4,1000.00,0.0650,365,1,'股票指数基金'),
                                                                                                                                         ('FP003','年金保险',3,1,50000.00,0.0280,1095,1,'长期年金险'),
                                                                                                                                         ('FP004','短期现金理财',1,1,1000.00,0.0250,30,0,'产品下架'),
                                                                                                                                         ('FP005','混合基金B',2,3,5000.00,0.0520,180,1,'股债混合基金');

INSERT INTO product_order(order_no,user_id,product_id,account_id,buy_amount,order_status,sell_user_id,remark) VALUES
                                                                                                                  ('PO20260912001',5,1,1,50000.00,1,2,'客户购买稳健理财A'),
                                                                                                                  ('PO20260912002',2,2,3,20000.00,1,2,'客户李四购买指数基金'),
                                                                                                                  ('PO20260912003',5,5,2,10000.00,2,2,'客户赎回混合基金'),
                                                                                                                  ('PO20260912004',5,3,1,100000.00,0,2,'待扣款年金保险'),
                                                                                                                  ('PO20260912005',2,4,3,5000.00,3,2,'订单失败余额不足');
INSERT INTO menu(menu_id,menu_name,menu_url,parent_id,icon,sort_no) VALUES
                                                                        (1,'系统首页','/success',0,'icon-home',1),
                                                                        (2,'账户管理','/account/list',0,'icon-account',2),
                                                                        (3,'客户开户','/account/toAdd',0,'icon-add',3),
                                                                        (4,'柜面交易','/trade/toAdd',0,'icon-trade',4),
                                                                        (5,'交易流水','/trade/list',0,'icon-list',5),
                                                                        (6,'大额取款业务','/withdraw/list',0,'icon-withdraw',6),
                                                                        (7,'发起大额取款','/withdraw/toAdd',0,'icon-add',7),
                                                                        (8,'贷款业务','/loan/list',0,'icon-loan',8),
                                                                        (9,'贷款申请','/loan/toAdd',0,'icon-add',9),
                                                                        (10,'还款记录','/repay/list',0,'icon-repay',10),
                                                                        (11,'审批记录','/approve/list',0,'icon-approve',11),
                                                                        (12,'审计日志','/audit/list',0,'icon-audit',12),
                                                                        (13,'账户状态变更','/status/list',0,'icon-status',13),
                                                                        (14,'通知公告','/notice/list',0,'icon-notice',14),
                                                                        (15,'用户管理','/user/list',0,'icon-user',15),
                                                                        (16,'角色管理','/role/list',0,'icon-role',16),
                                                                        (17,'修改密码','/user/toPassword',0,'icon-lock',17);
-- 柜员(1)
INSERT INTO role_menu(role_id,menu_id) VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,13),(1,14),(1,17);
-- 信贷员(2)
INSERT INTO role_menu(role_id,menu_id) VALUES (2,1),(2,8),(2,9),(2,10),(2,14),(2,17);
-- 分行经理(3)
INSERT INTO role_menu(role_id,menu_id) VALUES (3,1),(3,2),(3,5),(3,6),(3,8),(3,11),(3,13),(3,14),(3,15),(3,16),(3,17);
-- 风控/审计员(4)
INSERT INTO role_menu(role_id,menu_id) VALUES (4,1),(4,2),(4,5),(4,12),(4,13),(4,14),(4,17);
-- 客户(5)
INSERT INTO role_menu(role_id,menu_id) VALUES (5,1),(5,2),(5,5),(5,7),(5,9),(5,10),(5,14),(5,17);