--!strict
type Array<T = any> = { T }
local x = 31337
local _negativeLiteral = -3
local _negativeVariable = -x
local _notLiteral = not true
local _notVariable = not x
local _length = #{x}
export type Function<T... = ...any> = (...any) -> T...

return function (req, ...: boolean): ({[string|number]: any}, string, Function<...any>)
  local body = string.format("%s %s\n", req.method, req.path)
  local res = {
    code = 200,
    { "Content-Type", "text/plain" },
    { "Content-Length", #body } :: Array<any>,
  } :: { [any]: number | Array<string | boolean> }
  if req.keepAlive then
    res[#res + 1] = { "Connection", "Keep-Alive" }
    res[#res + 2] = { ... }
  end

  return res, body, function(...): ...any return ... end
end