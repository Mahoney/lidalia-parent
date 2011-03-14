package uk.org.lidalia.lang;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;

public final class Collections3 {

	public static String toString(Collection<?> collection, String delimiter) {
		return toString(collection, "[", delimiter, "]");
	}

	public static <T> String toString(Collection<T> collection, String start, String delimiter, String end) {
		return toString(collection, start, delimiter, end, new Function<T, String>() {
			@Override
			public String apply(T object) {
				return object.toString();
			}
		});
	}

	public static <T> String toString(Collection<T> collection, String start, String delimiter, String end, Function<T, String> transform) {
		StringBuilder builder = new StringBuilder(start);
		for (Iterator<T> iterator = collection.iterator(); iterator.hasNext();) {
			T object = iterator.next();
			builder.append(transform.apply(object));
			if (iterator.hasNext()) {
				builder.append(delimiter);
			}
		}
		builder.append(end);
		return builder.toString();
	}

    private Collections3() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
