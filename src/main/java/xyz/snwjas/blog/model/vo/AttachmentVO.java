package xyz.snwjas.blog.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.snwjas.blog.model.base.BeanConvert;
import xyz.snwjas.blog.model.base.ValidGroupType.Save;
import xyz.snwjas.blog.model.base.ValidGroupType.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Attachment VO
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("附件视图对象")
public class AttachmentVO implements BeanConvert, Serializable {

	private static final long serialVersionUID = 2383027373825151464L;

	@NotNull(groups = {Update.class})
	@Min(value = 1, groups = {Update.class})
	@ApiModelProperty("附件ID")
	private Integer id;

	@NotBlank(groups = {Save.class, Update.class})
	@Length(max = 255, groups = {Save.class, Update.class})
	@ApiModelProperty("附件名")
	private String name;

	@ApiModelProperty("附件大小（单位：字节）")
	private Long size;

	@ApiModelProperty("附件路径")
	private String path;

	@ApiModelProperty("附件资源类型")
	private String mediaType;

	@ApiModelProperty("附件缩略图路径")
	private String thumbPath;

	@ApiModelProperty(value = "文件分组")
	private String team;

	@ApiModelProperty("附件为图片时的宽度")
	private Integer width;

	@ApiModelProperty("附件为图片时的高度")
	private Integer height;

	private LocalDateTime createTime;

}
