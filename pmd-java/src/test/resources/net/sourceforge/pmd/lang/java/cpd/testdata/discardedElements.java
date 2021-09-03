/*
 * This comment is ignored
 */
package a.b.c; // ignored

// imports are ignored
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Properties;



@Foo // ignored
public class Foo { // class Bar
    // comments are ignored


    // semicolons are ignored
    int x;
    {
        x++;
        foo();
    }

    // annotations are ignored
    @AnnotationWithParams("ugh")
    @AnnotationWithParams({@Nested(1) ,
                           @Nested(2) ,
                           @Nested
        })
    public void foo() {

    }
}