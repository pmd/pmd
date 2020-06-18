using System.Text; // ignored
using System; // ignored

// rest is not ignored
public class MyClass {

    void foo() {
        using (Font font1 = new Font("Arial", 10.0f)) {
          byte charset = font1.GdiCharSet;
        }

        using var font1 = new Font("Arial", 10.0f);
        byte charset = font1.GdiCharSet;
    }

}
