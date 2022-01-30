// See https://github.com/pmd/pmd/issues/2839
// and https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/langCon_apex_SafeNavigationOperator.htm

public class Foo {
    Integer x = anObject?.anIntegerField; // The expression is of type Integer because the field is of type Integer

    // New code using the safe navigation operator
    String profileUrl = user.getProfileUrl()?.toExternalForm();

    public void bar1(Object a) {
        a?.b; // Evaluates to: a == null ? null : a.b
        ((T)a1?.b1)?.c1();
    }

    public void bar2(Object[] a, int x) {
        a[x]?.aMethod().aField; // Evaluates to null if a[x] == null
        a[x].aMethod()?.aField;
    }

    public String getName(int accId) {
        String s = contact.Account?.BillingCity;
        // New code using the safe navigation operator
        return [SELECT Name FROM Account WHERE Id = :accId]?.Name;
    }
}