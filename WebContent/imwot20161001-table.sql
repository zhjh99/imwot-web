/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.6.26-log : Database - imwot
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`imwot` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `imwot`;

/*Table structure for table `authorization` */

DROP TABLE IF EXISTS `authorization`;

CREATE TABLE `authorization` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `NAME` varchar(20) NOT NULL COMMENT '名称',
  `TITLE` varchar(50) NOT NULL COMMENT '标题',
  `PATH` varchar(50) DEFAULT NULL COMMENT '路径',
  `SORTVALUE` int(11) DEFAULT '0' COMMENT '排序',
  `PID` int(11) DEFAULT NULL COMMENT '父ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;

/*Data for the table `authorization` */

insert  into `authorization`(`ID`,`NAME`,`TITLE`,`PATH`,`SORTVALUE`,`PID`) values (1,'app','app','',1,0),(2,'manager','manager','',2,0),(3,'userList','用户管理','/admin/user/userList',1,2),(4,'userAddPage','userAddPage','/admin/user/userAddPage',0,3),(5,'userDel','userDel','/admin/user/userDel',0,3),(6,'test','test','/admin/user/test',0,3),(7,'userListList','userListList','/admin/user/getList',0,3),(8,'userUpdate','userUpdate','/admin/user/userUpdate',0,3),(9,'userDel','userDel','/admin/user/userDel',0,3),(10,'users','users','/admin/user/users',0,3),(11,'userUpdatePage','userUpdatePage','/admin/user/userUpdatePage',0,3),(17,'authorizationListPag','权限管理','/admin/authorization/listPage',2,2),(18,'authorizationListPag','authorizationListPag','/admin/authorization/getList',0,17),(19,'authorizationAddPage','authorizationAddPage','/admin/authorization/addPage',0,17),(20,'authorizationUpdate','authorizationUpdate','/admin/authorization/update',0,17),(21,'authorizationDelete','authorizationDelete','/admin/authorization/delete',0,17),(22,'authorizationNavigat','authorizationNavigat','/admin/authorization/navigation',0,17),(23,'roleListPage','角色管理','/admin/role/listPage',3,2),(24,'roleList','roleList','/admin/role/getList',0,23),(25,'roleAddPage','roleAddPage','/admin/role/addPage',0,23),(26,'roleUpdate','roleUpdate','/admin/role/update',0,23),(27,'roleDelete','roleDelete','/admin/role/delete',0,23),(28,'roleAuthManager','roleAuthManager','/admin/role/authManager',0,23),(29,'roleUsers','roleUsers','/admin/role/users',0,23),(30,'roleAuthorize','roleAuthorize','/admin/role/authorize',0,23),(31,'roleManager','roleManager','/admin/role/userManager',0,23),(32,'roleAuth','roleAuth','/admin/role/auth',0,23),(33,'roleAuthData','roleAuthData','/admin/role/authData',0,23),(34,'authData','authData','/admin/authorization/authData',0,17),(35,'操作1','操作1','www.baidu.com',1,1),(36,'操作2','操作2','#',2,1),(37,'操作3','操作3','#',3,1);

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `NAME` varchar(20) NOT NULL COMMENT '名称',
  `PID` int(6) NOT NULL DEFAULT '0' COMMENT '父ID',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

/*Data for the table `role` */

insert  into `role`(`ID`,`NAME`,`PID`,`STATUS`,`CREATE_TIME`) values (1,'管理员',0,1,'2015-10-01 18:53:56'),(2,'员工组',0,1,'2015-10-01 18:53:56'),(3,'质检组',0,1,'2015-10-01 18:53:56'),(4,'sss1',0,1,'2015-10-01 18:53:56');

/*Table structure for table `role_authorization` */

DROP TABLE IF EXISTS `role_authorization`;

CREATE TABLE `role_authorization` (
  `ROLE_ID` int(11) NOT NULL COMMENT '角色ID',
  `AUTHORIZATION_ID` int(11) NOT NULL COMMENT '权限ID',
  KEY `FK_role_authorization` (`ROLE_ID`),
  KEY `FK_role_roles` (`AUTHORIZATION_ID`),
  CONSTRAINT `FK_role_authorization` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`),
  CONSTRAINT `FK_role_roles` FOREIGN KEY (`AUTHORIZATION_ID`) REFERENCES `authorization` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role_authorization` */

insert  into `role_authorization`(`ROLE_ID`,`AUTHORIZATION_ID`) values (1,33),(1,32),(1,31),(1,30),(1,29),(1,28),(1,27),(1,26),(1,25),(1,24),(1,23),(1,37),(1,36),(1,35),(1,34),(1,8),(1,9),(1,2),(1,4),(1,5),(1,6),(1,7),(1,10),(1,3),(1,22),(1,20),(1,21),(1,11),(1,18),(1,17),(1,19),(1,1);

/*Table structure for table `role_user` */

DROP TABLE IF EXISTS `role_user`;

CREATE TABLE `role_user` (
  `ROLE_ID` int(11) NOT NULL COMMENT '角色ID',
  `USER_ID` int(11) NOT NULL COMMENT '用户ID',
  KEY `FK_role_user` (`USER_ID`),
  KEY `FK_role_role` (`ROLE_ID`),
  CONSTRAINT `FK_role_role` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`),
  CONSTRAINT `FK_role_user` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role_user` */

insert  into `role_user`(`ROLE_ID`,`USER_ID`) values (1,1);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `USERNAME` varchar(50) DEFAULT NULL COMMENT '用户名',
  `PASSWORD` varchar(32) DEFAULT NULL COMMENT '密码',
  `NICKNAME` varchar(50) NOT NULL COMMENT '昵称',
  `EMAIL` varchar(50) DEFAULT NULL COMMENT '邮件',
  `CREATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `STATUS` int(11) DEFAULT '0' COMMENT '启用状态(0:停用;1:启用)',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`ID`,`USERNAME`,`PASSWORD`,`NICKNAME`,`EMAIL`,`CREATE_TIME`,`UPDATE_TIME`,`STATUS`) values (1,'admin','21232f297a57a5a743894a0e4a801fc3','admin','123@126.com','2015-10-01 18:53:56','2015-10-01 18:53:56',1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
