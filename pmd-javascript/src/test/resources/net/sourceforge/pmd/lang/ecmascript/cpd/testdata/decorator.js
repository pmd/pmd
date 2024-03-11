// A simple decorator\n"
@annotation
class MyClass { }

function annotation(target) {
   // Add a property on target
   target.annotated = true;
}