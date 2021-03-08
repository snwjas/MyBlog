package xyz.snwjas.blog.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.snwjas.blog.model.enums.LogType;

import java.util.List;

/**
 * Log Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("日志搜索参数")
public class LogSearchParam extends BasePageParam {

	@ApiModelProperty("日志类型")
	private List<LogType> types;

}
