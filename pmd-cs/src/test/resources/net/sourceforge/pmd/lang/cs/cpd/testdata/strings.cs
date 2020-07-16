class Foo {
  void bar() {

    var test = $@"test";
    var test2 = @$"test";

    String query =
      @"SELECT foo, bar
         FROM table
         WHERE id = 42";
  }
}