package xyz.snwjas.blog.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 博客数据统计View Object
 *
 * @author Myles Yang
 */

@Data
@Accessors(chain = true)
public class StatisticsBasicVO {

	private static final long serialVersionUID = 4310667613112942914L;

	private Integer blogCount;

	private Integer commentCount;

	private Integer categoryCount;

	private Integer tagCount;

	private Integer linkCount;

	private Integer establishDaysCount;

	// @JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime birthday;

	private Integer blogTotalVisits;

	private Integer webTotalVisits;

}
