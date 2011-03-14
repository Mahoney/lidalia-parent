package uk.org.lidalia.lang;

public final class ClassUtils {

    public static boolean inSameClassHierarchy(Class<?> a, Class<?> b) {
        return a.isAssignableFrom(b) || b.isAssignableFrom(a);
    }

    public static boolean instanceOf(Object o, Class<?> possibleSuperType) {
        return possibleSuperType.isAssignableFrom(o.getClass());
    }

    private ClassUtils() {
        throw new UnsupportedOperationException("Not instantiable");
    }
}
