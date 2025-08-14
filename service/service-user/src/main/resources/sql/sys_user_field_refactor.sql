-- sys_user表字段重构脚本
-- 执行时间: 2025-08-13
-- 变更内容: 重命名企业微信字段，新增普通微信字段

-- 1. 重命名字段 (企业微信相关)
ALTER TABLE sys_user 
CHANGE COLUMN wechat_id wechatWork_id VARCHAR(255) COMMENT '企业微信用户ID';

ALTER TABLE sys_user 
CHANGE COLUMN wechat_nickname wechatWork_nickname VARCHAR(50) COMMENT '企业微信昵称';

-- 2. 新增字段 (普通微信相关)
ALTER TABLE sys_user 
ADD COLUMN wechat_id VARCHAR(255) COMMENT '普通微信用户ID' AFTER wechatWork_id;

ALTER TABLE sys_user 
ADD COLUMN wechat_nickname VARCHAR(50) COMMENT '普通微信昵称' AFTER wechat_id;

-- 3. 添加索引 (可选，根据查询需求)
-- ALTER TABLE sys_user ADD INDEX idx_wechat_id (wechat_id);
-- ALTER TABLE sys_user ADD INDEX idx_wechatWork_id (wechatWork_id);

-- 4. 验证变更结果
-- DESCRIBE sys_user;

-- 5. 回滚脚本 (如果需要)
/*
-- 回滚重命名字段
ALTER TABLE sys_user 
CHANGE COLUMN wechatWork_id wechat_id VARCHAR(255) COMMENT '微信用户ID';

ALTER TABLE sys_user 
CHANGE COLUMN wechatWork_nickname wechat_nickname VARCHAR(50) COMMENT '微信昵称';

-- 删除新增字段
ALTER TABLE sys_user DROP COLUMN wechat_id;
ALTER TABLE sys_user DROP COLUMN wechat_nickname;
*/
