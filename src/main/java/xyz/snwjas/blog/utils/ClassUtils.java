package xyz.snwjas.blog.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class ClassUtils {

	/**
	 * 根据一个接口返回该接口的所有类,
	 * 注意实现类需要位于该接口的包下
	 *
	 * @param clazz 接口
	 * @return List<Class < ?>> 实现接口的所有类
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> getAllClassByInterface(Class<T> clazz) {
		ArrayList<Class<T>> returnClassList = new ArrayList<>();
		// 判断是不是接口,不是接口不作处理
		if (clazz.isInterface()) {
			// 获得当前包名
			String packageName = clazz.getPackage().getName();
			try {
				// 获得当前包以及子包下的所有类
				List<Class<?>> allClass = getClasses(packageName);
				// 判断是否是一个接口
				for (Class<?> aClass : allClass) {
					if (clazz.isAssignableFrom(aClass) && !clazz.equals(aClass)) {
						returnClassList.add((Class<T>) aClass);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return returnClassList;
	}

	/**
	 * 根据一个注解返回该接口的所有类,
	 * 注意实现类需要位于该注解的包下
	 *
	 * @param clazz 注解
	 * @return List<Class < ?>> 实现接口的所有类
	 */
	public static List<Class<?>> getAllClassByAnnotation(Class<? extends Annotation> clazz) {
		List<Class<?>> returnClassList = new ArrayList<>();
		// 判断是不是注解
		if (clazz.isAnnotation()) {
			//获得当前包名
			String pkgName = clazz.getPackage().getName();
			try {
				// 获得当前包以及子包下的所有类
				List<Class<?>> allClass = getClasses(pkgName);
				for (Class<?> aClass : allClass) {
					if (aClass.isAnnotationPresent(clazz)) {
						returnClassList.add(aClass);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return returnClassList;
	}

	/**
	 * 根据包名获得该包以及子包下的所有类不查找jar包中的
	 *
	 * @param pkgName 包名
	 * @return List<Class>    包下所有类
	 */
	private static List<Class<?>> getClasses(String pkgName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = pkgName.replace(".", "/");
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String newPath = resource.getFile().replace("%20", " ");
			dirs.add(new File(newPath));
		}
		ArrayList<Class<?>> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClass(directory, pkgName));
		}
		return classes;
	}

	private static List<Class<?>> findClass(File dir, String pkgName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if (Objects.isNull(dir) || !dir.isDirectory()) {
			return classes;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClass(file, pkgName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				String className = pkgName + "." + file.getName().substring(0, file.getName().length() - 6);
				classes.add(Class.forName(className));
			}
		}
		return classes;
	}

}
