package xyz.snwjas.blog.mapper;

import org.apache.ibatis.annotations.Param;
import xyz.snwjas.blog.model.entity.LinkEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 友链 Mapper 接口
 * </p>
 *
 * @author Myles Yang
 */
public interface LinkMapper extends BaseMapper<LinkEntity> {

	/**
	 * 根据Logo解析器更新全部记录Logo
	 */
	Integer updateLogoByParser(@Param("parser") String parser);

}
