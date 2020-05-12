var country = $('#country').val();

$('#userSearchText').focus();
loadUserData('%', null);

$('#userSearchText').keyup(function() {
	var sort = null;
	
	var ascHead = $('#tableHead .asc');
	var descHead = $('#tableHead .desc');
	
	if(ascHead.length > 0) {
		sort = $('#tableHead .asc').parent().attr('id') + ',asc';
	} else if(descHead.length > 0) {
		sort = $('#tableHead .desc').parent().attr('id') + ',desc';
	}
	
	loadUserData($(this).val(), sort);
});

function loadUserData(userSearchText, sort) {
	$.get({
		url: '/goalkeepinmanager/user/searchUserListByIdAndNickname/' + country + '/1',
		data: {
			userSearchText: userSearchText,
			sort: sort
		},
		dataType: 'json',
		success: function(data) {
			var pageData = data.page.pageData;
			var pageNum = data.page.pageNum;
			var pageSize = data.page.pageSize;
			
			addSortLink(data);
			
			if(pageData.length > 0) {
				var getUrl = '/goalkeepinmanager/user/searchUserListByIdAndNickname/' + country + '/';
				
				processTableData(pageData, pageNum, pageSize);
				processPagination(data, getUrl, sort);
			} else {
				$('#userData').empty();
				var row = '<tr class="text-center"><td colspan="5">No Data Found</td></tr>';
				$('#userData').append(row);
				$('#pagingArea').empty();
			}
		}, error: function() {
			alert('Error searching users');
		}
	});
}

function addSortLink(data) {
	var sortField = data.sortField;
	var sortOrder = data.sortOrder;
	
	if(data.page.totalRecordNum > 1) {
		$('.table .sortable').each(function() {
			$(this).css({'cursor': 'pointer', 'color': '#3A7FBA'});
			
			var sortSymbol;
			if(sortField == null || sortField != $(this).attr('id')) {
				sortSymbol = $('<span class="originalSort"></span>');
			} else {
				if(sortOrder == 'asc') {
					sortSymbol = $('<span class="asc" style="color: green;"></span>');
				} else {
					sortSymbol = $('<span class="desc" style="color: red;"></span>');
				}
			}
			$(this).find('span').remove();
			$(this).append(sortSymbol);
		});
		
		$('.table .sortable').off('click').click(function(e) {
			if($(this).has('span.asc').length > 0) {
				loadUserData($('#userSearchText').val(), $(this).attr('id') + ',desc');
			} else {
				loadUserData($('#userSearchText').val(), $(this).attr('id') + ',asc');
			}
		});
	}
}

function processTableData(pageData, pageNum, pageSize) {
	$('#userData').empty();
	
	// table data
	for(var i=0; i<pageData.length; i++) {
		var row = '<tr class="text-center">';
		row += '<td>';
		row += (parseInt(pageNum) - 1) * pageSize + (i + 1);
		row += '</td><td>';
		row += pageData[i].userId;
		row += '</td><td>';
		row += pageData[i].nickName;
		row += '</td><td>';
		row += pageData[i].loginTypeCd;
		row += '</td><td>';
		row += pageData[i].userRegDate;
		row += '</td></tr>';
		
		$('#userData').append(row);
	}
}

function processPagination(data, getUrl, sort) {
	$('#pagingArea').empty();
	var currentPageNum = data.page.pageNum;
	var totalPageNum = data.page.totalPageNum;
	
	if(totalPageNum > 0) {
		var pagination = '<ul class="pagination" id="pagination">';
		
		if(totalPageNum > 10 && currentPageNum == 1) {
			pagination += '<li class="page-item"><a class="page-link">&lt;&lt;</a></li>';
		}
		
		if(totalPageNum > 10 && currentPageNum > 1) {
			pagination += '<li class="page-item">';
			pagination += '<a style="cursor: pointer" class="page-link" ';
			pagination += 'id="page-1">'
			pagination += '&lt;&lt;</a></li>';
		}
		
		if(currentPageNum == 1) {
			pagination += '<li class="page-item"><a class="page-link">&lt;</a></li>';
		}
		
		if(currentPageNum > 1) {
			pagination += '<li class="page-item">';
			pagination += '<a style="cursor: pointer" class="page-link" ';
			pagination += 'id="page-' + (currentPageNum - 1) + '">'
			pagination += '&lt;</a></li>';
		}
		
		for(var i=data.page.firstPageNum; i<=data.page.lastPageNum; i++) {
			pagination += '<li class="page-item page-no';
			if(data.page.pageNum == i) {
				pagination += ' active';
			}
			pagination += '">';
			pagination += '<a style="cursor: pointer" class="page-link"';
			pagination += ' id="page-' + i + '"';
			pagination += '>';
			pagination += i;
			pagination += '</a>';
			pagination += '</li>';
		}
		
		if(currentPageNum == totalPageNum) {
			pagination += '<li class="page-item"><a class="page-link">&gt;</a></li>';
		}
		
		if(currentPageNum < totalPageNum) {
			pagination += '<li class="page-item">';
			pagination += '<a style="cursor: pointer" class="page-link" ';
			pagination += 'id="page-' + (currentPageNum + 1) + '">'
			pagination += '&gt;</a></li>';
		}
		
		if(totalPageNum > 10 && currentPageNum == totalPageNum) {
			pagination += '<li class="page-item"><a class="page-link">&gt;&gt;</a></li>';
		}
		
		if(totalPageNum > 10 && currentPageNum < totalPageNum) {
			pagination += '<li class="page-item">';
			pagination += '<a style="cursor: pointer" class="page-link" ';
			pagination += 'id="page-' + totalPageNum + '">'
			pagination += '&gt;&gt;</a></li>';
		}
		
		pagination += '</ul>';
		
		$('#pagingArea').append(pagination);
		
		$('.pagination a[id]').click(function() {
			var id = $(this).attr('id');
			// id: page-xxx
			var pageNum = id.substring(5);
			$.getJSON(getUrl + pageNum,
				{
					userSearchText: $('#userSearchText').val() == '' ? '%' : $('#userSearchText').val(),
					sort: sort
				},
				function(data) {
					addSortLink(data);
					processTableData(data.page.pageData, data.page.pageNum, data.page.pageSize);
					processPagination(data, getUrl, sort);
				}
			);
		});
	}
}
