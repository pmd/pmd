public class FieldTypeMismatch {
    public static void run() {
        Account account = new Account();
        account.Known_Number__c = 'hello';
    }
}
