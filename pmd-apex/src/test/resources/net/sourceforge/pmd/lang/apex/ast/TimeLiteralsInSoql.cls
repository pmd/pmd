public class PmdTest {
    void runSoql() {
        Object o = [SELECT Break__c,Check_Out__c FROM VMS_Time_Card_Item__c WHERE Time_Card__c =:timeCard.Id AND Check_Out__c = 01:00:00.000Z];
    }
}
