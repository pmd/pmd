/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// see https://github.com/pmd/pmd/issues/3642
public class GitHubBug3642 {
    @interface Foo {
        String v1()[]; // parse error
        // equivalent to String[] v1();
    }
}
