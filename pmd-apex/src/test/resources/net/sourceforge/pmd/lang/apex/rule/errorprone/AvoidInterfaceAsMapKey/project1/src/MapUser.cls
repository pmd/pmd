/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class MapUser {
    // Field declaration - should trigger violation
    Map<IKey, String> fieldMap;

    // Method parameter - should trigger violation
    public void processMap(Map<IKey, String> paramMap) {
    }

    // Local variable - should trigger violation
    public void createMap() {
        Map<IKey, String> localMap = new Map<IKey, String>();
    }
}

