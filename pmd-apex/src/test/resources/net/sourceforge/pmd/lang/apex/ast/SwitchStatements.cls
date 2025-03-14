public class ApexSwitchStatements {
    public void example1() {
        int i = 3;
        switch on i {
           when 2 {
               System.debug('when block 2');
           }
           when 3, 4, 5 {
               System.debug('when block 3 and 4 and 5');
           }
           when 6, 7 {
               System.debug('when block 6 and 7');
           }
           when -3 {
               System.debug('when block -3');
           }
           when null {
               System.debug('bad integer');
           }
           when else {
               System.debug('default');
           }
        }
    }

    public void example2() {
        int i = 1;
        switch on someInteger(i) {
           when 2 {
               System.debug('when block 2');
           }
           when 3 {
               System.debug('when block 3');
           }
           when else {
               System.debug('default');
           }
        }
    }
    private int someInteger(int i) {
        return i + 1;
    }

    public void example3() {
        switch on sobject {
           when Account a {
               System.debug('account ' + a);
           }
           when Contact c {
               System.debug('contact ' + c);
           }
           when null {
               System.debug('null');
           }
           when else {
               System.debug('default');
           }
        }
    }

    public void example4() {
        switch on season {
           when WINTER {
               System.debug('boots');
           }
           when SPRING, SUMMER {
               System.debug('sandals');
           }
           when else {
               System.debug('none of the above');
           }
        }
    }
}