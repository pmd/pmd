PROCEDURE IsOfType (
inChannelID IN number,
inOperID IN number,
inClientId IN number,
ioFPOobj IN FPO_OBJ,
inPackageIDout IN number,
inStatusId IN number)
is
loFPOGE_OBJ FPOGE_OBJ;
BEGIN

IF ioFPOobj IS OF (FPOGE_OBJ) THEN
loFPOGE_OBJ:=treat(ioFPOobj AS FPOGE_OBJ);
end if;

IF ioFPOobj IS NOT OF TYPE (ONLY FPOGE_OBJ) THEN
loFPOGE_OBJ:=treat(ioFPOobj AS FPOGE_OBJ);
end if;

loFPOGE_OBJ:=SELECT A FROM persons p WHERE VALUE(p) IS OF TYPE (employee_t);
loFPOGE_OBJ:=SELECT A FROM persons p WHERE VALUE(p) IS NOT OF TYPE (ONLY employee_t, other_t);

end;