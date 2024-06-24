<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cpd="https://pmd-code.org/schema/cpd-report"
                exclude-result-prefixes="cpd">
<!--
   PMD CPD (Copy and Paste Detector) XML to HTML transformer 
-->
<xsl:output method="html" doctype-system="about:legacy-compat"/>

<xsl:template match="/">

<html>
<head>
  <meta charset="utf-8"/>

  <!-- Dependencies:
  https://getbootstrap.com/docs/5.3/getting-started/download/
  https://datatables.net/download/ (Styling Bootstrap5 + DataTables + Buttons + Column Visibility + HTML5 Export + JSZip + pdfmake + Print view
  https://releases.jquery.com/
-->

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous"/>
  <script src="https://code.jquery.com/jquery-3.7.0.min.js" integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g=" crossorigin="anonymous"/>

  <link href="https://cdn.datatables.net/v/bs5/jszip-2.5.0/dt-1.13.4/b-2.3.6/b-html5-2.3.6/b-print-2.3.6/datatables.min.css" rel="stylesheet"/>

  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js"/>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.js"/>
  <script src="https://cdn.datatables.net/v/bs5/jszip-2.5.0/dt-1.13.4/b-2.3.6/b-html5-2.3.6/b-print-2.3.6/datatables.min.js"/>


</head>
<body style="padding-top: 3.5rem;">
  <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
      <a class="navbar-brand" href="#">PMD - CPD (Copy and Paste Detector)</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
        aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item">
            <a class="nav-link active" href="#">Home</a>
          </li>
          <li class="nav-item" id="nav_enable_datatable">
            <a class="nav-link" href="?d=">Enable datatable</a>
          </li>
          <li class="nav-item" id="nav_disable_datatable">
            <a class="nav-link" href="?">Disable datatable</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" target="_blank" rel="noopener noreferrer" href="https://docs.pmd-code.org/latest/pmd_userdocs_cpd.html#refactoring-duplicates">About Refactoring Duplicates</a>
          </li>
        </ul>
      </div>
  </nav>


    <div class="container">
      <div class="row">
        <div class="col">
          <h4>Summary of duplicated code</h4>
          <p>This page summarizes the code fragments that have been found to be replicated in the code.</p>

          <table class="table table-light table-bordered table-striped table-hover">
            <tr>
              <th># Duplications</th>
              <th>Total lines</th>
              <th>Total tokens</th>
              <th>Approximate number of bytes</th>
            </tr>
            <tr>
              <td class="SummaryNumber"><xsl:value-of select="count(//cpd:duplication)"/></td>
              <td class="SummaryNumber"><xsl:value-of select="sum(//cpd:duplication/@lines)"/></td>
              <td class="SummaryNumber"><xsl:value-of select="sum(//cpd:duplication/@tokens)"/></td>
              <td class="SummaryNumber"><xsl:value-of select="sum(//cpd:duplication/@tokens) * 4"/></td>
            </tr>
          </table>
        </div>
      </div>

      <div class="row">
        <div class="col">
          <h4>Details of duplicated code</h4>
        </div>
      <table style="width:100%" id="data_table" class="table table-light table-bordered table-striped table-hover">
          <thead>
            <tr>
              <th>lines</th>
              <th>tokens</th>
              <th>files</th>
              <th>codefragment</th>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="cpd:pmd-cpd/cpd:duplication" />
          </tbody>
      </table>
    </div>
  </div>


<script>
  let params = (new URL(document.location)).searchParams;
  let showDatatable = false;

  //------------ can be called with this parameter d
  if (params.get('d') !== null) { // got it via query param d
      showDatatable = true;
  }

  if (showDatatable)  {
    $("#nav_disable_datatable").show();
    $("#nav_enable_datatable").hide();
    $(document).ready( function () {
        $('#data_table').DataTable({
          dom: "&lt;'row'&lt;'col-sm-12 col-md-4'B>&lt;'col-sm-12 col-md-4'l>&lt;'col-sm-12 col-md-4'f>>" +
               "&lt;'row'&lt;'col-sm-12'tr>>" +
               "&lt;'row'&lt;'col-sm-12 col-md-5'i>&lt;'col-sm-12 col-md-7'p>>",
          buttons: [
              'copy', 'csv', 'excel', 'pdf', 'print'
          ]
        }
        );
    } );
  } else {
    $("#nav_disable_datatable").hide();
    $("#nav_enable_datatable").show();
  }

</script>

</body>
</html>
</xsl:template>

<!-- templates -->
<xsl:template match="cpd:pmd-cpd/cpd:duplication">
  <xsl:for-each select=".">
      <tr>
        <td><xsl:value-of select="@lines"/></td>
        <td><xsl:value-of select="@tokens"/></td>
        <td>
          <table class="table table-light table-bordered table-striped table-hover">
            <tr><th>column</th><th>endcolumn</th><th>line</th><th>endline</th><th>path</th></tr>
            <xsl:for-each select="cpd:file">
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
        <td><pre><xsl:value-of select="cpd:codefragment"/></pre></td>
      </tr>
  </xsl:for-each>
</xsl:template>
  
</xsl:stylesheet>
