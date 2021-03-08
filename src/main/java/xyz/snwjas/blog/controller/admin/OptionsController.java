package xyz.snwjas.blog.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.snwjas.blog.config.properties.MyBlogProperties;
import xyz.snwjas.blog.service.OptionsService;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.URLUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Option Controller
 *
 * @author Myles Yang
 */
@Slf4j
@RestController("AdminOptionsController")
@RequestMapping("/api/admin/option")
@Api(value = "后台设置控制器", tags = {"后台设置选项接口"})
public class OptionsController {

	@Autowired
	private OptionsService optionsService;

	@Autowired
	private MyBlogProperties properties;

	@ApiOperation("获取所有的博客设置")
	@GetMapping("/list")
	public R listAll() {
		Map<String, Object> optionsMap = optionsService.listAll();
		return RUtils.success("所有的博客设置", optionsMap);
	}

	@ApiOperation("修改博客设置")
	@PostMapping("/update")
	public R update(@RequestBody Map<String, Object> optionMap) {
		// 修改成功的数量
		AtomicInteger count = new AtomicInteger();
		optionMap.forEach((key, value) -> {
			if (StringUtils.hasText(key) && Objects.nonNull(value)) {
				int i = optionsService.setAnyway(key, String.valueOf(value));
				if (i > 0) {
					count.getAndIncrement();
				}
				// 保存 favicon
				// if (MyBlogOptionEnum.FAVICON.key().equals(key)) {
				// 	saveFavicon(String.valueOf(value));
				// }
			}
		});

		if (count.get() > 0) {
			optionsService.resetOptionsCache();
			return RUtils.success("博客设置修改成功");
		}

		return RUtils.success("博客设置未作修改");
	}

	/**
	 * 保存 favicon 到文件中
	 */
	@Async("executor")
	public void saveFavicon(String url) {
		String savePath = properties.getFileSavePath() + File.separatorChar + "favicon.ico";

		try (BufferedInputStream bis = new BufferedInputStream(
				new URL(URLUtils.encodeAll(url)).openStream())) {
			// 不是图片
			String contentType = URLConnection.guessContentTypeFromStream(bis);
			if (!(StringUtils.hasText(contentType) && contentType.startsWith("image"))) {
				log.warn("not an image url: {}", url);
				return;
			}
			// 压缩图片
			Thumbnails.of(bis)
					.size(32, 32)
					.outputFormat("jpg") // ico不支持
					.toFile(savePath);
		} catch (Exception e) {
			log.warn("favicon.icon url is invalid.");
		}
	}

}
