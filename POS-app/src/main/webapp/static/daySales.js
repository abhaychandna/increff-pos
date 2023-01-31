
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

function setDates(startDate, endDate){
	console.log('Adding timezone offset before converting to ISO String')
	// adding timezone offsets
	startDate = new Date(startDate.getTime() - startDate.getTimezoneOffset() * 60000);
	endDate = new Date(endDate.getTime() - endDate.getTimezoneOffset() * 60000);	
	console.log("Initialized date inputs before ISO COnversion" + startDate + " " + endDate);

	// conversion and setting values to yyyy-mm-dd
	startDate = startDate.toISOString().slice(0,10);
	endDate = endDate.toISOString().slice(0,10);
	$('#startDate').val(startDate);
	$('#endDate').val(endDate);
}
function initializeDateInputs(){
	var endDate = new Date();
	var startDate = new Date(endDate.getFullYear(), endDate.getMonth(), 1);
	setDates(startDate, endDate);
	return {startDate: startDate, endDate: endDate};
}

function init(){
	var dates = initializeDateInputs();
	
	startDate = dates.startDate.toISOString();
	endDate = dates.endDate.toISOString();

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
				dt_params = { startDate: startDate, endDate: endDate};
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
			{ "data": "totalRevenue",
			"render": function (data) {
				return truncateFloat(data, 2);
			 }
			},
        ]
	});

}

$(document).ready(init);

