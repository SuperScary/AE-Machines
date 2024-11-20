package ae2m.api.bug;

/**
 * Marks a class, method, or field to contain a known bug.
 */
public @interface Bug {

    /**
     * Description of the bug.
     */
    String value();

}
