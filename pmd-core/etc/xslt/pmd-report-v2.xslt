<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:scan="http://pmd.sourceforge.net/report/2.0.0"
  >

<xsl:template match="scan:pmd">
  <xsl:variable name="version" select="@version" />
  <xsl:variable name="timestamp" select="@timestamp" />
  <xsl:variable name="count">0</xsl:variable>
  <xsl:variable name="total">0</xsl:variable>


  <html>
   <head>
   <title>PMD Report </title>
   <link rel='stylesheet' type='text/css' href='https://cdn.datatables.net/1.12.1/css/jquery.dataTables.css'/>
   <link rel='stylesheet' type='text/css' href='https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css'/>

   <script src='https://code.jquery.com/jquery-3.6.1.min.js'></script> 
   <script type='text/javascript' charset='utf8' src='https://cdn.datatables.net/1.12.1/js/jquery.dataTables.js'></script>

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
    <h5>Problems found ::  <small> Version: <xsl:value-of select="$version"/> @ <xsl:value-of select="$timestamp"/></small> </h5>
    <table id='pmdTable' class='table table-hover table-striped'>
    <thead>
 
      <tr>
        <th>File #</th>
        <th>Issue #</th>
        <th>File</th>
        <th>Line</th>
        <th>Priority</th>
        <th>Rule</th>
        <th>Problem</th>

   
      </tr>
      </thead>
      <xsl:for-each select="scan:file">
      <xsl:variable name="filename" select="@name" />
      <xsl:variable name="slno" select="position()" />
         <xsl:for-each select="scan:violation">
          <xsl:variable name="pos" select="position()" />
           <xsl:variable name="currPos" select="position()" />
          <tr>
          <td><xsl:value-of select="$slno"/></td>
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

          <td> <a href="{@externalInfoUrl}">Details</a></td>
          </tr>
          </xsl:for-each>
      </xsl:for-each>
    </table>
     <div id="vizBar"></div>
     <div id="vizArc"></div>
     <div id="vizArcRS"></div>

   </div>
    <script>
$(document).ready( function () { $('#pmdTable').DataTable(); 
   console.log(total, p1,p2,p3, p4);

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
      {"rule": "Best Practices", "count": bp},
      {"rule": "Code Style", "count":codeStyle },
      {"rule": "Design", "count": design},
      {"rule": "Documentation", "count": doc},

      {"rule": "Documentation", "count": ep},
      {"rule": "Error Prone", "count": perf},
      {"rule": "Security", "count": security}

    ]
  },
  "mark": {"type": "arc" , "innerRadius": 50, "tooltip": true},

  "encoding": {
    "theta": {"field": "count", "type": "quantitative"},
    "color": {"field": "rule", "type": "nominal"}
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
