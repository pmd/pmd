% CPD-OFF
function g = vec(op, y)
  opy = op(y);
  if ( any(size(opy) > 1) )
    g = @loopWrapperArray;
  end
  % CPD-ON
end