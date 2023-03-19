<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<!--
   PMD CPD (Copy and Paste Detector) XML to HTML transformer 
-->
<xsl:output method="html" />

<xsl:template match="/">

<html>
<head>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous"></link>
  <script src="https://code.jquery.com/jquery-3.6.4.min.js" integrity="sha256-oP6HI9z1XaZNBrJURtCoUT5SUnxFr8s3BzRl+cbzUq8=" crossorigin="anonymous"></script>
  <link rel="stylesheet" type="text/css"
      href="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.12.1/b-2.2.3/b-colvis-2.2.3/b-html5-2.2.3/b-print-2.2.3/datatables.min.css" />

  <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
  <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
  <script type="text/javascript"
      src="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.12.1/b-2.2.3/b-colvis-2.2.3/b-html5-2.2.3/b-print-2.2.3/datatables.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/7.6.1/d3.min.js"
      integrity="sha512-MefNfAGJ/pEy89xLOFs3V6pYPs6AmUhXJrRlydI/9wZuGrqxmrdQ80zKHUcyadAcpH67teDZcBeS6oMJLPtTqw=="
      crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    
</head>
<body style='min-height: 75rem; padding-top: 3.5rem;'>
  <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
      <a class="navbar-brand" href="#">PMD - CPD (Copy and Paste Detector)</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
        aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item active">
            <a class="nav-link" href="#">Home</a>
          </li>
        </ul>
      </div>
  </nav>


    <div class='container'>
      <h4>Summary of duplicated code</h4>
      <p>This page summarizes the code fragments that have been found to be replicated in the code.</p>
    

      <table style="width:640px" id='data_table0' class="table table-light table-bordered table-striped table-hover">
        <tr>
          <th># Duplications</th>
          <th>Total lines</th>
          <th>Total tokens</th>
          <th>Approximate number of bytes</th>
        </tr>
        <tr>
          <td class="SummaryNumber"><xsl:value-of select="count(//duplication)"/></td>
          <td class="SummaryNumber"><xsl:value-of select="sum(//duplication/@lines)"/></td>
          <td class="SummaryNumber"><xsl:value-of select="sum(//duplication/@tokens)"/></td>
          <td class="SummaryNumber"><xsl:value-of select="sum(//duplication/@tokens) * 4"/></td>
        </tr>
      </table>
      <hr/>
      <a class='btn btn-info' href='https://pmd.github.io/latest/pmd_userdocs_cpd.html#refactoring-duplicates'>About Refactoring Duplicates</a>
      <h4>Details of duplicated code</h4>

      <table style="width:640px" id='data_table' class="table table-light table-bordered table-striped table-hover">
          <thead>
            <tr>
              <th>lines</th>
              <th>tokens</th>
              <th>files</th>
              <th>codefragment</th>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="pmd-cpd/duplication" />
          </tbody>
      </table>
    </div>


<script>
  let params = (new URL(document.location)).searchParams;
  let showDatatable = false;

  //------------ can be called with this parameter d 
  if (params.get('d') !== null) { // got it via query param d
      showDatatable = true;
  }
  
  if (showDatatable)  {
    $(document).ready( function () {
        $('#data_table').DataTable({
          dom: 'Blfrtip',
          buttons: [
              'copy', 'csv', 'excel', 'pdf', 'print'
          ]
        }
        );
    } );
  }
  
</script>

</body>
</html>
</xsl:template>

<!-- templates -->
<xsl:template match="pmd-cpd/duplication">
  <xsl:for-each select=".">
      <tr>
        <td><xsl:value-of select="@lines"/></td>
        <td><xsl:value-of select="@tokens"/></td>
        <td>
          <table style="width:640px" id='data_table2' class="table table-light table-bordered table-striped table-hover">
            <tr><th>column</th><th>endcolumn</th><th>line</th><th>endline</th><th>path</th></tr>
            <xsl:for-each select="file">
              <tr>
                <td><xsl:value-of select="@column"/></td>
                <td><xsl:value-of select="@endcolumn"/></td>
                <td><xsl:value-of select="@line"/></td>
                <td><xsl:value-of select="@endline"/></td>
                <td><xsl:value-of select="@path"/></td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
        <td><pre><xsl:value-of select="codefragment"/></pre></td>
      </tr>
  </xsl:for-each>
</xsl:template>
  
</xsl:stylesheet>
