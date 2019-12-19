model ParserTestGraphical
  Modelica.Blocks.Continuous.Integrator integrator annotation(
    Placement(visible = true, transformation(origin = {-8, 6}, extent = {{-10, -10}, {10, 10}}, rotation = 0)));
  Modelica.Blocks.Sources.RealExpression realExpression(y = 1.0e2)  annotation(
    Placement(visible = true, transformation(origin = {-82, 6}, extent = {{-10, -10}, {10, 10}}, rotation = 0)));
  Modelica.Blocks.Interfaces.RealOutput y annotation(
    Placement(visible = true, transformation(origin = {76, 6}, extent = {{-10, -10}, {10, 10}}, rotation = 0), iconTransformation(origin = {76, 6}, extent = {{-10, -10}, {10, 10}}, rotation = 0)));
equation
  connect(realExpression.y, integrator.u) annotation(
    Line(points = {{-70, 6}, {-20, 6}, {-20, 6}, {-20, 6}}, color = {0, 0, 127}));
  connect(integrator.y, y) annotation(
    Line(points = {{4, 6}, {66, 6}, {66, 6}, {76, 6}}, color = {0, 0, 127}));

annotation(
    uses(Modelica(version = "3.2.2")));end ParserTestGraphical;