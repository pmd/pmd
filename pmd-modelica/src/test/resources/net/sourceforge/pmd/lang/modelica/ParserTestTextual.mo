model ParserTestTextual
  input Real x1;
  output Real y1;
  input Real x2;
  output Real y2;
equation
  der(y1) = x1;
  y2 = der(x2);
end ParserTestTextual;