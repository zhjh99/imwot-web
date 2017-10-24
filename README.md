# imwot-web
imwot-web

一、简介


二、开发环境

2.1、Eclipse Java EE IDE for Web Developers.
Version: Kepler Service Release 2
Build id: 20140224-0627

2.2、jdk1.7.0_79

2.3、apache-tomcat-7.0.78

2.4、mysql5.6.26

三、开发准备:

3.1、下载源码到本地

3.2、生成 Eclipse 项目文件,并修改编码为utf-8
mvn eclipse:eclipse

3.3、导入到项目

3.4、tomcat服务器

3.5、添加tomcat服务器
右键->属性->java build path->libraries->add library...->选中server runtime->next->Apache tomcat v7.0

3.6、设置成web项目
右键->属性->Project Facets->在右侧选择“Dynamic Web Module” 3.0和Java 1.7

3.7、添加maven jar包
右键->属性->Deployment Assembly->add->Java Build Path Entries->全选

3.8、建立基础数据库表
导入项目WebContent下的imwot20161001-table.sql
