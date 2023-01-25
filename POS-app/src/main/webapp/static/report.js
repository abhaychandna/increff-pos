// TODO : Remove testing variable from here
var globalData;

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
            console.log(data);
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
            console.log(data);
            downloadPDF(data, "InventoryReport");
        },
        error: function (response) {
            handleAjaxError(response);
        }
    });
}

// sales report is post request
/*

{
  "brand": "string",
  "category": "string",
  "endDate": "string",
  "startDate": "string"
}
*/

function salesReport(){
    var url = getReportUrl() + "/sales";
    var $form = $("#sales-report-form");

	var json = JSON.parse(toJson($form));

    for (var key in json) {
        if (json[key] === "") {
            delete json[key];
            continue;
        }
    }
    if(!json.hasOwnProperty("startDate") || !json.hasOwnProperty("endDate")){
        alert("Please enter start date and end date");
        return;
    }
    json["startDate"] = new Date(json["startDate"]).toISOString();
    json["endDate"] = new Date(json["endDate"]).toISOString();
    console.log(json);
    globalData = json;

    json = JSON.stringify(json);
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        contentType: "application/json",
        success: function (data) {
            console.log(data);
            downloadPDF(data, "SalesReport");
        },
        error: function (response) {
            handleAjaxError(response);
        }
    });

}

function init(){

    $("#brand-report").click(brandReport);
    $("#inventory-report").click(inventoryReport);
    $("#sales-report").click(salesReport);


}

$(document).ready(init);
