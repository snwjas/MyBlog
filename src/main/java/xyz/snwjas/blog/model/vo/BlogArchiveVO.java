package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.snwjas.blog.model.base.BeanConvert;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客归档
 *
 * @author Myles Yang
 */
@Data
@ApiModel("博客归档视图对象")
public class BlogArchiveVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 5910901865665778938L;

	@ApiModelProperty("博客文章ID")
	private Integer id;

	@ApiModelProperty("博客文章标题")
	private String title;

	@ApiModelProperty("博客文章URL")
	private String url;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
