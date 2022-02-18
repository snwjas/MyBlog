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

	@ApiModelProperty("附件分组")
	private String team;

	@ApiModelProperty("附件分组模糊查询字段")
	private String likeTeam;

	private LocalDateTime createTime;

}
