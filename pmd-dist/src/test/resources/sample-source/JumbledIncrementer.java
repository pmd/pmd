/**
 * Sample file which triggers the JumbledIncrementerRule on line 8.
 * Used in the integration tests.
 */
public class JumbledIncrementer {
    public void foo() {
        for (int i = 0; i < 10; i++) {          // only references 'i'
            for (int k = 0; k < 20; i++) {      // references both 'i' and 'k'
                System.out.println("Hello");
            }
        }
    }
}
