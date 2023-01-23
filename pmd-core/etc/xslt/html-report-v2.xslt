<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:scan="http://pmd.sourceforge.net/report/2.0.0">
<xsl:output method="html"/>

<xsl:template match="scan:pmd">
  <xsl:variable name="version" select="@version" />
  <xsl:variable name="timestamp" select="@timestamp" />
  <xsl:variable name="count">0</xsl:variable>
  <xsl:variable name="total">0</xsl:variable>

  <html>
    <head>
    <title>PMD Report </title>
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous"/>
      <script src="https://code.jquery.com/jquery-3.6.1.min.js" integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
      <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.12.1/b-2.2.3/b-colvis-2.2.3/b-html5-2.2.3/b-print-2.2.3/datatables.min.css"/>
 
      <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
      <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
      <script type="text/javascript" src="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.12.1/b-2.2.3/b-colvis-2.2.3/b-html5-2.2.3/b-print-2.2.3/datatables.min.js"></script>

      <script src="https://cdn.jsdelivr.net/npm/vega@5.21.0"></script>
      <script src="https://cdn.jsdelivr.net/npm/vega-lite@5.2.0"></script>
      <script src="https://cdn.jsdelivr.net/npm/vega-embed@6.20.2"></script>

      <script>
        let total = 0;
        let p1 = 0 , p2 = 0, p3 = 0, p4 = 0, p5 = 0;
        let bp =0, codeStyle = 0, design = 0, doc = 0, ep = 0, perf = 0, security = 0;
      </script>
    </head>
    <body>
      <nav class="nav navbar navbar-expand-md navbar-dark bg-dark fixed-top">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">PMD Report</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
        </div>
      </nav>
      <div class='container' style='margin-top:100px;'>
        <h5>Problems found ::  <small> Version: <xsl:value-of select="$version"/> @ <xsl:value-of select="$timestamp"/></small>
          <span id="summary"></span>
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
                <xsl:variable name="priority" select="@priority" />
                <script> total++; </script>
                <xsl:choose>
                  <xsl:when test="$priority = 1">
                    <script> p1++; </script>
                  </xsl:when>
                     <xsl:when test="$priority = 2">
                    <script> p2++; </script>
                  </xsl:when>
                  <xsl:when test="$priority = 3">
                    <script> p3++; </script>
                  </xsl:when>
                  <xsl:when test="$priority = 4">
                    <script> p4++; </script>
                  </xsl:when>
                  <xsl:when test="$priority = 5">
                    <script> p5++; </script>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text></xsl:text>
                  </xsl:otherwise>
                </xsl:choose>

                <xsl:variable name="ruleset" select="@ruleset" />
                <xsl:choose>
                  <xsl:when test="$ruleset = 'Best Practices'">
                    <script> bp++; </script>
                  </xsl:when>
                  <xsl:when test="$ruleset = 'Code Style'">
                    <script> codeStyle++; </script>
                  </xsl:when>
                  <xsl:when test="$ruleset = 'Design'">
                    <script> design++; </script>
                  </xsl:when>
                  <xsl:when test="$ruleset = 'Documentation'">
                    <script> doc++; </script>
                  </xsl:when>
                  <xsl:when test="$ruleset = 'Error Prone'">
                    <script> ep++; </script>
                  </xsl:when>
                  <xsl:when test="$ruleset = 'Performance'">
                    <script> perf++; </script>
                  </xsl:when>
                  <xsl:when test="$ruleset = 'Security'">
                    <script> security++; </script>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text></xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <td><xsl:value-of select="@ruleset"/></td>

                <td> <a href="{@externalInfoUrl}"><xsl:value-of select="@rule"/></a></td>
              </tr>
            </xsl:for-each>
          </xsl:for-each>
        </table>
        <div id="vizBar"></div>
        <div id="vizArc"></div>
        <div id="vizArcRS"></div>
      </div>
      <script>
$(document).ready( function () { $('#pmdTable').DataTable( {dom: 'Blfrtip',
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ] }); 
   console.log(total, p1,p2,p3, p4,p5);

const summaryEle = document.getElementById('summary');
summaryEle.innerHTML = `
  <table class="table table-bordered table-hover table-striped">
    <tr><th>Total</th><th>P1</th><th>P2</th><th>P3</th><th>P4</th><th>P5</th></tr>
    <tr><td><b>${total}</b></td><td>${p1}</td><td>${p2}</td><td>${p3}</td><td>${p4}</td><td>${p5}</td></tr>
  </table>`;
// charting
const arcSpec ={
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "description": "PMD violations",
  "data": {
    "values": [
      {"priority": 1, "count": p1},
      {"priority": 2, "count": p2},
      {"priority": 3, "count": p3},
      {"priority": 4, "count": p4},
      {"priority": 5, "count": p5}
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
      {"category": "Best Practices", "count": bp},
      {"category": "Code Style", "count": codeStyle},
      {"category": "Design", "count": design},
      {"category": "Documentation", "count": doc},
      {"category": "Error Prone", "count": ep},
      {"category": "Performance", "count": perf},
      {"category": "Security", "count": security}
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
      {"priority": 1, "count": p1},
      {"priority": 2, "count": p2},
      {"priority": 3, "count": p3},
      {"priority": 4, "count": p4},
      {"priority": 5, "count": p5}
    ]
    },
  "mark": { type: "bar","tooltip": true},
  "encoding": {
    "x": {"field": "priority", "type": "nominal", "axis": {"labelAngle": 0}},
    "y": {"field": "count", "type": "quantitative"}
  }
}

 vegaEmbed('#vizBar', barSpec);

 } );

    </script>
    </body>
  </html>
</xsl:template>
</xsl:stylesheet>
