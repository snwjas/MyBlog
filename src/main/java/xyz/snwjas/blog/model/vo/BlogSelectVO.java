package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.base.BeanConvert;

import java.io.Serializable;

/**
 * Blog Select VO
 * 评论中选择文章搜索
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("评论中选择文章搜索的视图对象")
public class BlogSelectVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 9072913105080249037L;

	@ApiModelProperty("文章ID")
	private Integer id;

	@ApiModelProperty("文章标题")
	private String title;

}
