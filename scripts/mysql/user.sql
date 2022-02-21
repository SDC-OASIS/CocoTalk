'CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `cid` varchar(20) NOT NULL,
  `email` varchar(125) DEFAULT NULL,
  `loggedin_at` datetime(6) DEFAULT NULL,
  `username` varchar(20) DEFAULT NULL,
  `nickname` varchar(20) NOT NULL,
  `password` varchar(64) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `profile` varchar(255) DEFAULT NULL,
  `status` smallint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_589idila9li6a4arw1t8ht1gx` (`phone`) /*!80000 INVISIBLE */,
  UNIQUE KEY `UK_cid` (`cid`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'