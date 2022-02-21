'CREATE TABLE `device` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token` text NOT NULL,
  `ip` varchar(15) NOT NULL,
  `agent` text NOT NULL,
  `type` smallint NOT NULL,
  `loggedin_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_device` (`user_id`,`type`),
  CONSTRAINT `device_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'