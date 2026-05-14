/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// VIOLATION expected - abstract class defines only hashCode (dispatch bug still occurs)
public class MapUser {
    Map<IKey, String> fieldMap;
}

