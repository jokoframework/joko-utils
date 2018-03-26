package io.github.jokoframework.utils.exception;

/**
 * Unexpected error 
 *
 * @author bsandoval
 */
public class JokoUtilsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5029508179517400869L;

	public JokoUtilsException(String msg) {
		super(msg);
	}

	public JokoUtilsException(Throwable e) {
		super(e);
	}
	
	public JokoUtilsException(String msg, Throwable e) {
        super(msg, e);
    }
}
