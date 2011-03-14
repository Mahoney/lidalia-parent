package uk.org.lidalia.lang;

public final class EqualityUtils {
    static boolean equal(Object a, Object b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    private EqualityUtils() {
        throw new UnsupportedOperationException("Not instantiable");
    }
}
