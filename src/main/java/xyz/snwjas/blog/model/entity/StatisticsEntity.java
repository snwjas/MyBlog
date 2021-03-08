package xyz.snwjas.blog.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 统计表（统计每日的数据）
 * </p>
 *
 * @author Myles Yang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("statistics")
@ApiModel(value = "StatisticsEntity对象", description = "统计表（统计每日的数据）")
public class StatisticsEntity extends BaseEntity {

	private static final long serialVersionUID = -2832150898756464414L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "网站访问量")
	@TableField("web_visit_count")
	private Integer webVisitCount;

	@ApiModelProperty(value = "文章访问量")
	@TableField("blog_visit_count")
	private Integer blogVisitCount;

	@ApiModelProperty(value = "评论数量")
	@TableField("comment_count")
	private Integer commentCount;

	@ApiModelProperty(value = "统计日期")
	@TableField("date")
	private LocalDateTime date;

}
