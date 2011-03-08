package uk.org.lidalia.lang;

public class Classes {

	public static boolean instanceOf(Object o, Class<?> c) {
		return c.isAssignableFrom(o.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <CompileTimeType> Class<? extends CompileTimeType> getClass(final CompileTimeType object) {
		return (Class<? extends CompileTimeType>) object.getClass();
	}
}
