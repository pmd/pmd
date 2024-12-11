public with sharing class NestedSubqueries {
    public NestedSubqueries() {
        Schema.Account account = [SELECT Id, (SELECT Id, (SELECT Id FROM CaseComments) FROM Cases) FROM Account LIMIT 1];
    }
}
