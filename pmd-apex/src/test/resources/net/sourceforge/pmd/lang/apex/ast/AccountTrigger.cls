// see https://github.com/pmd/pmd/issues/5139
trigger AccountTrigger on Account (before insert, before update) {
    integer i = 0;
    for (i = 0; i <15; i++) {
        SObjectType token = Schema.getGlobalDescribe().get('Account');
    }
    integer anotherField = 2;
    System.debug('test');
}
