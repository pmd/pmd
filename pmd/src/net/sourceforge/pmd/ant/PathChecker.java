package net.sourceforge.pmd.ant;

public class PathChecker {

    private boolean isWindowsVariant;

    public PathChecker(String osNameProperty) {
        isWindowsVariant = osNameProperty.toLowerCase().indexOf("windows") != -1;
    }

    public boolean isAbsolute(String path) {
        if (!isWindowsVariant) {
            return path.charAt(0) == '/';
        }
        return path.charAt(1) == ':';
    }
}
