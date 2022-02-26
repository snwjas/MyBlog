package xyz.snwjas.blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * File Utils
 *
 * @author Myles Yang
 */
@Slf4j
public class FileUtils {

	/**
	 * 根据文件名获取文件名称与后缀
	 *
	 * @param fileName
	 * @return [name, ext]，后缀含"."
	 */
	public static String[] getFileNameAndExt(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return new String[]{"", ""};
		}
		int len = fileName.length();
		int idx = fileName.lastIndexOf('.');
		String name = fileName.substring(0, idx >= 0 ? idx : len);
		String ext = idx >= 0 && idx < len - 1 ? fileName.substring(idx, len) : "";
		return new String[]{name, ext};
	}

	/**
	 * 文件名后添加字符串，如 word.txt 添加‘-java’, 效果 word-java.txt
	 */
	public static String fileNameAppend(String fileName, String append) {
		StringBuilder newFileName = new StringBuilder(fileName);

		// 是否有后缀
		int i = org.apache.commons.lang3.StringUtils.lastIndexOf(fileName, '.');
		if (i >= 0) {
			newFileName.insert(i, append);
		} else {
			newFileName.append(append);
		}

		return newFileName.toString();
	}

	/**
	 * @param filePath 文件全路径
	 * @return {-1: "文件不存在或是是文件夹", 0: "删除失败", 1: "删除成功"}
	 */
	public static int deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			return -1;
		}
		boolean deleted = file.delete();
		return deleted ? 1 : 0;
	}

	/**
	 * 获取图片像素
	 *
	 * @param imageFile 图片文件
	 * @return 图片像素 {width, height}
	 */
	public static int[] getImagePixel(File imageFile) {
		try {
			BufferedImage bi = ImageIO.read(imageFile);
			int width = bi.getWidth();
			int height = bi.getHeight();
			return new int[]{width, height};
		} catch (IOException e) {
			log.error("获取图片像素失败", e.getCause());
		}
		return new int[]{0, 0};
	}

	/**
	 * 获取SpringWebMVC文件资源映射路径
	 *
	 * @param path 映射目录
	 */
	public static String getFileResLoc(String path) {
		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException("参数不能为空");
		}
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		return "file:" + path;
	}
}
