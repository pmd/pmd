// https://github.com/pmd/pmd/issues/5163

public with sharing class ToLabelInSosl {
    public Object doSoslSearch() {

        List<List<SObject>> searchResults = [
                FIND :searchTerm
                IN ALL FIELDS
                RETURNING
                        Account(Id, toLabel(Name))
                LIMIT 10
        ];

        return null;
    }
}
