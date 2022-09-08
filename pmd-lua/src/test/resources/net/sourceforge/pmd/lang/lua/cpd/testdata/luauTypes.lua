--!strict
type Array<T = any> = { T }
local x = 31337
local _negativeLiteral = -3
local _negativeVariable = -x
local _notLiteral = not true
local _notVariable = not x
local _length = #{x}
export type Function<T... = ...any> = (...any) -> T...
local _PlatformService = nil
local game = require(script.Parent.game) :: any
pcall(function() _PlatformService = game:GetService('PlatformService') end)


return function <T>(req, ...: boolean): ({[string|number]: T}, string, Function<...any>)
  local body = string.format("%s %s\n", req.method, req.path)
  local res = {
    code = 200,
    { "Content-Type", "text/plain" },
    { "Content-Length", #body } :: Array<any>,
  } :: { [any]: number | Array<string | boolean> }
  if (req :: any).keepAlive then
    local socketType: "Connection" | "Pingback" | "" = "" :: ""
    socketType = "Connection" :: "Connection"
    res[#res + 1] = { socketType :: string, "Keep-Alive" }
    res[#res - 2] = { ... }
  end

  return (res :: any) :: { T }, body, function(...): ...any return ... end
end