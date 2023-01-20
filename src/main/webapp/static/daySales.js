
function getDaySalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/daySaless";
}

function getDaySalesList(){
	$("#daySales-table").DataTable().ajax.reload();
}

function filterDaySalesList(){
	// get start date and end date
	var startDate = $('#startDate').val();
	var endDate = $('#endDate').val();
	console.log(startDate);
	console.log(endDate);
	// ajax call to get data
	$.ajax({
		url: getDaySalesUrl(),
		type: 'GET',
		data: {startDate: startDate, endDate: endDate},
		success: function(data) {
			console.log(data);
		}
	});
	
}

function init(){
	$('#refresh-data').click(getDaySalesList);
	$('#filter').click(filterDaySalesList)

	$('#daySales-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
        "ordering": false,
		"lengthMenu": [2,5,10,20, 40, 60, 80, 100],
		"pageLength":10,
		"ajax": {url : getDaySalesUrl()},
		"columns": [
            { "data": "date",
            "render": function (timestamp) {
                var millisecondTimestamp = timestamp * 1000;
                return  new Date(millisecondTimestamp).toLocaleString();
            } },
			{ "data": "invoicedItemsCount" },
            { "data": "invoicedOrdersCount" },
			{ "data": "totalRevenue" }
        ]
	});

}

$(document).ready(init);

