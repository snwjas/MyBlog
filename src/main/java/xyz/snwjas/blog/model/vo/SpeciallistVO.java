package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.base.BeanConvert;
import xyz.snwjas.blog.model.enums.SpecialListType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Special List VO
 *
 * @author Myles Yang
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
@ApiModel("特殊清单视图对象")
public class SpeciallistVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 1258610051667767051L;

	@ApiModelProperty("ID")
	private Integer id;

	@ApiModelProperty("类型")
	private SpecialListType type;

	@ApiModelProperty("内容")
	private String content;

	@ApiModelProperty("创建时间")
	private LocalDateTime createTime;

	public SpeciallistVO(SpecialListType type, String content) {
		this.type = type;
		this.content = content;
	}
}
