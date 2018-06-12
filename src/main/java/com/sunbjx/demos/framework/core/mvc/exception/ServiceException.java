package com.sunbjx.demos.framework.core.mvc.exception;

import com.sunbjx.demos.framework.core.BaseException;

/**
 * @author sunbjx
 * @since 2018/6/11 14:10
 */
public class ServiceException extends BaseException {
    private static final long serialVersionUID = -192627484453909172L;
    
    private static final int EXCEPTION_CODE = 500;


    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ServiceException(String message) {
        super(EXCEPTION_CODE, message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public ServiceException(String message, Throwable cause) {
        super(EXCEPTION_CODE, message, cause);
    }
}
