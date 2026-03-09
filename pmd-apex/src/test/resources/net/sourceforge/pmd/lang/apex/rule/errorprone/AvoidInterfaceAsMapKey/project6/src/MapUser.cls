/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// VIOLATION expected - concrete subclass of abstract implementor defines equals/hashCode
public class MapUser {
    Map<IKey, String> fieldMap;
}

