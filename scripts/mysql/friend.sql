'CREATE TABLE `friend` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_uid` bigint DEFAULT NULL,
  `to_uid` bigint DEFAULT NULL,
  `hidden` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueFriend` (`from_uid`,`to_uid`),
  KEY `FKkbtnpta8qnw2lp9lw2g9a6wm6` (`to_uid`),
  CONSTRAINT `FK7nanmg5aamo1tdopv3csxp4fv` FOREIGN KEY (`from_uid`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKkbtnpta8qnw2lp9lw2g9a6wm6` FOREIGN KEY (`to_uid`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'