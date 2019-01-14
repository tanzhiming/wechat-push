DROP TABLE IF EXISTS tb_wx_user;
CREATE TABLE `tb_wx_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sex` int(11) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `headimgurl` varchar(255) DEFAULT NULL,
  `subscribe` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS tb_device;
CREATE TABLE `tb_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `puid` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` varchar(20) NOT NULL,
  `index` int(11) DEFAULT NULL,
  `usable` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_task;
CREATE TABLE `tb_task` (
  `f_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_name` varchar(60) DEFAULT NULL,
  `f_type` int(11) DEFAULT NULL,
  `f_cron_express` varchar(60) DEFAULT NULL,
  `f_detail` varchar(500) DEFAULT NULL,
  `f_job_name` varchar(255) DEFAULT NULL,
  `f_job_group` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`f_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tb_task_log` (
  `f_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_task_name` varchar(60) DEFAULT NULL,
  `f_task_type` int(11) DEFAULT NULL,
  `f_log` text,
  `f_create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`f_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;