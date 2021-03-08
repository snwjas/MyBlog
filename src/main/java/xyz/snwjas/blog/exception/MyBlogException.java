package xyz.snwjas.blog.exception;

/**
 * Base exception of the project.
 *
 * @author Myles Yang
 */
public abstract class MyBlogException extends RuntimeException {

	private static final long serialVersionUID = 4140200838147465959L;

	private Object errorData;

	public MyBlogException(String message) {
		super(message);
	}

	public MyBlogException(String message, Throwable cause) {
		super(message, cause);
	}

	public abstract int getStatus();

	public Object getErrorData() {
		return errorData;
	}

	public void setErrorData(Object errorData) {
		this.errorData = errorData;
	}
}
