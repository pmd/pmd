function(arg) {
    notDeclaredVariable = 1;    // this will create a global variable and trigger the rule

    var someVar = 1;            // this is a local variable, that's ok

    window.otherGlobal = 2;     // this will not trigger the rule, although it is a global variable.
}
