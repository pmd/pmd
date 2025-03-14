create procedure p (@v int) as begin
	declare @f int
	set @f = @v + 2
	select @f
end
