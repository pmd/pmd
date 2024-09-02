// https://github.com/pmd/pmd/issues/5182

public class GroupingInSoql {
    private List<AggregateResult> getDefects() {
        AggregateResult[] defects = [
            SELECT
              OBJ1__c O1,
              OBJ2__c O2,
              OBJ3__c O3,
              SUM(OBJ4__c) O4,
              GROUPING(OBJ1__c) O1Group,
              GROUPING(OBJ2__c) O2Group,
              GROUPING(OBJ3__c) O3Group
            FROM OBJ4__c
            GROUP BY ROLLUP(OBJ1__c, OBJ2__c, OBJ3__c)
        ];
        return defects;
    }
}
