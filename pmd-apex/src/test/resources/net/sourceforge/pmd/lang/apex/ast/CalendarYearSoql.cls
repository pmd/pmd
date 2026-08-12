public with sharing class CustomRollupExample {

    public static void runRollup() {

        List<AggregateResult> results = [
            SELECT
                ExternalId__c,
                CALENDAR_YEAR(CustomDateField__c) year,
                SUM(CustomAmountField__c) amount,
                COUNT(Id) counter
            FROM CustomObject__c
            GROUP BY ROLLUP(
                CALENDAR_YEAR(CustomDateField__c),
                ExternalId__c
            )
            ORDER BY
                ExternalId__c ASC NULLS FIRST,
                CALENDAR_YEAR(CustomDateField__c) ASC NULLS LAST
            LIMIT 2000
        ];

        for (AggregateResult ar : results) {

            String externalId = (String) ar.get('ExternalId__c');
            Integer year = (Integer) ar.get('year');
            Decimal totalAmount = (Decimal) ar.get('amount');
            Integer recordCount = (Integer) ar.get('counter');

            System.debug('ExternalId: ' + externalId +
                         ' | Year: ' + year +
                         ' | Amount: ' + totalAmount +
                         ' | Count: ' + recordCount);
        }
    }
}
