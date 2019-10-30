CREATE TABLE `t_order` (
  `id` bigint(20) unsigned not null AUTO_INCREMENT comment '自增主键',
  `name` varchar(32),
  `creator` varchar(24),
  `price` varchar(64),
  `create_time` datetime,
  `status` tinyint(1),
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '订单表';