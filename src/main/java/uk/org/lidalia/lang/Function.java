package uk.org.lidalia.lang;

public interface Function<A, R> {
	R call(A arg);
}
