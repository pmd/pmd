-- https://github.com/Thomas-S-B/SQLServerTools/blob/master/MailJobTimeLine.sql


--###################################################################################################
-- This SQL sends an email with all Jobs as an graphical timeline
--
-- You can configure this in the section "Configuration"
--
-- History:
-- 2017-07-28 Initial version
--###################################################################################################

 
SET NOCOUNT ON
 

DECLARE @StartDate DATETIME
DECLARE @EndDate DATETIME
DECLARE @MinimalJobDurationInSeconds  INT
DECLARE @EmailTo VARCHAR(500)
DECLARE @DaysPast INT
DECLARE @HoursPast INT
DECLARE @Servername VARCHAR(50)
 

--###################################################################################################
--
-- Configuration Start
--
--###################################################################################################
--Wer soll alles eine Mail bekomen?
SET @EmailTo = 'John.Doe@xyz.com;Jennifer.Doe@xyz.com'
 
--How many days in the past should be displayed?
SET @DaysPast = 1

--How many hours in the past should be displayed?
SET @HoursPast = 0

--How long must a job taken to displayed?
SET @MinimalJobDurationInSeconds  = 0
--###################################################################################################
--
-- Configuration End
--
--###################################################################################################



--###################################################################################################
--
-- Calculate timespan
--
--###################################################################################################
SET @StartDate = DateAdd(hh, -(@HoursPast), GETDATE() - @DaysPast)
SET @EndDate = GETDATE()
 

--###################################################################################################
--
-- Textliterals
--
--###################################################################################################
DECLARE @TEXT_SUCCESS VARCHAR(20)
SET @TEXT_SUCCESS = 'Successful'

 

--###################################################################################################
--
-- Colors
--
--###################################################################################################
DECLARE @COLOR_ERROR VARCHAR(10)
SET @COLOR_ERROR = '#FF4136'
 
DECLARE @COLOR_SUCCESS VARCHAR(10)
SET @COLOR_SUCCESS = '#2ECC40'

DECLARE @COLOR_RETRY VARCHAR(10)
SET @COLOR_RETRY = '#FFDC00'

DECLARE @COLOR_ABORTED VARCHAR(10)
SET @COLOR_ABORTED = '#AAAAAA'

DECLARE @COLOR_UNDEFINED VARCHAR(10)
SET @COLOR_UNDEFINED = '#111111'

DECLARE @COLOR_RUNNING VARCHAR(10)
SET @COLOR_RUNNING = '#7FDBFF'

DECLARE @COLOR_SERVERSTART VARCHAR(10)
SET @COLOR_SERVERSTART = '#FF851B'

 

--###################################################################################################
--
-- Cleanup, perhaps there are temptables from other executions
--
--###################################################################################################
IF OBJECT_ID('tempdb..#JobExecutionTimes') IS NOT NULL
   DROP TABLE #JobExecutionTimes;

IF OBJECT_ID('tempdb..##TimelineGraph') IS NOT NULL
   DROP TABLE ##TimelineGraph;

 

--###################################################################################################
--
-- Create table which holds the generated HTML
--
--###################################################################################################
CREATE TABLE ##TimelineGraph
   (ID INT IDENTITY(1, 1)
           NOT NULL
   ,HTML VARCHAR(8000) NULL                       --8000, so it works with SQL-Server < 2008R2
   )

 

