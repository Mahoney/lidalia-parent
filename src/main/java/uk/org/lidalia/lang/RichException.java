package uk.org.lidalia.lang;

public class RichException extends Exception {

	private static final long serialVersionUID = 1L;

	public RichException() {
		super();
	}

	public RichException(String message) {
		super(message);
	}

	public RichException(Throwable cause) {
		super(cause);
	}

	public RichException(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	public String toString() {
		return Exceptions.throwableToString(super.toString(), getCause());
	}

	public boolean instanceOf(Class<?> possibleSuperType) {
		return possibleSuperType.isAssignableFrom(getClass());
	}
}
