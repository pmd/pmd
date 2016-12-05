public class Foo<K,V> {
  public <A extends K, B extends V> Foo(Bar<A,B> t) {}
}