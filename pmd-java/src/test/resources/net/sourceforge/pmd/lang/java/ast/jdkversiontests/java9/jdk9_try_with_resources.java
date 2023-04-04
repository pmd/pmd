/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class InputJava9TryWithResources {
 public static void main() {
  MyResource resource1 = new MyResource();
  MyResource resource2 = new MyResource();
  try (resource1) { }
  try (resource1;) { }
  try (resource1; resource2) { }
  try (resource1.foo) { }
  try (resource1.foo.a) { }
  try (resource1.foo.Type v = null) { }
  try (this.foo.aa) { }
  try (this.foo) { }
 }
}
