package foo.bar.baz;
@SuppressWarnings({"woof","CPD-START"})

@SuppressWarnings("CPD-START")

@ MyAnnotation ("ugh")
@NamedQueries({
                  @NamedQuery(
                  )})

public class Foo {}
@SuppressWarnings({"ugh","CPD-END"})
class Other {}
