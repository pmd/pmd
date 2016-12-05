require "socket"

gs = TCPServer.open(0)
addr = gs.addr
addr.shift

while true
  ns = gs.accept
  print(ns, " is accepted")
  Thread.start do
    s = ns                      # save to dynamic variable
    while s.gets
      s.write($_)
    end
    print(s, " is 
               gone
                       and
                               dead")
    s.close
  end
end

