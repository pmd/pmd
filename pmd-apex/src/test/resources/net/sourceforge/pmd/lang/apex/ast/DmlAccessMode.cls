public class DmlAccessMode {
    void example(Account a, Account b) {
        insert a;
        insert as user a;
        insert as system a;
        update a;
        update as user a;
        update as system a;
        upsert a;
        upsert as user a;
        upsert as system a;
        delete a;
        delete as user a;
        delete as system a;
        undelete a;
        undelete as user a;
        undelete as system a;
        merge a b;
        merge as user a b;
        merge as system a b;
    }
}