--###################################################################################################
--
-- Create table of the jobs
--
--###################################################################################################
SELECT   JOBDATA.*
INTO     #JobExecutionTimes
FROM     (
--Jobs, which are currently not running
          SELECT  JOB.name AS JobName
                 ,CAT.name AS CatName
                 ,CONVERT(DATETIME, CONVERT(CHAR(8), HIS.run_date, 112) + ' '
                  + STUFF(STUFF(RIGHT('000000'
                                      + CONVERT(VARCHAR(8), HIS.run_time), 6),
                                5, 0, ':'), 3, 0, ':'), 120) AS SDT
                 ,DATEADD(s,
                          ((HIS.run_duration / 10000) % 100 * 3600)
                          + ((HIS.run_duration / 100) % 100 * 60)
                          + HIS.run_duration % 100,
                          CONVERT(DATETIME, CONVERT(CHAR(8), HIS.run_date, 112)
                          + ' ' + STUFF(STUFF(RIGHT('000000'
                                                    + CONVERT(VARCHAR(8), HIS.run_time),
                                                    6), 5, 0, ':'), 3, 0, ':'), 120)) AS EDT
                 ,JOB.description
                 ,HIS.run_status
                 ,CASE WHEN HIS.run_status = 0 THEN @COLOR_ERROR       
                       WHEN HIS.run_status = 1 THEN @COLOR_SUCCESS
                       WHEN HIS.run_status = 2 THEN @COLOR_RETRY
                       WHEN HIS.run_status = 3 THEN @COLOR_ABORTED
                       ELSE @COLOR_UNDEFINED
                  END AS JobStatus
                 ,CASE WHEN HIS.run_status = 0 THEN HIS.message   -- 0 = Error (red)
                       WHEN HIS.run_status = 1 THEN @TEXT_SUCCESS -- 1 = Successful (green)
                       WHEN HIS.run_status = 2 THEN HIS.message   -- 2 = New try (yellow)
                       WHEN HIS.run_status = 3 THEN HIS.message   -- 3 = Aborted (gray)
                       ELSE HIS.message                           -- undefined status (black)
                  END AS JobMessage

          FROM    msdb.dbo.sysjobs AS JOB
          LEFT JOIN msdb.dbo.sysjobhistory AS HIS ON HIS.job_id = job.job_id
          INNER JOIN msdb.dbo.syscategories AS CAT ON CAT.category_id = JOB.category_id
          WHERE   CONVERT(DATETIME, CONVERT(CHAR(8), HIS.run_date, 112) + ' '
                  + STUFF(STUFF(RIGHT('000000'
                                      + CONVERT(VARCHAR(8), HIS.run_time), 6),
                                5, 0, ':'), 3, 0, ':'), 120) BETWEEN @StartDate
                                                             AND
                                                              @EndDate
                  AND HIS.step_id = 0 -- step_id = 0 is the job, step_id > 0 are the subjobs of this job
                  AND ((HIS.run_duration / 10000) % 100 * 3600)
                  + ((HIS.run_duration / 100) % 100 * 60) + HIS.run_duration
                  % 100 >= @MinimalJobDurationInSeconds 

          UNION ALL

          --Jobs currently running
          SELECT  JOB.name AS JobName
                 ,CAT.name AS CatName
                 ,JA.start_execution_date AS SDT
                 ,GETDATE() AS EDT
                 ,JOB.description
                 ,HIS.run_status
                 ,CASE WHEN HIS.run_status = 0 THEN @COLOR_ERROR
                       WHEN HIS.run_status = 1 THEN @COLOR_SUCCESS
                       WHEN HIS.run_status = 2 THEN @COLOR_RETRY
                       WHEN HIS.run_status = 3 THEN @COLOR_ABORTED
                       WHEN HIS.run_status IS NULL THEN @COLOR_RUNNING
                       ELSE @COLOR_UNDEFINED
                  END AS JobStatus
                 ,CASE WHEN HIS.run_status = 0 THEN HIS.message         -- 0 = Error (red)
                       WHEN HIS.run_status = 1 THEN @TEXT_SUCCESS       -- 1 = Successful (green)
                       WHEN HIS.run_status = 2 THEN HIS.message         -- 2 = New try (yellow)
                       WHEN HIS.run_status = 3 THEN HIS.message         -- 3 = Aborted (gray)
                       WHEN HIS.run_status IS NULL THEN 'Running currently'
                       ELSE HIS.message                                 -- undefined status (black)
                  END AS JobMessage

          FROM    msdb.dbo.sysjobactivity AS JA
          LEFT JOIN msdb.dbo.sysjobhistory AS HIS ON HIS.instance_id = JA.job_history_id
          JOIN    msdb.dbo.sysjobs AS JOB ON JOB.job_id = JA.job_id
          JOIN    msdb.dbo.sysjobsteps AS JS ON JS.job_id = JA.job_id
                                             AND ISNULL(JA.last_executed_step_id,
                                                        0) + 1 = JS.step_id
          LEFT JOIN msdb.dbo.syscategories AS CAT ON CAT.category_id = JOB.category_id
          WHERE   JA.session_id = (SELECT TOP 1
                                          session_id
                                   FROM   msdb.dbo.syssessions
                                   ORDER BY agent_start_date DESC
                                  )
                  AND JA.start_execution_date IS NOT NULL
                  AND JA.stop_execution_date IS NULL
         ) AS JOBDATA

ORDER BY JOBDATA.JobName

