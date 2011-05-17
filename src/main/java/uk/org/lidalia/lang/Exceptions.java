/* 
 * Copyright (c) 2009-2010 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.lang;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class Exceptions {

	public static RuntimeException asRuntimeException(final Throwable throwable) {
		final RuntimeException result;
		if (throwable == null) {
			throw new IllegalArgumentException("Throwable argument cannot be null");
		} else if (throwable instanceof Error) {
			throw (Error) throwable;
		} else if (throwable instanceof RuntimeException) {
			result = (RuntimeException) throwable;
		} else if (throwable instanceof InterruptedException || throwable instanceof InterruptedIOException) {
			throw new IllegalArgumentException(
					"An interrupted exception needs to be handled to end the thread, or the interrupted status needs to be " +
					"restored, or the exception needs to be propagated explicitly - it should not be used as an argument to " +
					"this method", throwable);
		} else if (throwable instanceof InvocationTargetException || throwable instanceof ExecutionException) {
			result = asRuntimeException(throwable.getCause());
		} else {
			result = new WrappedCheckedException(throwable);
		}
		return result;
	}

	public static String throwableToString(String baseToString, List<Throwable> causes) {
		if (causes.isEmpty()) {
			return baseToString;
		} else {
			StringBuilder stringValue = new StringBuilder(baseToString);
			stringValue.append("; caused by: ").append(causes);
			return stringValue.toString();
		}
	}

	private Exceptions() {
		throw new UnsupportedOperationException("Not instantiable");
	}

	static List<Throwable> buildUnmodifiableCauseList(Throwable cause, Throwable[] otherCauses) {
		ArrayList<Throwable> causes = new ArrayList<Throwable>(otherCauses.length + 1);
		causes.add(cause);
		causes.addAll(Arrays.asList(otherCauses));
		return Collections.unmodifiableList(causes);
	}
}
