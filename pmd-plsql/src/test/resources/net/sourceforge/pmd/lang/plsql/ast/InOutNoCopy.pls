create or replace procedure InOutNoCopyTest(blankParam       varchar2,
                                            inParam          in integer,
                                            outParam         out varchar2,
                                            inOutParam       in out date,
                                            inOutNoCopyParam in out nocopy clob,
                                            outNoCpyParam    out nocopy blob)
is
begin
   null;
end InOutNoCopyTest;
/

create or replace package InOutNoCopyTestPck is

   procedure InOutNoCopyTest(blankParam       varchar2,
                             inParam          in integer,
                             outParam         out varchar2,
                             inOutParam       in out date,
                             inOutNoCopyParam in out nocopy clob,
                             outNoCpyParam    out nocopy blob);

end InOutNoCopyTestPck;
/

create or replace package body InOutNoCopyTestPck is

   procedure InOutNoCopyTest(blankParam       varchar2,
                             inParam          in integer,
                             outParam         out varchar2,
                             inOutParam       in out date,
                             inOutNoCopyParam in out nocopy clob,
                             outNoCpyParam    out nocopy blob) is
   begin
      null;
   end;

end InOutNoCopyTestPck;
/
