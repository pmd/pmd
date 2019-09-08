declare

  cursor c_queue_exists(cp_queue_name xla_reference_code.code%type) is
    select 1
    from   dba_queues dq
    where  dq.name = cp_queue_name;

  r_queue_exists c_queue_exists%rowtype;
begin
  null;
end;
/
