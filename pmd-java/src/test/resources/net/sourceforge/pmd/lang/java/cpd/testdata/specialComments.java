package foo.bar.baz;
// CPD-OFF
// CPD-OFF
// another irrelevant comment
@ MyAnnotation ("ugh")
@NamedQueries({
                  @NamedQuery(
                  )})

public class Foo {// CPD-ON

    // special multiline comments
    class Foo /* CPD-OFF */{ } /* CPD-ON */
    class Foo /* CPD-OFF */{

        {something();}

    } /* CPD-ON */
}






