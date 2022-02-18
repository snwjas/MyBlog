package xyz.snwjas.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 附件
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("attachment")
@ApiModel(value = "AttachmentEntity对象", description = "附件")
public class AttachmentEntity extends BaseEntity {

	private static final long serialVersionUID = 6442928839623298459L;

	@ApiModelProperty(value = "文件id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "文件名（包括后缀）")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "文件大小（字节）")
	@TableField("size")
	private Long size;

	@ApiModelProperty(value = "文件路径")
	@TableField("path")
	private String path;

	@ApiModelProperty(value = "html资源类型")
	@TableField("media_type")
	private String mediaType;

	@ApiModelProperty(value = "文件缩略图路径")
	@TableField("thumb_path")
	private String thumbPath;

	@ApiModelProperty(value = "文件分组")
	@TableField("team")
	private String team;

	@ApiModelProperty(value = "文件为图片时，图片的宽度像素")
	@TableField(value = "width")
	private Integer width;

	@ApiModelProperty(value = "文件为图片时，图片的高度像素")
	@TableField(value = "height")
	private Integer height;

}
