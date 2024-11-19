package ae2m.core.util;

import org.jetbrains.annotations.Nullable;

/**
 * Utility class for common operations.
 * Used for coherently checking values.
 */
public class Utilities {

    /**
     * Compares two objects for equality. The objects must be comparable.
     * Returns true if the objects are equal, false otherwise.
     */
    public static <T extends Comparable<T>, U extends Comparable<U>> boolean checkEqual (@Nullable T a, @Nullable U b) {
        if (a == null || b == null) return false; // If either object is null, return false
        return a.equals(b);
    }

    /**
     * Checks if the object is not null.
     */
    public static <T> boolean notNull (T a) {
        return a != null;
    }

}
