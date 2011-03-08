package uk.org.lidalia.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.google.common.base.Function;

public class CachedFunction<A, R> implements Function<A, R> {
	private final ConcurrentMap<A, Future<R>> cache = new ConcurrentHashMap<A, Future<R>>();
	private final Function<A, R> function;
	
	private CachedFunction(Function<A, R> operation) {
		this.function = operation;
	}

	@Override
	public R apply(final A arg) {
		while (true) {
			Future<R> result = cache.get(arg);
			if (result == null) {
				Callable<R> callable = new Callable<R>() {
					@Override
					public R call() throws RichException {
						return function.apply(arg);
					}
				};
				FutureTask<R> task = new FutureTask<R>(callable);
				result = cache.putIfAbsent(arg, task);
				if (result == null) {
					result = task;
					task.run();
				}
			}
			try {
				return result.get();
			} catch (InterruptedException e) {
				cache.remove(arg, result);
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RichRuntimeException)
					throw (RichRuntimeException) e.getCause();
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
