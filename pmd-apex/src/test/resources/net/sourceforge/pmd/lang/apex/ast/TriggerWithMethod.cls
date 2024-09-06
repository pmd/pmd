// based on https://github.com/SalesforceLabs/EmailResponseConsole/blob/master/triggers/HVEMApprovalProcessAction.trigger
trigger TriggerWithMethod on DraftEmailMessage__c (after update){
    String body = 'email body';

    for(DraftEmailMessage__c demInstance : Trigger.New){
            sendEmail(demInstance, emailBody);
    }

    private void sendEmail(DraftEmailMessage__c demInstance, String emailBody) {
    }
}
