import java.util.concurrent.Callable;

class A {
  A(Callable<Boolean> x) {}
}

class B extends A {
  B() {
    super(
        () -> switch X():
          case true -> True;
          default -> False;
    );
  }

  static boolean X() {
    return true;
  }
}
