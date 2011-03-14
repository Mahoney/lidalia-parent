package uk.org.lidalia.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils {

    public static <OldType, NewType> Collection<NewType> collect(Collection<OldType> initial, Function<OldType, NewType> mapper) {
        Collection<NewType> result = new ArrayList<NewType>(initial.size());
        for (OldType old : initial) {
            result.add(mapper.call(old));
        }
        return result;
    }

    public static String toString(Collection<?> collection, String delimiter) {
        return toString(collection, "[", delimiter, "]");
    }

    public static String toString(Collection<?> collection, String before, String delimiter, String after) {
        Iterator<?> iter = collection.iterator();
        StringBuilder string = new StringBuilder();
        string.append(before);
        while (iter.hasNext()) {
            string.append(iter.next());
            if (iter.hasNext()) {
                string.append(delimiter);
            }
        }
        string.append(after);
        return string.toString();
    }

    public static String toString(Collection<?> collection, String before, String delimiter, String after, Function<?, String> objectToString) {

    }
}
