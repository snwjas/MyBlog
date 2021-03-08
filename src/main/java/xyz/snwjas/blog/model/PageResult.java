package xyz.snwjas.blog.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @author Myles Yang
 */
@Data
@ApiModel("分页结果")
public class PageResult<T> implements Serializable {

	private static final long serialVersionUID = -3862949514414749919L;

	@ApiModelProperty("数据总条数")
	private long total;

	@ApiModelProperty("分页数据")
	private List<T> list;

	public PageResult(@NonNull List<T> list) {
		this.total = list.size();
		this.list = list;
	}

	public PageResult(long total, @NonNull List<T> list) {
		this.total = total;
		this.list = list;
	}
}
