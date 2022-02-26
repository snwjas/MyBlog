package xyz.snwjas.blog.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.snwjas.blog.annotation.AccessLimit;
import xyz.snwjas.blog.constant.MyBlogOptionEnum;
import xyz.snwjas.blog.model.vo.OptionVO;
import xyz.snwjas.blog.model.vo.UserVO;
import xyz.snwjas.blog.service.OptionsService;
import xyz.snwjas.blog.service.StatisticsService;
import xyz.snwjas.blog.service.UserService;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.UserDetail;
import xyz.snwjas.blog.utils.RUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Index Controller
 *
 * @author Myles Yang
 */
@RestController("AppIndexController")
@RequestMapping("/api/app")
@Api(value = "前台通用控制器", tags = {"前台通用接口"})
public class IndexController {

	@Autowired
	private OptionsService optionsService;

	@Autowired
	private StatisticsService statisticsService;

	@Autowired
	private UserService userService;

	@AccessLimit(maxCount = 1)
	@GetMapping("/attributes")
	@ApiOperation("获取博客的基本属性")
	public R getAttributes() {

		Map<String, Object> attributes = optionsService.listNecessary();

		// 建站日期
		MyBlogOptionEnum birthdayEnum = MyBlogOptionEnum.BIRTHDAY;
		OptionVO birthdayVO = optionsService.get(birthdayEnum.key());
		LocalDateTime birthday = LocalDateTime.parse(birthdayVO.getOptionValue());
		attributes.put(birthdayEnum.key(), birthday);

		// 页脚
		MyBlogOptionEnum footerEnum = MyBlogOptionEnum.FOOTER;
		OptionVO footerVO = optionsService.get(footerEnum.key());
		attributes.put(footerEnum.key(), footerVO.getOptionValue());

		// 网站访问数量
		int webTotalVisit = statisticsService.getWebTotalVisit();
		attributes.put("visit", webTotalVisit);

		return RUtils.success("基本属性", attributes);
	}

	@AccessLimit(maxCount = 1)
	@GetMapping("/about-me")
	@ApiOperation("关于我")
	public R getAboutMe() {
		// 确保账号id为1
		UserDetail userDetail = userService.getByUserId(1);
		UserVO userVO = new UserVO().convertFrom(userDetail);
		userVO.setUsername("");

		return RUtils.success("关于我", userVO);
	}

}
