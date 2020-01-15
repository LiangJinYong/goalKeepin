var noIndex = $('.table thead th').index($('.table thead th:contains("NO")'));
			
var pageIndex = $('#pagination li').index($('#pagination li.active'));

$('.table tbody tr td:nth-child(' + (noIndex + 1) + ')').each(function(index, item) {
	
	$(item).text(pageIndex * 10 + (index+1));
});

$('.table').css('border', '1px solid #ccc');
