package xyz.snwjas.blog.exception;

import org.springframework.http.HttpStatus;
import xyz.snwjas.blog.constant.RS;

/**
 * 服务异常
 *
 * @author Myles Yang
 */
public class ServiceException extends MyBlogException {

	private static final long serialVersionUID = 7951201720502956459L;

	private final int status;

	public ServiceException() {
		super(RS.SYSTEM_ERROR.message());
		this.status = RS.SYSTEM_ERROR.status();
	}

	public ServiceException(String message) {
		super(message);
		this.status = RS.SYSTEM_ERROR.status();
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		this.status = RS.SYSTEM_ERROR.status();
	}

	public ServiceException(RS status) {
		super(status.message());
		this.status = status.status();
	}

	public ServiceException(HttpStatus status) {
		super(status.getReasonPhrase());
		this.status = status.value();
	}

	@Override
	public int getStatus() {
		return status;
	}


}
