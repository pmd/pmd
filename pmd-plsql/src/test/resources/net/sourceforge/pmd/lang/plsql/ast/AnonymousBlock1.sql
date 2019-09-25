declare
  gw_acl_name   varchar2(100);

  procedure create_acl(p_acl_name in varchar2) is
    cursor c_acl is
      select dna.acl
      from   dba_network_acls dna
      where  acl like '%' || p_acl_name;
    w_acl_name varchar2(100);
  begin
    open c_acl;
    fetch c_acl
      into w_acl_name;
    close c_acl;

  end;

begin
  null;
end;
/