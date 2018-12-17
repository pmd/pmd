PROCEDURE IsNull (
  dummy IN number
)
is
  V_BUF varchar(1);
BEGIN

IF V_BUF IS NULL THEN
  null;
end if;

IF V_BUF IS NOT NULL THEN
  null;
end if;

IF (V_BUF.x IS NOT NULL) THEN
  null;
end if;

end;