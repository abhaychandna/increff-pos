
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}


function openAddInventoryModal(){
	$('#add-Inventory-modal').modal('toggle');
}
function addInventory(event){
	var $form = $("#Inventory-form");
	if(!validateFormHTML($form)) return;
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
			raiseAlert('Success', 'Inventory added successfully', 'success');
	   		getInventoryList();  
			resetForm("#Inventory-form");
			toggleModal("#add-Inventory-modal");
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(event){
	//Get the ID
	var id = $("#Inventory-edit-form input[name=id]").val();	
	var url = getInventoryUrl();

	//Set the values to update
	var $form = $("#Inventory-edit-form");
	if(!validateFormHTML($form)) return;
	var json = toJson($form);

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
	uploadRows();
}

function uploadRows(){
	var url = getInventoryUrl() + "/add/bulk";
	var jsonData = JSON.stringify(fileData);
	// TODO : Add file upload limit
	$.ajax({
		url: url,
		type: 'POST',
		data: jsonData,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			console.log(response);
			errorData = response;
			processCount = fileData.length;	 
			raiseAlert('Success', 'Inventory uploaded successfully', 'success');
            resetUploadDialog();
			toggleModal("upload-Inventory-modal");
			getInventoryList();
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			console.log(errorData);
			processCount = fileData.length;
			var $file = $('#InventoryFile');
	        $file.val('');       
            $('#InventoryFileName').html("Choose File");
			document.getElementById('download-errors').disabled=false;
			$('#statusView').html("Status : Failed to upload " + errorData.length + " rows. Download errors to see error descriptions.");
			raiseAlert('Error', 'Failed to upload inventory. Download errors to see detailed descriptions.', 'error');
		}
	 });

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

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
	$('#statusView').html('Please Upload TSV file');
	document.getElementById('download-errors').disabled=true;
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
	$("#Inventory-edit-form input[name=id]").val(data.id);	
	$("#update-Inventory").attr('disabled', true);
	$('#edit-Inventory-modal').modal('toggle');
}

function tableColumns(){
	columns = [
		{
			"data": "id",
			render: function (data, type, row, meta) {
				return meta.row + meta.settings._iDisplayStart + 1;
			}
		},
		{ "data": "barcode" },
		{ "data": "quantity"},
	];
	if (userRole == 'supervisor'){
		columns.push({
			"data":null,
			"render":function(o){
				return '<button type="button" class="btn btn-info" onclick="displayEditInventory(' + o.id + ')"th:if="${info.getRole() == "supervisor"}>Edit</button>'
			}
		});
	}
	return columns;
}

//INITIALIZATION CODE
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
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#InventoryFile').on('change', updateFileName)

	$('#Inventory-edit-form').on('input change', function() {
		$('#update-Inventory').attr('disabled', false);
	});
	$('#Inventory-form').on('input change', function() {
		$('#add-Inventory').attr('disabled', false);
	});
}

$(document).ready(init);
$(document).ready(getInventoryList);

