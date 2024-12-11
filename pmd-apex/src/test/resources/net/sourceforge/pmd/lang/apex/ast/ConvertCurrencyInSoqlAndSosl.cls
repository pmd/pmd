public class PmdTest {
    void queryOpportunities() {
        List<Opportunity> myList;

         myList = [
            SELECT convertcurrency(Amount)
            FROM Opportunity
        ];

        // with FORMAT()
        myList = [
            SELECT Amount, FORMAT(amount) Amt, convertCurrency(amount) convertedAmount,
                FORMAT(convertCurrency(amount)) convertedCurrency
            FROM Opportunity where id = '006R00000024gDtIAI'
        ];

        // FORMAT() with aggregate function
        myList = [ SELECT FORMAT(MIN(closedate)) Amt FROM opportunity ];
    }

    void soslQueries() {
        List<List<SObject>> searchResults;

        // label with alias
        searchResults = [
                FIND :searchTerm
                IN ALL FIELDS
                RETURNING
                        Account(Id, toLabel(Name) AliasName)
                LIMIT 10
        ];

        // convertCurrency
        searchResults = [ FIND 'test' RETURNING Opportunity(Name, convertCurrency(Amount), convertCurrency(Amount) AliasCurrency) ];

        // with FORMAT()
        searchResults = [ FIND 'Acme' RETURNING Account(AnnualRevenue, FORMAT(convertCurrency(AnnualRevenue)) convertedCurrency) ];

        // FORMAT() with aggregate function
        searchResults = [ FIND 'Acme' RETURNING Account(AnnualRevenue, FORMAT(MIN(CloseDate))) ];

    }
}
