USE `huixin_blog`;

-- 1. 注入超级管理员账号 (用户名: guanliyuan, 密码: abc123456)
-- 你原 user 表没有 role，而是用 role_type。我们约定: 0-普通用户, 1-博主, 2-超级管理员
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `avatar_url`, `role_type`, `status`, `create_time`, `update_time`)
VALUES
('guanliyuan', '$2a$10$EixZaYVK1fsrs1ZhuONOndZc19OPO.B1fH.hJ1hE2C0P.vJ.vJ.vJ', '超级管理员', 'admin@huixin.blog', 'https://api.dicebear.com/7.x/adventurer/svg?seed=Admin&backgroundColor=ffdfbf', 2, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `role_type` = 2, `nickname` = '超级管理员';

