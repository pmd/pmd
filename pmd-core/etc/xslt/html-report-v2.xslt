<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:scan="http://pmd.sourceforge.net/report/2.0.0">
<xsl:output method="html" doctype-system="about:legacy-compat"/>

<xsl:template match="scan:pmd">
  <xsl:variable name="version" select="@version" />
  <xsl:variable name="timestamp" select="@timestamp" />

  <html>
    <head>
    <title>PMD Report </title>

      <!-- Dependencies:
        https://getbootstrap.com/docs/5.3/getting-started/download/
        https://datatables.net/download/ (DataTables + Buttons + Column Visibility + HTML5 Export + JSZip + pdfmake + Print view
        https://releases.jquery.com/
        https://www.jsdelivr.com/package/npm/vega
        https://www.jsdelivr.com/package/npm/vega-lite
      -->

      <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous"/>
      <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.13.1/b-2.3.3/b-colvis-2.3.3/b-html5-2.3.3/b-print-2.3.3/datatables.min.css"/>

      <script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous" />
      <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"/>
      <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"/>
      <script type="text/javascript" src="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.13.1/b-2.3.3/b-colvis-2.3.3/b-html5-2.3.3/b-print-2.3.3/datatables.min.js"/>

      <script src="https://cdn.jsdelivr.net/npm/vega@5.22.1/build/vega.min.js" integrity="sha256-cx8BtoEWvBhaGWMiCWyjQnN0JMSZXEimZ09X68ln6cE=" crossorigin="anonymous"/>
      <script src="https://cdn.jsdelivr.net/npm/vega-lite@5.6.0/build/vega-lite.min.js" integrity="sha256-6NKNS22U5kO0J2/tMld/SvW/eyY6wbOW6w+9mB2Z2p4=" crossorigin="anonymous"/>
      <script src="https://cdn.jsdelivr.net/npm/vega-embed@6.21.0/build/vega-embed.min.js" integrity="sha256-/XLqtIcBhAjhlmMOaz23UAXzBgQymz60D73+PfrK24w=" crossorigin="anonymous"/>

    </head>
    <body>
      <nav class="nav navbar navbar-expand-md navbar-dark bg-dark fixed-top">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">PMD Report</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"/>
          </button>
        </div>
      </nav>
      <div class='container' style='margin-top:100px;'>
        <h5>Problems found ::  <small> Version: <xsl:value-of select="$version"/> @ <xsl:value-of select="$timestamp"/></small>
          <span id="summary"/>
        </h5>
        <table id='pmdTable' class='table table-hover table-striped'>
          <thead>
            <tr>
              <th>File #</th>
              <th>Issue #</th>
              <th>File</th>
              <th>Line</th>
              <th>Priority</th>
              <th>Category</th>
              <th>Problem</th>
            </tr>
          </thead>
          <xsl:for-each select="scan:file">
            <xsl:variable name="filename" select="@name" />
            <xsl:variable name="fileNumber" select="position()" />
            <xsl:for-each select="scan:violation">
              <tr>
                <td><xsl:value-of select="$fileNumber"/></td>
                <td><xsl:value-of select="position()"/></td>
                <td><xsl:value-of select="$filename"/> </td>
                <td><xsl:value-of select="@beginline"/></td>
                <td><xsl:value-of select="@priority"/></td>
                <td><xsl:value-of select="@ruleset"/></td>
                <td><a href="{@externalInfoUrl}"><xsl:value-of select="@rule"/></a></td>
              </tr>
            </xsl:for-each>
          </xsl:for-each>
        </table>
        <div id="vizBar"/>
        <div id="vizArc"/>
        <div id="vizArcRS"/>
      </div>
      <script>
        $(document).ready(
          function () {
            let priorityCounter = { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 };
            let categoryCounter = {
              'Best Practices': 0,
              'Code Style': 0,
              'Design': 0,
              'Documentation': 0,
              'Error Prone': 0,
              'Multithreading': 0,
              'Performance': 0,
              'Security': 0,
              'Other': 0
            };
            function mapCategory(rulesetName) {
              switch (rulesetName.toLowerCase()) {
                case 'best practices': return 'Best Practices';
                case 'code style': return 'Code Style';
                case 'design': return 'Design';
                case 'documentation': return 'Documentation';
                case 'error prone': return 'Error Prone';
                case 'multithreading': return 'Multithreading';
                case 'performance': return 'Performance';
                case 'security': return 'Security';
              }
              return 'Other';
            }

            let rows = $('#pmdTable tbody tr');
            let total = rows.length;
            rows.each(function() {
              let priority = $("td", this).slice(4, 5).text();
              priorityCounter[priority]++;

              let rulesetName = $("td", this).slice(5, 6).text();
              categoryCounter[mapCategory(rulesetName)]++;
            });

            const summaryEle = document.getElementById('summary');
            summaryEle.innerHTML = `
  <table class="table table-bordered table-hover table-striped">
    <tr>
      <th>Total</th>
      <th>P1</th>
      <th>P2</th>
      <th>P3</th>
      <th>P4</th>
      <th>P5</th>
    </tr>
    <tr>
      <td><strong>${total}</strong></td>
      <td>${priorityCounter['1']}</td>
      <td>${priorityCounter['2']}</td>
      <td>${priorityCounter['3']}</td>
      <td>${priorityCounter['4']}</td>
      <td>${priorityCounter['5']}</td>
    </tr>
  </table>`;

            $('#pmdTable').DataTable({
              dom: 'Blfrtip',
              buttons: [ 'copy', 'csv', 'excel', 'pdf', 'print']
            });

            // charting
            const arcSpec ={
              "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
              "description": "PMD violations",
              "data": {
                "values": [
                  {"priority": 1, "count": priorityCounter['1']},
                  {"priority": 2, "count": priorityCounter['2']},
                  {"priority": 3, "count": priorityCounter['3']},
                  {"priority": 4, "count": priorityCounter['4']},
                  {"priority": 5, "count": priorityCounter['5']}
                ]
              },
              "mark": {"type": "arc" , "innerRadius": 50, "tooltip": true},
              "encoding": {
                "theta": {"field": "count", "type": "quantitative"},
                "color": {"field": "priority", "type": "nominal"}
              },
            }
            vegaEmbed('#vizArc', arcSpec);

            const arcSpecRS ={
              "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
              "description": "PMD violations",
              "data": {
                "values": [
                  {"category": "Best Practices", "count": categoryCounter['Best Practices']},
                  {"category": "Code Style", "count": categoryCounter['Code Style']},
                  {"category": "Design", "count": categoryCounter['Design']},
                  {"category": "Documentation", "count": categoryCounter['Documentation']},
                  {"category": "Error Prone", "count": categoryCounter['Error Prone']},
                  {"category": "Multithreading", "count": categoryCounter['Multithreading']},
                  {"category": "Performance", "count": categoryCounter['Performance']},
                  {"category": "Security", "count": categoryCounter['Security']},
                  {"category": "Other", "count": categoryCounter['Other']}
                ]
              },
              "mark": {"type": "arc" , "innerRadius": 50, "tooltip": true},
              "encoding": {
                "theta": {"field": "count", "type": "quantitative"},
                "color": {"field": "category", "type": "nominal"}
              },
            }
            vegaEmbed('#vizArcRS', arcSpecRS);

            const barSpec =
             {
              "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
              "description": "PMD violations",
              "data": {
                  "values": [
                  {"priority": 1, "count": priorityCounter['1']},
                  {"priority": 2, "count": priorityCounter['2']},
                  {"priority": 3, "count": priorityCounter['3']},
                  {"priority": 4, "count": priorityCounter['4']},
                  {"priority": 5, "count": priorityCounter['5']}
                ]
              },
              "mark": { type: "bar","tooltip": true},
              "encoding": {
                "x": {"field": "priority", "type": "nominal", "axis": {"labelAngle": 0}},
                "y": {"field": "count", "type": "quantitative"}
              }
            }
            vegaEmbed('#vizBar', barSpec);
          }
        );
    </script>
    </body>
  </html>
</xsl:template>
</xsl:stylesheet>
