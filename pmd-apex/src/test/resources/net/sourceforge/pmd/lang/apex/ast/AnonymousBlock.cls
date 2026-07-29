//
// See https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_anonymous_block.htm
//

Integer int1 = 0;

void myProcedure1() {
    myProcedure2();
}

void myProcedure2() {
    int1++;
}

myProcedure1();
