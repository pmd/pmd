// https://github.com/pmd/pmd/issues/5055
// https://github.com/google/summit-ast/issues/53

public inherited sharing class SoslWithUsermode {

  public static String example() {
    String SecondarySearchList = 'test';

    List<List<SObject>> accountList = [
      find :SecondarySearchList
      in name fields
      returning Account(Id, Account.Name where ID = '' limit 100)
      with user_mode
    ];
  }

}
