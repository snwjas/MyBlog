package xyz.snwjas.blog.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Attachment Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("附件搜索参数")
public class AttachmentSearchParam extends BasePageParam {

	@ApiModelProperty("附件名")
	private String name;

	@ApiModelProperty("附件类型")
	private String mediaType;

	private LocalDateTime createTime;

}
