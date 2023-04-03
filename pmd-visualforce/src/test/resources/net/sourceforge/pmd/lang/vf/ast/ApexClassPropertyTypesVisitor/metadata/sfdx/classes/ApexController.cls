public class ApexController {
    public Id AccountIdProp { get; set; }

    public ApexController() {
        acc = [SELECT Id, Name, Site FROM Account
                   WHERE Id = :ApexPages.currentPage().getParameters().get('id')];
        this.AccountIdProp = acc.Id;
    }

    public Id getAccountId() {
        return acc.id;
    }

    public String getAccountName() {
        return acc.name;
    }

    public InnerController getInnerController() {
        return new InnerController(this);
    }

    public class InnerController {
        private ApexController parent;
        public Id InnerAccountIdProp { get; set; }

        public InnerController(ApexController parent) {
            this.parent = parent;
            this.InnerAccountIdProp = parent.AccountIdProp;
        }

        public Id getInnerAccountId() {
            return 'Inner: ' + parent.acc.id;
        }

        public String getInnerAccountName() {
            return 'Inner: ' + parent.acc.name;
        }
    }
}