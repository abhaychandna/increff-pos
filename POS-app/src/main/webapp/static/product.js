
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

function addProduct(event){
	var $form = $("#Product-form");
	if(!validateFormHTML($form)) return;
	var json = toJsonString($form);
	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			raiseAlert('Success', 'Brand added successfully', 'success');
	   		getProductList(); 
			resetForm("#Product-form");
			toggleAddProductModal();
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

function toggleEditProductModal(){
	$('#edit-Product-modal').modal('toggle');
}
function updateProduct(event){
	
	var id = $("#Product-edit-form input[name=id]").val();	
	var url = getProductUrl() + "/" + id;

	
	var $form = $("#Product-edit-form");
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
			raiseAlert('Success', 'Product updated successfully', 'success');
	   		getProductList();   
			resetForm("#Product-edit-form");
			toggleEditProductModal();
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


var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#ProductFile')[0].files[0];
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
    var headerColumns = ['barcode', 'brand', 'category', 'name', 'mrp'];
	if (validateFile(fileData, headerColumns) == false){
		resetUploadDialog();
		return;
	}
	uploadRows();
}

function uploadRows(){
	var url = getProductUrl() + "/bulk-add";
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
			raiseAlert('Success', 'Products uploaded successfully', 'success');
            resetUploadDialog();
			toggleModal("upload-Product-modal");
			getProductList();
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			
			processCount = fileData.length;
			var $file = $('#ProductFile');
	        $file.val('');       
            $('#ProductFileName').html("Choose File");
			document.getElementById('download-errors').disabled=false;
			$('#statusView').html("Status : Upload failed. Download errors to see error descriptions.");
			raiseAlert('Error', 'Failed to upload products. Download errors to see detailed descriptions.', 'error');
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



function toggleAddProductModal(){
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
	
	var $file = $('#ProductFile');
	$file.val('');
	$('#ProductFileName').html("Choose File");
	$('#statusView').html('Status : Please Upload TSV file');
	document.getElementById('download-errors').disabled=true;

}

function updateFileName(){
	var $file = $('#ProductFile');
    var fileName =  $file[0].files[0].name;
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
	$('#update-Product').attr('disabled', true);	
	var fieldList = ["mrp", "name"];
	enableButtonOnFormChange("#Product-edit-form", "#update-Product", data, fieldList);
	toggleEditProductModal();
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
		{ "data": "brand" },
		{ "data": "category" },
		{ "data": "name" },
		{ 
			"data": "mrp",
			"render":function(data){
				return truncateFloat(data, 2);
			}
		},
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


function init(){

	$('#Product-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
		"ordering": false,
		"lengthMenu": [5, 10, 25, 50, 100],
		"pageLength":10,
		"ajax": {url : getProductUrl()},
		"columns": tableColumns()
	});

	$('#add-Product').click(addProduct);
	$('#open-add-Product-modal').click(toggleAddProductModal)
	$('#update-Product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#ProductFile').on('change', updateFileName)

}

$(document).ready(init);


