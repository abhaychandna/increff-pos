
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}


function openAddInventoryModal(){
	$('#add-Inventory-modal').modal('toggle');
}
function addInventory(){
	var $form = $("#Inventory-form");
	if(!validateFormHTML($form)) return;
	var json = toJsonString($form);
	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			raiseAlert('Success', 'Inventory added successfully', 'success');
	   		getInventoryList();  
			resetForm("#Inventory-form");
			toggleModal("#add-Inventory-modal");
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(){
	
	var id = $("#Inventory-edit-form input[name=id]").val();	
	var url = getInventoryUrl();

	
	var $form = $("#Inventory-edit-form");
	if(!validateFormHTML($form)) return;
	var json = toJsonString($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			raiseAlert('Success', 'Inventory updated successfully', 'success');
	   		getInventoryList();  
			resetForm("#Inventory-edit-form");
			toggleModal("#edit-Inventory-modal"); 
	   },
	   error: handleAjaxError
	});

	return false;
}

function getInventoryList(){
	$("#Inventory-table").DataTable().ajax.reload();
}

function refreshList(){
	raiseAlert('Success', 'Refreshing Table', 'success')
	getInventoryList();
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


var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#InventoryFile')[0].files[0];
	if(!file){
		raiseAlert('Error', 'Please select a file to upload', 'error');
		return;
	}
	if(getFileExtension(file.name) != 'tsv'){
		raiseAlert('Error', 'Only TSV files are allowed ', 'error');
		resetUploadDialog();
		return;
	}
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;	
    var headerColumns = ["barcode", "quantity"];
	if (validateFile(fileData, headerColumns) == false){
		resetUploadDialog();
		return;
	}
	uploadRows();
}

function uploadRows(){
	var url = getInventoryUrl() + "/bulk-add";
	var jsonData = JSON.stringify(fileData);

	$.ajax({
		url: url,
		type: 'POST',
		data: jsonData,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			
			errorData = response;
			processCount = fileData.length;	 
			raiseAlert('Success', 'Inventory uploaded successfully', 'success');
            resetUploadDialog();
			toggleModal("upload-Inventory-modal");
			getInventoryList();
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			
			processCount = fileData.length;
			var $file = $('#InventoryFile');
	        $file.val('');       
            $('#InventoryFileName').html("Choose File");
			document.getElementById('download-errors').disabled=false;
			document.getElementById('download-errors').style.display = "block";
			$('#statusView').html("Status : Upload failed. Download errors to see error descriptions.");
			raiseAlert('Error', 'Failed to upload inventory. Download errors to see detailed descriptions.', 'error');
		}
	 });

}

function downloadErrors(){
	writeFileData(errorData);
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
	
	var $file = $('#InventoryFile');
	$file.val('');
	$('#InventoryFileName').html("Choose File");
	$('#statusView').html('Status : Please Upload TSV file');
	document.getElementById('download-errors').disabled=true;
	document.getElementById('download-errors').style.display = "none";

}

function updateFileName(){
	var $file = $('#InventoryFile');
    var fileName =  $file[0].files[0].name;
	$('#InventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-Inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#Inventory-edit-form input[name=barcode]").val(data.barcode);	
	$("#Inventory-edit-form input[name=quantity]").val(data.quantity);	
	$("#Inventory-edit-form input[name=id]").val(data.productId);	
	$("#update-Inventory").attr('disabled', true);

	var fieldList = ["quantity"];
	enableButtonOnFormChange("#Inventory-edit-form", "#update-Inventory", data, fieldList);
	$('#edit-Inventory-modal').modal('toggle');
}

function tableColumns(){
	columns = [
		{
			"data": "productId",
			render: function (data, type, row, meta) {
				return meta.row + meta.settings._iDisplayStart + 1;
			}
		},
		{ "data": "barcode" },
		{ "data": "quantity"},
	];
	if (userRole == 'SUPERVISOR'){
		columns.push({
			"data":null,
			"render":function(o){
				
				return '<button type="button" class="btn btn-info" onclick="displayEditInventory(' + o.productId + ')"th:if="${info.getRole() == "SUPERVISOR"}>Edit</button>'
			}
		});
	}
	return columns;
}


function init(){

	$('#Inventory-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
		"ordering": false,
		"lengthMenu": [5, 10, 25, 50, 100],
		"pageLength":10,
		"ajax": {url : getInventoryUrl()},
		"columns": tableColumns()
	});
	
	$('#add-Inventory').click(addInventory);
	$('#open-add-Inventory-modal').click(openAddInventoryModal)
	$('#update-Inventory').click(updateInventory);
	$('#refresh-data').click(refreshList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#InventoryFile').on('change', updateFileName)

}

$(document).ready(init);

