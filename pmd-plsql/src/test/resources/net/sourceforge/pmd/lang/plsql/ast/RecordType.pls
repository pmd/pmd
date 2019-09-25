create or replace package body solt9001 is

  procedure upd is

  begin

      update solt90_web_service_log
      set    row = solt9001.rec
      returning record_version into solt9001.rec.record_version;

  end upd;

end;
/

create or replace package body solt9001 is

  procedure upd is

  begin

      update solt90_web_service_log
      set    row = solt9001.rec
      returning record_version into solt9001.rec.record_version;

  end upd;

end;
/

create or replace function test return varchar2 is
  begin

    insert into sol_individual_commission_rate
    values solt4601.rec
    returning record_version, rowid into solt4601.rec.record_version, solt4601.current_rowid;

    return null;
  end test;
/

begin

select id_no into t4666.rec.agent_no from name where company_reg_no = '66666111';

end;
/