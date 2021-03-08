package xyz.snwjas.blog.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.base.BeanConvert;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客每天的统计数据
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
public class StatisticsReportVo implements BeanConvert, Serializable {

	private static final long serialVersionUID = -1362266664886856765L;

	private Integer webVisitCount;

	private Integer blogVisitCount;

	private Integer commentCount;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime date;

}
