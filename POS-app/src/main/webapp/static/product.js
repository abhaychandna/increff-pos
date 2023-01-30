
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

function addProduct(event){
	var $form = $("#Product-form");
	var json = toJson($form);
	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			Swal.fire('Success', 'Brand added successfully', 'success');
	   		getProductList(); 
			resetAddDialog();
	   },
	   error: handleAjaxError
	});

	return false;
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);   
	   },
	   error: handleAjaxError
	});	
}

function displayProduct(data){
	$("#Product-edit-form input[name=id]").val(data.id);	
	$("#Product-edit-form input[name=barcode]").val(data.barcode);
	$("#Product-edit-form input[name=mrp]").val(data.mrp);
	$("#Product-edit-form input[name=name]").val(data.name);
	$('#edit-Product-modal').modal('toggle');
}


function updateProduct(event){
	$('#edit-Product-modal').modal('toggle');
	//Get the ID
	var id = $("#Product-edit-form input[name=id]").val();	
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#Product-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getProductList();   
	   },
	   error: handleAjaxError
	});

	return false;
}


function getProductList(){
	$("#Product-table").DataTable().ajax.reload();
}

function deleteProduct(id){
	var url = getProductUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getProductList();  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#ProductFile')[0].files[0];
	if(!file){
		Swal.fire('Error', 'Please select a file to upload', 'error');
		return;
	}
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	var url = getProductUrl() + "/add/bulk";
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
			Swal.fire('Success', 'Products uploaded successfully', 'success');
            resetUploadDialog();
			getProductList();
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			console.log(errorData);
			processCount = fileData.length;
			$('#statusView').html("Failed to upload " + errorData.length + " rows. Download errors to see error descriptions.");
			Swal.fire('Error', 'Failed to upload products. Download errors to see detailed descriptions.', 'error');
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

function openAddProductModal(){
	$('#add-Product-modal').modal('toggle');
}


function displayProductList(data){
	var $tbody = $('#Product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button onclick="displayEditProduct(' + e.id + ')">edit</button>'
		var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.mrp + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#ProductFile');
	$file.val('');
	$('#ProductFileName').html("Choose File");
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
	var $file = $('#ProductFile');
	var fileName = $file.val();
	$('#ProductFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-Product-modal').modal('toggle');
}

function displayProduct(data){
	$("#Product-edit-form input[name=barcode]").val(data.barcode);
	$("#Product-edit-form input[name=brand]").val(data.brand);
	$("#Product-edit-form input[name=mrp]").val(data.mrp);
	$("#Product-edit-form input[name=name]").val(data.name);	
	$("#Product-edit-form input[name=category]").val(data.category);
	
	$("#Product-edit-form input[name=id]").val(data.id);	
	$('#edit-Product-modal').modal('toggle');
}

function tableColumns(){
	columns = [
		{ "data": "id" },
		{ "data": "barcode" },
		{ "data": "brand" },
		{ "data": "category" },
		{ "data": "name" },
		{ "data": "mrp" },
	];
	if (userRole == 'supervisor'){
		columns.push({
			"data":null,
			"render":function(o){
				return '<button type="button" class="btn btn-info" onclick="displayEditProduct(' + o.id + ')"th:if="${info.getRole() == "supervisor"}>Edit</button>'
			}
		});
	}
	return columns;
}

//INITIALIZATION CODE
function init(){

	$('#Product-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
		"ordering": false,
		"lengthMenu": [2,5,10,20, 40, 60, 80, 100],
		"pageLength":10,
		"ajax": {url : getProductUrl()},
		"columns": tableColumns()
	});

	$('#add-Product').click(addProduct);
	$('#open-add-Product-modal').click(openAddProductModal)
	$('#update-Product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#ProductFile').on('change', updateFileName)
}

$(document).ready(init);
//$(document).ready(getProductList);

