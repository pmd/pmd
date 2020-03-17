public class SomeClass {


    public static boolean field1 = false;
    public static boolean field2 = false;

    public static boolean field3 = false;
    public static boolean field4 = false;


    public static void onBeforeInsert(List<Object1__c> object1List){
        onBeforeInsert_update1(object1List);
        onBeforeInsert_update2(object1List);
    }

    public static void onAfterInsert(Map<Id, Object1__c> object1Map){
        onAfterInsert_postSomething();
    }

    public static void onBeforeUpdate(Map<Id, Object1__c> oldObject1Map, Map<Id, Object1__c> newObject1Map){
        onBeforeUpdate_update3(newObject1Map.values());
        onBeforeUpdate_update4(newObject1Map.values());
    }

    public static void onAfterUpdate(Map<Id, Object1__c> oldObject1Map, Map<Id, Object1__c> newObject1Map){
        onAfterUpdate_postSomething();
    }


    //----------- PRIVATE METHOD ----------
    private static Set<Id> objectIds;
    private static List<Item> itemList;
    private static List<Object1__c> List1;
    private static List<Object1__c> List2;

    private static void onBeforeInsert_update2(List<Object1__c> object1List){
        for(Object1__c object1 : object1List){
            String s = Something.getSomething(object1.Name);
            if((String)object1.get(s) != null){
                object1.txt_ParentId__c = (String)object1.get(s);
            }
        }
    }

    private static void onBeforeInsert_update1(List<Object1__c> object1List){
        updateSomething(object1List);
    }

    private static void onBeforeUpdate_update3(List<Object1__c> object1List){
        for(Object1__c object1 : object1List){
            String s = Something.getSomething(object1.Name);
            if((String)object1.get(s) != null){
                object1.txt_ParentId__c = (String)object1.get(s);
            }
        }
    }

    private static void onBeforeUpdate_update4(List<Object1__c> object1List){
        updateSomething(object1List);
    }

    private static void onAfterInsert_postSomething(){

        itemList = new List<Item>();
        itemToSomething1();
        itemToSomething2();
        insert itemList;
    }

    private static void onAfterUpdate_postSomething(){
        itemList = new List<Item>();
        itemToSomething1();
        itemToSomething2();
        insert itemList;
    }

    //-------------- HELPER FUNCTION -----------------
    private static void itemToSomething1(){
        if(!List1.isEmpty()){
            Map<Id, Object1__c> object1Map = new Map<Id, Object1__c>([SELECT Id, Name
                                                                                     FROM Object1__c
                                                                                     WHERE someId in: objectIds]);

            for(Object1__c object1 : List1){
                if(object1Map.get(object1.Id).Id != null && object1.Text != null){
                    Item post = new Item();
                    post.ParentId = object1Map.get(object1.Id).Id;
                    post.Body = 'Something';

                    itemList.add(post);
                }
            }
        }
    }

    private static void itemToSomething2(){
        if(!List2.isEmpty()){
            for(Object1__c object1 : List2){
                if(object1.Text != null && object1.Id != null){
                    Item post = new Item();
                    post.ParentId = object1.Id;
                    post.Body = 'Something';
                    itemList.add(post);
                }
            }
        }
    }

    private static void updateSomething(List<Object1__c> object1List){
        objectIds = new Set<Id>();
        List1 = new List<Object1__c>();
        List2 = new List<Object1__c>();

        for(Object1__c object1 : object1List){
            if(object1.isSomething && object1.Text != null){
                objectIds.add(object1.Text);
                List1.add(object1);
                object1.isTrue = false;
                object1.Date = System.now();
            }

            if(object1.isFalse){
                List2.add(object1);
                object1.isTrue = false;
                object1.Date = System.now();
            }
        }
    }
}
