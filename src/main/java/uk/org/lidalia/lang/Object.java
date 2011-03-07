package uk.org.lidalia.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Object {
	private static final Function<Class<?>, Set<Field>> GET_IDENTITY_FIELDS = CachedFunction.make(new Function<Class<?>, Set<Field>>() {
		@Override
		public Set<Field> call(Class<?> clazz) {
			Set<Field> result = new HashSet<Field>();
			for (Field field : clazz.getDeclaredFields()) {
				if (!field.isAnnotationPresent(Identity.class))
					continue;
				if (!Modifier.isPublic(field.getModifiers())) {
					field.setAccessible(true);
				}
				result.add(field);
			}
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != Object.class) {
				result.addAll(GET_IDENTITY_FIELDS.call(superclass));
			}
			return Collections.unmodifiableSet(result);
		}
		
	});
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private volatile int hashCode = 0;
	private volatile String toString = null;

	@Override
	public final boolean equals(java.lang.Object other) {
		// Usual equals checks
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}

		// One of the two must be a subtype of the other
		if (!inSameClassHierarchy(this.getClass(), other.getClass())) {
			return false;
		}

		// They must have precisely the same set of identity members to meet the
		// symmetric & transitive requirement of equals
		Set<Field> thisFields = GET_IDENTITY_FIELDS.call(this.getClass());
		if (thisFields.size() == 0) {
			return false;
		}
		Set<Field> otherFields = GET_IDENTITY_FIELDS.call(other.getClass());
		if (!thisFields.equals(otherFields)) {
			return false;
		}
		
		try {
			for (Field field : thisFields) {
				java.lang.Object otherValue = field.get(other);
				java.lang.Object thisValue = field.get(this);
				// Compare
				if (!equal(otherValue, thisValue))
					return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private boolean inSameClassHierarchy(Class<?> a, Class<?> b) {
		return a.isAssignableFrom(b) || b.isAssignableFrom(a);
	}

	@Override
	public int hashCode() {

		if (hashCode != 0) {
			return hashCode;
		}

		final int prime = 37;
		int result = 17;
		try {
			Set<Field> thisFields = GET_IDENTITY_FIELDS.call(this.getClass());
			java.lang.Object value = null;
			for (Field field : thisFields) {
				value = field.get(this);
				result = prime * result + (value == null ? 0 : value.hashCode());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (this instanceof Immutable) {
			hashCode = result;
		}
		return result;
	}

	@Override
	public String toString() {

		if (toString != null) {
			return toString;
		}

		Set<Field> thisFields = GET_IDENTITY_FIELDS.call(this.getClass());
		if (thisFields.isEmpty()) {
			return super.toString();
		}
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('[');
		try {
			java.lang.Object value = null;
			int i = 1;
			for (Field field : thisFields) {
				value = field.get(this);
				builder.append(value);
				if (i < thisFields.size()) {
					builder.append(", ");
				}
				i++;
			}
			builder.append("]");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (this instanceof Immutable) {
			toString = builder.toString();
		}
		return builder.toString();
	}
	
	private boolean equal(java.lang.Object a, java.lang.Object b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}
}
