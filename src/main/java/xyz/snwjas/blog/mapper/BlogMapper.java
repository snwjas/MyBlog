package xyz.snwjas.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import xyz.snwjas.blog.model.entity.BlogEntity;

/**
 * <p>
 * 博客 Mapper 接口
 * </p>
 *
 * @author Myles Yang
 */
public interface BlogMapper extends BaseMapper<BlogEntity> {

	/**
	 * 对某列求和
	 * @param column 列名
	 * @param wrapper 条件构造器
	 * @return 列和
	 */
	Integer sum(@Param("column") String column, @Param(Constants.WRAPPER) Wrapper<BlogEntity> wrapper);


}
