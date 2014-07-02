package com.vteba.common.exception;

/**
 * 持久层查询非唯一结果异常。
 * @author yinlei
 * @date 2014-07-02 21:39
 */
public class NonUniqueException extends RuntimeException {

	private static final long serialVersionUID = 8743420863120805829L;

	public NonUniqueException() {
	}

	public NonUniqueException(String message) {
		super(message);
	}

	public NonUniqueException(Throwable cause) {
		super(cause);
	}

	public NonUniqueException(String message, Throwable cause) {
		super(message, cause);
	}

	public NonUniqueException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
