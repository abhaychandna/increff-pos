
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
	
	

	var isoString = date.toISOString();
	
	return isoString;
}

function filterDaySalesList(){
	var startDate = $('#startDate').val();
	var endDate = $('#endDate').val();
	if(startDate == "" || endDate == ""){
		var errorString = "Please select both start date and end date";
		raiseAlert({
            icon: 'error',
            title: 'Oops...',
            html: errorString,
        })
		return;
	}
	startDate = stringToISOString(startDate);
	endDate = stringToISOString(endDate);
	
	
	$('#daySales-table').data('dt_params', { startDate: startDate, endDate: endDate });
    $('#daySales-table').DataTable().ajax.reload();	
}

function initializeDateInputs(){
	var endDate = new Date();
	var startDate = new Date(endDate.getFullYear(), endDate.getMonth(), 1);
	
	
	startDate = increaseTimeByTimeZoneOffset(startDate);
	endDate = increaseTimeByTimeZoneOffset(endDate);
	
	startDate = startDate.toISOString().slice(0,10);
	endDate = endDate.toISOString().slice(0,10);

	$('#startDate').val(startDate);
	$('#endDate').val(endDate);
	return {startDate: startDate, endDate: endDate};
}

function init(){
	$('#refresh-data').click(getDaySalesList);
	$('#filter').click(filterDaySalesList)

	$('#daySales-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
        "ordering": false,
		"lengthMenu": [5, 10, 25, 50, 100],
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
				
				date = date.split(',')[0];
				return date;
            } },
			{ "data": "invoicedItemsCount" },
            { "data": "invoicedOrdersCount" },
			{ "data": "totalRevenue",
			"render": function (data) {
				return truncateFloat(data, 2);
			 }
			},
        ]
	});

	initializeDateInputs();
	filterDaySalesList();
}

$(document).ready(init);

