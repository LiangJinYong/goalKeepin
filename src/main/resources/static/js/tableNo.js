if(totalRecordNum > 0) {
	var noIndex = $('.table thead th').index($('.table thead th:contains("NO")'));
	
	var pageNum = parseInt($('#pagination li.active a').text());
	
	$('.table tbody tr td:nth-child(' + (noIndex + 1) + ')').each(function(index, item) {
		
		$(item).text((pageNum - 1) * pageSize + (index+1));
	});
}

$('.table').css('border', '1px solid #ccc');