IF NOT EXISTS ( SELECT  1
                FROM    #JobExecutionTimes )
   GOTO NothingToDo

 

--###################################################################################################
--
-- Create errormessage
--
--###################################################################################################
DECLARE @ERROR_COUNT AS INTEGER
DECLARE @ERROR_TEXT AS VARCHAR(50)
SET @ERROR_COUNT = (SELECT COUNT(*) FROM #JobExecutionTimes WHERE run_status=0)
IF @ERROR_COUNT > 0
   SET @ERROR_TEXT = 'There are ' + CONVERT(varchar(4), @ERROR_COUNT) +' faulty jobs.'
ELSE
   SET @ERROR_TEXT = ''

 

--###################################################################################################
--
-- Html Timeline - Head
--
--###################################################################################################
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '<html>
            <head>
                        <style>
                           .google-visualization-tooltip {
                                      width: 400px !important;
                                      height: 200px !important;
                                      border-radius: 8px !important;
                                      border: 2px solid rgb(1, 1, 1) !important;
                                      background-color: rgb(50, 50, 50) !important;
                                      color: rgb(230, 230, 230) !important;
                                      font-size: 14px !important;
                                      font-family: Helvetica !important;
                                      box-shadow: 7px 5px 7px 0px rgba(50, 50, 50, 0.75) !important;
                                      padding: 6px 6px 6px 6px !important;
                                      opacity: 0.85 !important;                       }
'

INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '
                        #legend ul, li {
                                   margin: 0px;
                                   list-style: none;
                                   display: inline-block;
                                   font-size: 14px;
                                   font-family: Helvetica;
                                   padding: 0px 0px 5px 0px;
                        }

                        .rectangleBasis,
                        .legendSuccess,
                        .legendRunning,
                        .legendError,
                        .legendRetry,
                        .legendAborted,
                        .legendServerstart,
                        .legendUndefined {
                                   float: left;
                                   width: 30px;
                                   height: 15px;
                                   margin: 0px 3px 0px 15px;
                        }

                        .legendSuccess {
                                   background: '+ @COLOR_SUCCESS + ';
                        }

                        .legendRunning {
                                   background: '+ @COLOR_RUNNING + ';
                        }

                        .legendError {
                                   background: '+ @COLOR_ERROR + ';
                        }

                        .legendRetry {
                                   background: '+ @COLOR_RETRY + ';
                        }

                        .legendAborted {
                                   background: '+ @COLOR_ABORTED + ';
                        }

                        .legendServerstart {
                                   background: '+ @COLOR_SERVERSTART + ';
                        }

                        .legendUndefined {
                                   background: '+ @COLOR_UNDEFINED + ';
                        }

'


INSERT   INTO ##TimelineGraph
         (HTML)
SELECT   '</style>'


INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '<!--<META HTTP-EQUIV="refresh" CONTENT="3">-->
            <script type="text/javascript" src="https://www.google.com/jsapi?autoload={''modules'':[{''name'':''visualization'', ''version'':''1'',''packages'':[''timeline'']}]}"></script>'


INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '    <script type="text/javascript">
            google.setOnLoadCallback(drawChart);
            function drawChart() {'


INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '       var container = document.getElementById(''JobTimeline'');
            var chart = new google.visualization.Timeline(container);
            var dataTable = google.visualization.arrayToDataTable(['


INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '[''Category'', ''Name'', {role: ''style''}, ''Start'', ''End''],'

 

--###################################################################################################
--
-- Html Timeline - Jobdata
--
--###################################################################################################
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '                   [ ' + '''' + JobName + ''','''', ''' + JobStatus + ''', '
         + 'new Date(' + CAST(DATEPART(YEAR, SDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MONTH, SDT) - 1 AS VARCHAR(4))
         + ', ' + CAST(DATEPART(DAY, SDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(HOUR, SDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MINUTE, SDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(SECOND, SDT) AS VARCHAR(4)) + '), ' + 'new Date('
         + CAST(DATEPART(YEAR, EDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MONTH, EDT) - 1 AS VARCHAR(4))
         + ', ' + CAST(DATEPART(DAY, EDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(HOUR, EDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MINUTE, EDT) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(SECOND, EDT) AS VARCHAR(4)) + ') ],'
FROM     #JobExecutionTimes
 

--###################################################################################################
--
-- Html Timeline - Last serverstart
--
--###################################################################################################
DECLARE @DTServerstart AS DATETIME
SET @DTServerstart = (SELECT login_time FROM sys.dm_exec_sessions WHERE session_id=1) --AND login_time >= @StartDate)


INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '                   [ ' + '''' + 'Last serverstart' + ''','''', ''' + @COLOR_SERVERSTART + ''', '
         + 'new Date(' + CAST(DATEPART(YEAR, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MONTH, @DTServerstart) - 1 AS VARCHAR(4))
         + ', ' + CAST(DATEPART(DAY, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(HOUR, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MINUTE, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(SECOND, @DTServerstart) AS VARCHAR(4)) + '), ' + 'new Date('
         + CAST(DATEPART(YEAR, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MONTH, @DTServerstart) - 1 AS VARCHAR(4))
         + ', ' + CAST(DATEPART(DAY, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(HOUR, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(MINUTE, @DTServerstart) AS VARCHAR(4)) + ', '
         + CAST(DATEPART(SECOND, @DTServerstart) AS VARCHAR(4)) + ') ],'
WHERE @DTServerstart >= @StartDate

 
--###################################################################################################
--
-- Html Timeline - Footer
--
--###################################################################################################
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '       ]);
            var options =
            {
                        timeline:           {
                                                           groupByRowLabel: true,
                                                           colorByRowLabel: false,
                                                           singleColor: false,
                                                           rowLabelStyle: {fontName: ''Helvetica'', fontSize: 11 },
                                                           barLabelStyle: {fontName: ''Helvetica'', fontSize: 7 }                                                    
                                                           },
        hAxis: {format: "dd.MM.yyyy - HH:mm"}
            };
            chart.draw(dataTable, options);
'
 

--###################################################################################################
--
-- Html Timeline - Tooltip - Additional jobdata, needed because it's not possible to squezze this
--                           into the google timeline data
--
--###################################################################################################
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   'var dataTableAdditionalData = google.visualization.arrayToDataTable(['
 

INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '[''Addition1''],'
 

--Jobdata
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '[' + '''' + LEFT(REPLACE(COALESCE(JobMessage, ''), '''', ''), 200) + '''],'
FROM     #JobExecutionTimes


--Last serverstart
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '[' + '''' + '' + '''],'
FROM sys.dm_exec_sessions
WHERE session_id =1
AND login_time >= @StartDate


--Dataend
INSERT   INTO ##TimelineGraph
         (HTML)
SELECT   '       ]);'

 
--Javascript tooltip
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '                               google.visualization.events.addListener(chart, ''onmouseover'', function (e) {
                                               setTooltipContent(dataTable, e.row);
                                   });
                                   function setTooltipContent(dataTable, row) {
                                               if (row != null) {
                                                           var content = ''<div class="custom-tooltip" ><h3>'' + dataTable.getValue(row, 0) + ''</h3>'' +
                                                                       ''</div>'' +
                                                                       ''<div>Von '' + formatDate(dataTable.getValue(row, 3)) + '' bis '' + formatDate(dataTable.getValue(row, 4)) + ''</div>'' +
                                                                       ''<br/><div>Dauer: '' + (dateDiff(dataTable.getValue(row, 3), dataTable.getValue(row, 4))) + ''</div>'' +
                                                                       ''<br/><div>'' + (dataTableAdditionalData.getValue(row, 0)) + ''</div>''
                                                                       ;
                                                           var tooltip = document.getElementsByClassName("google-visualization-tooltip")[0];
                                                           tooltip.innerHTML = content;
                                               }
                                   }
                                   '
 

INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '
                                   function formatDate(d) {
                                               return ("0" + d.getDate()).slice(-2) + "." + ("0" + (d.getMonth() + 1)).slice(-2) + "." + d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2) + ":" + ("0" + d.getSeconds()).slice(-2);
                                   }

                                   function dateDiff(dateNow, dateFuture) {
                                               var seconds = Math.floor((dateFuture - (dateNow)) / 1000);
                                               var minutes = Math.floor(seconds / 60);
                                               var hours = Math.floor(minutes / 60);
                                               var days = Math.floor(hours / 24);
                                               hours = hours - (days * 24);
                                               minutes = minutes - (days * 24 * 60) - (hours * 60);
                                               seconds = seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
                                               return ("0" + days).slice(-2) + '':'' +("0" + hours).slice(-2) + '':'' + ("0" + minutes).slice(-2) + '':'' + ("0" + seconds).slice(-2)
                                   }
                                   '

 

--###################################################################################################
--
-- Html Timeline - Footerend
--
--###################################################################################################
INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '}
            </script>
            </head>
            <body>' + '<font face="Helvetica" size="3" ><b>' + @@servername
         + ' Jobs' + ' from ' + CONVERT(VARCHAR(20), @StartDate, 120)
         + ' to ' + CONVERT(VARCHAR(20), @EndDate, 120) +
         + CASE WHEN @ERROR_COUNT = 0 THEN ''
            ELSE
                '. ' + @ERROR_TEXT
           END
         + CASE WHEN @MinimalJobDurationInSeconds  = 0 THEN ''
                ELSE ' (Jobs longer than '
                     + CAST(@MinimalJobDurationInSeconds  AS VARCHAR(10))
                     + ' seconds)'
           END
         + '</b></font>
                        <p/>
