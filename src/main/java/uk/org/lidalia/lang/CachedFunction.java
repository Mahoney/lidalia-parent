package uk.org.lidalia.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CachedFunction<A, R> implements Function<A, R> {
	private final ConcurrentMap<A, FutureTask<R>> cache = new ConcurrentHashMap<A, FutureTask<R>>();
	private final Function<A, R> function;
	
	private CachedFunction(Function<A, R> operation) {
		this.function = operation;
	}

	@Override
	public R call(final A args) {
		while (true) {
			FutureTask<R> result = cache.get(args);
			if (result == null) {
                result = MapUtils.putIfAbsentReturningValue(cache, args, new FutureTask<R>(new Callable<R>() {
                    @Override
                    public R call() throws Exception {
                        return function.call(args);
                    }
                }));
                result.run();
			}
			try {
				return result.get();
			} catch (InterruptedException e) {
                Thread.currentThread().interrupt();
				cache.remove(args, result);
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RuntimeException)
					throw (RuntimeException) e.getCause();
				else if (e.getCause() instanceof Error)
					throw (Error) e.getCause();
				else
					throw new IllegalStateException("Checked exception...", e.getCause());
			}
		}
	}

    public static <A, R> CachedFunction<A, R> make(Function<A, R> operation) {
		return new CachedFunction<A, R>(operation);
	}
}
