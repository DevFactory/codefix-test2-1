<html>
<head>
  <meta charset="UTF-8"/>
  <style>
   	  .rTable {
          display: table;
          width: 80%;
      }
      .rTableRow {
          display: table-row;
      }
      .rTableHeading {
          display: table-header-group;
          background-color: #ddd;
      }
      .rTableCell, .rTableHead {
          display: table-cell;
          padding: 3px 10px;
          border: 1px solid #999999;
      }
      .rTableHeading {
          display: table-header-group;
          background-color: #ddd;
          font-weight: bold;
      }
      .rTableFoot {
          display: table-footer-group;
          font-weight: bold;
          background-color: #ddd;
      }
      .rTableBody {
          display: table-row-group;
      }
  </style>
</head>
<body>

<p>Your repositories have been analyzed, please visit CodeFix to prioritize work <a href="${frontEndUrl}">[link]</a></p>

<div class="rTable">
  <div class="rTableRow">
    <div class="rTableHead"><strong>Repository Url</strong></div>
    <div class="rTableHead"><strong>Number of Insights</strong></div>
    <div class="rTableHead"><strong>Overal Severity</strong></div>
  </div>
  <div class="rTableRow">
    <div class="rTableCell">${repositoryUrl}</div>
    <div class="rTableCell">${numberOfInsights}</div>
    <div class="rTableCell">${overallSeverity}</div>
  </div>
</div>
</body>
</html>
