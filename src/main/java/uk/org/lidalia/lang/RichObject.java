package uk.org.lidalia.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.org.lidalia.lang.ClassUtils.inSameClassHierarchy;
import static uk.org.lidalia.lang.EqualityUtils.equal;

public class RichObject {

	private static final Function<Class<?>, Set<Field>> GET_IDENTITY_FIELDS = CachedFunction.make(new Function<Class<?>, Set<Field>>() {
		@Override public Set<Field> call(Class<?> clazz) {
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
			if (superclass != RichObject.class) {
				result.addAll(GET_IDENTITY_FIELDS.call(superclass));
			}
			return Collections.unmodifiableSet(result);
		}
		
	});
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override public final boolean equals(Object other) {
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
				if (!equal(field.get(other), field.get(this)))
					return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

    private volatile int hashCode = -1;

    @Override public int hashCode() {

		if (hashCode != -1) {
			return hashCode;
		}

		final int prime = 37;
		int result = 17;
		try {
			Set<Field> thisFields = GET_IDENTITY_FIELDS.call(this.getClass());
			for (Field field : thisFields) {
				Object value = field.get(this);
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

    private volatile String toString = null;

	@Override public String toString() {

		if (toString != null) {
			return toString;
		}

		Set<Field> thisFields = GET_IDENTITY_FIELDS.call(this.getClass());
		if (thisFields.isEmpty()) {
			return super.toString();
		}
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        Collection<String> values = CollectionUtils.collect(thisFields, new Function<Field, String>() {
            @Override
            public String call(Field field) {
                try {
                    return field.getName() + "=" + field.get(RichObject.this);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Field " + field + " is not accessible - should be!", e);
                }
            }
        });
        builder.append(CollectionUtils.toString(values, ", "));

		if (this instanceof Immutable) {
			toString = builder.toString();
		}
		return builder.toString();
	}

    public boolean instanceOf(Class<?> possibleSuperType) {
        return ClassUtils.instanceOf(this, possibleSuperType);
    }
}