'
 

INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '
            <div id="legend">
                        <ul>
                        legend:
                                   <li>
                                               <div class="legendSuccess"></div>= Successful
                                   </li>
                                   <li>
                                               <div class="legendRunning"></div>= Running
                                   </li>
                                   <li>
                                               <div class="legendError"></div>= Faulty
                                   </li>
                                   <li>
                                               <div class="legendRetry"></div>= Retry
                                   </li>
                                   <li>
                                               <div class="legendAborted"></div>= Aborted
                                   </li>
                                   <li>
                                               <div class="legendServerstart"></div>= Last serverstart
                                   '
                                  

INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '       ('
                                       + CONVERT(char(20), @DTServerstart,113)
                      + ')
                                      '
                                  

INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT        
                                   '</li>
                                   <li>
                                               <div class="legendUndefined"></div>= Undefined
                                   </li>
                        </ul>
            </div>
'
 
 

--###################################################################################################
--
-- Html Timeline - End
--
--###################################################################################################
DECLARE @Timeline_Width AS INTEGER
IF @DaysPast < 1
   SET @Timeline_Width = 1800
ELSE
   SET @Timeline_Width = @DaysPast * 1800


INSERT   INTO ##TimelineGraph
         (HTML
         )
SELECT   '
                        <div id="JobTimeline" style="width: ' + CAST(@Timeline_Width AS VARCHAR(10))
         + 'px; height: 950px;"></div>
            </body>
</html>'

 

--###################################################################################################
--
-- Send timeline as an email
--
--###################################################################################################
DECLARE @emailBodyText NVARCHAR(MAX); 
SET @emailBodyText = 'Timeline of all jobs between '
   + CONVERT(VARCHAR(20), @StartDate, 120) + ' to '
   + CONVERT(VARCHAR(20), @EndDate, 120) + ' open the attachement.'

DECLARE @emailSubjectText NVARCHAR(MAX); 
SET @emailSubjectText = @@servername + ' Jons between '
   + CONVERT(VARCHAR(20), @StartDate, 120) + ' and '
   + CONVERT(VARCHAR(20), @EndDate, 120)
   + ' ' + @ERROR_TEXT

DECLARE @emailHTMLFilenameText NVARCHAR(MAX); 
SET @emailHTMLFilenameText = @@servername + ' Jobs between '
   + CONVERT(VARCHAR(20), @StartDate, 120) + ' and '
   + CONVERT(VARCHAR(20), @EndDate, 120) + '.html'
SET @emailHTMLFilenameText = REPLACE(@emailHTMLFilenameText, ':', '_')

DECLARE @email_Importance NVARCHAR(10); 
IF @ERROR_COUNT > 0
   SET @email_Importance = 'High'
ELSE
   SET @email_Importance = 'Normal'

EXECUTE msdb.dbo.sp_send_dbmail @recipients = @EmailTo,
   @subject = @emailSubjectText, @body = @emailBodyText,
   @body_format = 'HTML' -- or TEXT
   , @importance = @email_Importance
   , @sensitivity = 'Normal' --Normal Personal Private Confidential
   , @execute_query_database = 'master', @query_result_header = 0,            --@query_result_header = 0 is important, otherwise "HTML----" comes from the query into the html
   @query = 'set nocount on; SELECT HTML FROM ##TimelineGraph ORDER BY ID',
   @query_result_no_padding = 1
   --,@query_no_truncate= 1
   , @attach_query_result_as_file = 1,
   @query_attachment_filename = @emailHTMLFilenameText
 

GOTO Cleanup
 

--###################################################################################################
--
-- Nothing to do or errorhandling
--
--###################################################################################################

NothingToDo:
PRINT 'Found no jobs (this could be also an error)'

 

--###################################################################################################
--
-- Cleanup
--
--###################################################################################################
Cleanup:

IF OBJECT_ID('tempdb..#JobExecutionTimes') IS NOT NULL
   DROP TABLE #JobExecutionTimes;

IF OBJECT_ID('tempdb..##TimelineGraph') IS NOT NULL
   DROP TABLE ##TimelineGraph;