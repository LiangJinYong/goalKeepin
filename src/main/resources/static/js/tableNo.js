if(totalRecordNum > 0) {
	var noIndex = $('.table thead th').index($('.table thead th:contains("NO")'));
	
	var pageNum = parseInt($('#pagination li.active a').text());
	
	$('.table tbody tr td:nth-child(' + (noIndex + 1) + ')').each(function(index, item) {
		
		$(item).text((pageNum - 1) * pageSize + (index+1));
	});
}

$('.table').css('border', '1px solid #ccc');

function addSortLink(dataUrl) {
	var index = dataUrl.indexOf('?');
	
	var paramLink = '?';
	if(index != -1) {
		paramLink = '&';
	}
	if(totalRecordNum > 1) {
		$('.table .sortable').each(function() {
			$(this).css({cursor: 'pointer'});
			var sortLink = $('<a>').css({'text-decoration': 'none'});
			sortLink.attr('href', dataUrl + paramLink + 'sort=' + $(this).attr('id') + ',asc')
			var sortSymbol;
			if(sortField == null || sortField != $(this).attr('id')) {
			//	sortSymbol = $('<span>&#11109;</span>').css({'marginLeft': '10px'});
			} else {
				if(sortOrder == 'asc') {
				//	sortSymbol = $('<span style="color: green;">&#11014;</span>').css({'marginLeft': '10px'});
					sortLink.attr('href', dataUrl + paramLink + 'sort=' + $(this).attr('id') + ',desc')
				} else {
				//	sortSymbol = $('<span style="color: red;">&#11015;</span>').css({'marginLeft': '10px'});
				}
			}
			//$(this).append(sortSymbol);
			$(this).contents().detach().appendTo(sortLink);
			sortLink.appendTo($(this));
		});
		
		if(sortField != null) {
			$('#pagination a[href]').each(function() {
				var href = $(this).attr('href');
				var newHref = href + paramLink + 'sort=' + sortField + ',' + sortOrder;
				$(this).attr('href', newHref);
			});
		}
	}
}