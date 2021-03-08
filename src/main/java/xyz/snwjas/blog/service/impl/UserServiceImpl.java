package xyz.snwjas.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.annotation.ActionRecord;
import xyz.snwjas.blog.mapper.UserMapper;
import xyz.snwjas.blog.model.UserDetail;
import xyz.snwjas.blog.model.entity.UserEntity;
import xyz.snwjas.blog.model.enums.LogType;
import xyz.snwjas.blog.model.vo.UserVO;
import xyz.snwjas.blog.service.UserService;
import xyz.snwjas.blog.utils.IPUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * User Service Impl.
 *
 * @author Myles Yang
 */
@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Override
	public UserDetail getByUsername(@NonNull String username) {
		UserEntity userEntity = userMapper.selectOne(
				Wrappers.lambdaQuery(UserEntity.class)
						.eq(UserEntity::getUsername, username));

		return new UserDetail().convertFrom(userEntity);
	}

	@Override
	public UserDetail getByUserId(int userId) {
		UserEntity userEntity = userMapper.selectOne(
				Wrappers.lambdaQuery(UserEntity.class)
						.eq(UserEntity::getId, userId));

		return new UserDetail().convertFrom(userEntity);
	}

	@ActionRecord(content = "'用户修改个人信息，id：'+#userId",
			type = LogType.PROFILE_UPDATED, condition = "#ret > 0")
	@Override
	public int updateProfile(int userId, UserVO userVO) {
		return userMapper.update(null, getUpdateWrapper(userId, userVO));
	}

	@ActionRecord(content = "'用户修改密码，id：'+#userId",
			type = LogType.PASSWORD_UPDATED, condition = "#ret > 0")
	@Override
	public int updatePassword(int userId, String newPassword) {
		return userMapper.update(null,
				Wrappers.lambdaUpdate(UserEntity.class)
						.eq(UserEntity::getId, userId)
						.set(UserEntity::getPassword, newPassword)
		);
	}

	@Override
	public Authentication getAuth() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// 匿名用户 即为 未登录
		// if ("anonymousUser".equals(auth.getName()))
		return (auth instanceof AnonymousAuthenticationToken)
				? null
				: auth;
	}

	private Wrapper<UserEntity> getUpdateWrapper(int userId, UserVO vo) {
		String username = vo.getUsername();
		String nickname = vo.getNickname();
		String email = vo.getEmail();
		String description = vo.getDescription();
		String avatar = vo.getAvatar();

		return Wrappers.lambdaUpdate(UserEntity.class)
				.eq(UserEntity::getId, userId)
				.set(StringUtils.hasText(username), UserEntity::getUsername, username)
				.set(Objects.nonNull(nickname), UserEntity::getNickname, nickname)
				.set(Objects.nonNull(email), UserEntity::getEmail, email)
				.set(Objects.nonNull(description), UserEntity::getDescription, description)
				.set(Objects.nonNull(avatar), UserEntity::getAvatar, getLocalStaticUrl(avatar));
	}

	/**
	 * 获取静态资源的保存路径
	 * 如本地url: localhost:9527/static/file，保存为：/static/file
	 */
	private String getLocalStaticUrl(String url) {
		if (StringUtils.isEmpty(url)) {
			return "";
		}
		String serverName = IPUtils.getRequest().getServerName();
		if (url.startsWith(serverName)) {
			int i = url.indexOf("/static");
			if (i >= 0) {
				return url.substring(i);
			}
		}
		return url;
	}
}
