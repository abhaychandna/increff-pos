function getReportUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/reports";
}

function brandReport() {
    var url = getReportUrl() + "/brand";
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            
            downloadPDF(data, "BrandReport");
        },
        error: function (response) {
            handleAjaxError(response);
        }
    });
}

function inventoryReport(){
    var url = getReportUrl() + "/inventory";
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            
            downloadPDF(data, "InventoryReport");
        },
        error: function (response) {
            handleAjaxError(response);
        }
    });
}

function salesReport(){
    var url = getReportUrl() + "/sales";
    var $form = $("#sales-report-form");

	var json = toJson($form);

    // Remove filters with empty values
    for (var key in json) {
        if (json[key] === "") {
            delete json[key];
            continue;
        }
    }
    if(!json.hasOwnProperty("startDate") || !json.hasOwnProperty("endDate")){
        var errorString = "Please select both start date and end date";
        raiseAlert({
            icon: 'error',
            title: 'Oops...',
            html: errorString,
        })
        return;
    }
    if(json["startDate"] > json["endDate"]){
        var errorString = "Start date must be before end date";
        raiseAlert({
            icon: 'error',
            title: 'Oops...',
            html: errorString,
        })
        return;
    }
    json["startDate"] = new Date(json["startDate"]).toISOString();
    // increase date by 23 hours, 59 minutes, 59 seconds, 999 milliseconds time to include all transactions in the day
    json["endDate"] = new Date(new Date(json["endDate"]).getTime() + 86399999).toISOString();
    
    json = JSON.stringify(json);
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        contentType: "application/json",
        success: function (data) {
            downloadPDF(data, "SalesReport");
        },
        error: function (response) {
            handleAjaxError(response);
        }
    });

}

function initializeDateInputs(){
	var endDate = new Date();
	var startDate = new Date(endDate.getFullYear(), endDate.getMonth(), 1);
	
	// increasing time since conversion to ISOString decreases time by timezone offset
	startDate = increaseTimeByTimeZoneOffset(startDate);
	endDate = increaseTimeByTimeZoneOffset(endDate);
	// Converting to yyyy-mm-dd format
	startDate = startDate.toISOString().slice(0,10);
	endDate = endDate.toISOString().slice(0,10);

	$('#startDate').val(startDate);
	$('#endDate').val(endDate);
	return {startDate: startDate, endDate: endDate};
}

function init(){

    $("#brand-report").click(brandReport);
    $("#inventory-report").click(inventoryReport);
    $("#sales-report").click(salesReport);
    initializeDateInputs();

}

$(document).ready(init);
