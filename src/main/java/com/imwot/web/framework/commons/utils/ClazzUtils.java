/**
 [The "BSD license"]
 Copyright (c) 2013-2017 jinhong zhou (周金红)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imwot.web.framework.commons.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import com.imwot.web.framework.interfaces.AbstractController;

/**
 * 类工具
 * 
 * @author jinhong zhou
 */
public class ClazzUtils {
	public static void main(String[] str) {
		Class<?> clazz = AbstractController.class;
		ArrayList<Class<?>> list = getAllClassByInterface(clazz);

		for (Class<?> clazzs : list) {
			System.out.println(clazzs.getCanonicalName());
		}
	}

	public static ArrayList<Class<?>> getAllClassByInterface(Class<?> clazz) {
		ArrayList<Class<?>> list = new ArrayList<>();
		// 获取指定接口的实现类
		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			try {
				String packageName = "com";
				ArrayList<Class<?>> allClass = getAllClass(packageName);
				/**
				 * 循环判断路径下的所有类是否实现了指定的接口 并且排除接口类自己
				 */
				for (Class<?> clazzs : allClass) {
					/**
					 * 判断是不是同一个接口 isAssignableFrom该方法的解析，请参考博客：
					 * http://blog.csdn.net/u010156024/article/details/44875195
					 */
					if (clazz.isAssignableFrom(clazzs)) {
						if (!clazz.equals(clazzs)) {// 自身并不加进去
							list.add(clazzs);
						} else {

						}
					}
				}
			} catch (Exception e) {
				System.out.println("出现异常");
			}
			// 如果不是接口，则获取它的所有子类
		} else {
			try {
				ArrayList<Class<?>> allClass = getAllClass(clazz.getPackage().getName());
				/**
				 * 循环判断路径下的所有类是否继承了指定类 并且排除父类自己
				 */
				for (int i = 0; i < allClass.size(); i++) {
					/**
					 * isAssignableFrom该方法的解析，请参考博客：
					 * http://blog.csdn.net/u010156024/article/details/44875195
					 */
					if (clazz.isAssignableFrom(allClass.get(i))) {
						if (!clazz.equals(allClass.get(i))) {// 自身并不加进去
							list.add(allClass.get(i));
						} else {

						}
					}
				}
			} catch (Exception e) {
				System.out.println("出现异常");
			}
		}
		return list;
	}

	/**
	 * 从一个指定路径下查找所有的类
	 * 
	 * @param name
	 */
	private static ArrayList<Class<?>> getAllClass(String packagename) {
		ArrayList<Class<?>> list = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packagename.replace('.', '/');
		try {
			ArrayList<File> fileList = new ArrayList<>();
			/**
			 * 这里面的路径使用的是相对路径 如果大家在测试的时候获取不到，请理清目前工程所在的路径 使用相对路径更加稳定！
			 * 另外，路径中切不可包含空格、特殊字符等！ 本人在测试过程中由于空格，吃了大亏！！！
			 */
			Enumeration<URL> enumeration = classLoader.getResources(path);
			// Enumeration<URL> enumeration = classLoader.getResources("../bin/"
			// + path);
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();
				fileList.add(new File(url.getFile()));
			}
			for (int i = 0; i < fileList.size(); i++) {
				list.addAll(findClass(fileList.get(i), packagename));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 如果file是文件夹，则递归调用findClass方法，或者文件夹下的类 如果file本身是类文件，则加入list中进行保存，并返回
	 * 
	 * @param file
	 * @param packagename
	 * @return
	 */
	private static ArrayList<Class<?>> findClass(File file, String packagename) {
		ArrayList<Class<?>> list = new ArrayList<>();
		if (!file.exists()) {
			return list;
		}
		File[] files = file.listFiles();
		for (File file2 : files) {
			if (file2.isDirectory()) {
				assert !file2.getName().contains(".");// 添加断言用于判断
				ArrayList<Class<?>> arrayList = findClass(file2, packagename + "." + file2.getName());
				list.addAll(arrayList);
			} else if (file2.getName().endsWith(".class")) {
				try {
					// 保存的类文件不需要后缀.class
					String names = packagename + '.' + file2.getName().substring(0, file2.getName().length() - 6);
					list.add(Class.forName(names));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}