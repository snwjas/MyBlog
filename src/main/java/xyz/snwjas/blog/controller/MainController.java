package xyz.snwjas.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.snwjas.blog.annotation.AccessLimit;
import xyz.snwjas.blog.config.properties.MyBlogProperties;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.model.vo.OptionVO;
import xyz.snwjas.blog.service.OptionsService;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.utils.RUtils;
import xyz.snwjas.blog.utils.URLUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Main Controller
 *
 * @author Myles Yang
 */
@RestController
@Api(value = "主控制器", tags = {"主接口"})
public class MainController {

	@Autowired
	private MyBlogProperties properties;

	@Autowired
	private OptionsService optionsService;

	/**
	 * 后台入口
	 */
	@GetMapping("${my-blog.admin-path:admin}")
	@ApiOperation("后台入口")
	public void toAdmin(HttpServletResponse response) throws IOException {
		response.sendRedirect("/" + properties.getAdminPath() + "/index.html");
	}

	/**
	 * 前台入口
	 */
	@GetMapping({"/", "/index"})
	@ApiOperation("前台入口")
	public void index(HttpServletResponse response) throws IOException {
		response.sendRedirect("/app/index.html");
	}

	@AccessLimit(maxCount = 2)
	@GetMapping("/favicon.ico")
	@ApiOperation("获取Favicon图标")
	public void getFavicon(HttpServletResponse response) throws IOException {
		OptionVO vo = optionsService.get(MyBlogOptionEnum.FAVICON.key());
		if (Objects.nonNull(vo) && StringUtils.hasText(vo.getOptionValue())) {
			response.sendRedirect(URLUtils.encodeAll(vo.getOptionValue()));
		}
	}

	@AccessLimit(maxCount = 2)
	@GetMapping("/attributes")
	@ApiOperation("获取博客的基本属性")
	public R getAttributes() {
		Map<String, Object> attributes = optionsService.listNecessary();
		return RUtils.success("基本属性", attributes);
	}

}
