package xyz.snwjas.blog.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Link Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("友链搜索参数")
public class LinkSearchParam extends BasePageParam {

	@ApiModelProperty("友链名称")
	private String name;

	@ApiModelProperty("友链url")
	private String url;

	@ApiModelProperty("关键词：名称或url")
	private String keyword;

}
