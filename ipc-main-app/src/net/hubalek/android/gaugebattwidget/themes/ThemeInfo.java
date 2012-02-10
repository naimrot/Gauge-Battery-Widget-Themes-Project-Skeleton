package net.hubalek.android.gaugebattwidget.themes;

/**
 * Holds info about theme.
 */
public class ThemeInfo {
    /**
     * Package Name.
     */
    public String packageName;
    /**
     * Service Name.
     */
    public String serviceName;

    /**
     * Returns String representation of the theme.
     *
     * @return String representation of the theme.
     */
    @Override
    public String toString() {
        return packageName;
    }
}
