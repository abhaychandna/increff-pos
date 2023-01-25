
function getDaySalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/daySales";
}
function getDaySalesFilterUrl(){
	return getDaySalesUrl() + "/filter";
}

function getDaySalesList(){
	$('#day-sales-table').data('dt_params', { startDate: "", endDate: "" });
	$('#day-sales-table').DataTable().draw();
}

function stringToISOString(dateString){
	var date = new Date(dateString);
	//date = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
	console.log(date);

	var isoString = date.toISOString();
	console.log("ISO String " + isoString)
	return isoString;
}

function filterDaySalesList(){
	// get start date and end date
	var startDate = $('#startDate').val();
	var endDate = $('#endDate').val();
	if(startDate == "" || endDate == ""){
		alert("Please select start date and end date");
		return;
	}
	startDate = stringToISOString(startDate);
	endDate = stringToISOString(endDate);
	console.log('sd' + startDate);
	console.log('ed'+ endDate);
	$('#daySales-table').data('dt_params', { startDate: startDate, endDate: endDate });
    $('#daySales-table').DataTable().draw();

	/*
	// ajax call to get data
	$.ajax({
		url: getDaySalesUrl(),
		type: 'GET',
		data: {startDate: startDate, endDate: endDate},
		success: function(data) {
			console.log(data);
		}
	});
	*/
	
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
		"ajax": {
			url : getDaySalesUrl(),
			dataType: 'json',
			cache:false,
			type: 'GET',
			data: function ( d ) {	
				var dt_params = $('#daySales-table').data('dt_params');
				if(dt_params){ $.extend(d, dt_params); }
			}},
		"columns": [
            { "data": "date",
            "render": function (timestamp) {
                var millisecondTimestamp = timestamp * 1000;
                var date = new Date(millisecondTimestamp).toLocaleString();
				// show only date
				date = date.split(',')[0];
				return date;
            } },
			{ "data": "invoicedItemsCount" },
            { "data": "invoicedOrdersCount" },
			{ "data": "totalRevenue" }
        ]
	});

}

$(document).ready(init);

