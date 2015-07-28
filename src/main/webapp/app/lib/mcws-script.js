function loadReportForm() 
{
  //$('#gradingData').load('app/coder/views/gradingsheet.html');
  
  $.get('app/coder/views/gradingsheet.html', function(data)
  {
    $('#gradingData').html($(data).html());
  });
}