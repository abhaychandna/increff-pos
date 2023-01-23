
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function addInventory(event){
	var $form = $("#Inventory-form");
	var json = toJson($form);
	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getInventoryList();  
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(event){
	$('#edit-Inventory-modal').modal('toggle');
	//Get the ID
	var id = $("#Inventory-edit-form input[name=id]").val();	
	var url = getInventoryUrl();

	//Set the values to update
	var $form = $("#Inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getInventoryList();   
	   },
	   error: handleAjaxError
	});

	return false;
}

function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	  
	   },
	   error: handleAjaxError
	});
}

function deleteInventory(id){
	var url = getInventoryUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getInventoryList();  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#InventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		getInventoryList();
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getInventoryUrl();
	console.log(json);
	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		uploadRows();  
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

//UI DISPLAY METHODS
function getBrandList(){
    var url = getBrandUrl();
    $.ajax({
       url: url,
       type: 'GET',
       success: function(data) {
            var brandSelect = $("#brand-select");
            var categorySelect  = $("#category-select");
            var brandEditSelect = $("#brand-edit-select");
            var categoryEditSelect  = $("#category-edit-select");
            for(var i in data){
                brandSelect.append("<option value='"+data[i].brand+"'>"+data[i].brand+"</option>");
                categorySelect.append("<option value='"+data[i].category+"'>"+data[i].category+"</option>");
                brandEditSelect.append("<option value='"+data[i].brand+"'>"+data[i].brand+"</option>");
                categoryEditSelect.append("<option value='"+data[i].category+"'>"+data[i].category+"</option>");
            }
       },
       error: handleAjaxError
    });
}

function displayInventoryList(data){
	var $tbody = $('#Inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button onclick="displayEditInventory(' + e.id + ')">edit</button>'
		var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditInventory(id){
	var url = getInventoryUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#InventoryFile');
	$file.val('');
	$('#InventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#InventoryFile');
	var fileName = $file.val();
	$('#InventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-Inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#Inventory-edit-form input[name=barcode]").val(data.barcode);	
	$("#Inventory-edit-form input[name=quantity]").val(data.quantity);	
	$("#Inventory-edit-form input[name=id]").val(data.id);	
	$('#edit-Inventory-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	$('#add-Inventory').click(addInventory);
	$('#update-Inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#InventoryFile').on('change', updateFileName)
	getBrandList();
}

$(document).ready(init);
$(document).ready(getInventoryList);

