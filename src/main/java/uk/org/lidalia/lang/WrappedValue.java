package uk.org.lidalia.lang;

import org.apache.commons.lang.Validate;

public abstract class WrappedValue<E> {

	protected final E wrappedValue;

	public WrappedValue(E wrappedValue) {
		Validate.notNull(wrappedValue);
		this.wrappedValue = wrappedValue;
	}

	@Override
	public String toString() {
		return wrappedValue.toString();
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().hashCode();
		result = prime * result + wrappedValue.hashCode();
		return result;
	}

	@Override
	public final boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other.getClass().equals(this.getClass())))
			return false;
		WrappedValue<?> otherWrappedValue = (WrappedValue<?>) other;
		return wrappedValue.equals(otherWrappedValue.wrappedValue);
	}
}
