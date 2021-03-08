package xyz.snwjas.blog.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.snwjas.blog.model.base.BeanConvert;

import java.util.Collection;

/**
 * SpringSecurity 中认证的用户核心信息
 *
 * @author Myles Yang
 */
@Data
public class UserDetail implements UserDetails, BeanConvert {

	private static final long serialVersionUID = -1339790632097707968L;

	private Integer id;

	private String username;

	private String password;

	private String nickname;

	private String email;

	private String avatar;

	private String description;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("admin");
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
