
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}



function toggleAddBrandModal(){
	$('#add-Brand-modal').modal('toggle');
	resetAddDialog();
}
function toggleEditBrandModal(){
	$('#edit-Brand-modal').modal('toggle');
}


function addBrand(event){
	
	var $form = $("#Brand-form");
	if(!validateFormHTML($form)) return;
	var json = toJsonString($form);
	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			raiseAlert('Success', 'Brand added successfully', 'success');
	   		getBrandList();  
			resetAddDialog();
			toggleAddBrandModal();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){
	
	var id = $("#Brand-edit-form input[name=id]").val();	
	var url = getBrandUrl() + "/" + id;

	
	var $form = $("#Brand-edit-form");
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
			raiseAlert('Success', 'Brand updated successfully', 'success');
	   		getBrandList();   
			toggleEditBrandModal();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	$("#brand-table").DataTable().ajax.reload();
}


var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#BrandFile')[0].files[0];
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
	var headerColumns = ["brand", "category"];
	if (validateFile(fileData, headerColumns) == false){
		resetUploadDialog();
		return;
	}
	uploadRows();
}

function uploadRows(){
	var url = getBrandUrl() + "/bulk-add";
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
			raiseAlert('Success', 'Brands uploaded successfully', 'success');
            resetUploadDialog();
			toggleModal("upload-Brand-modal");
			getBrandList();
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			
			processCount = fileData.length;
			var $file = $('#BrandFile');
	        $file.val('');       
            $('#BrandFileName').html("Choose File");
			document.getElementById('download-errors').disabled=false;
			$('#statusView').html("Status : Upload failed. Download errors to see error descriptions.");
			raiseAlert('Error', 'Failed to upload brands. Download errors to see detailed descriptions.', 'error');
		}
	 });

}

function downloadErrors(){
	writeFileData(errorData);
}



function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	var $file = $('#BrandFile');
	$file.val('');
	$('#BrandFileName').html("Choose File");
	$('#statusView').html('Status : Please Upload TSV file');
	document.getElementById('download-errors').disabled=true;
}

function resetAddDialog(){
	var $form = $("#Brand-form");
	$form.trigger("reset");
}

function updateFileName(){
	var $file = $('#BrandFile');
    var fileName =  $file[0].files[0].name;
	$('#BrandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-Brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#Brand-edit-form input[name=brand]").val(data.brand);	
	$("#Brand-edit-form input[name=category]").val(data.category);	
	$("#Brand-edit-form input[name=id]").val(data.id);
	$('#update-Brand').attr('disabled', true);	
	enableButtonOnFormChange("#Brand-edit-form", "#update-Brand", data);
	toggleEditBrandModal();
}

function tableColumns(){
	columns = [
		{
			"data": "id",
			render: function (data, type, row, meta) {
				return meta.row + meta.settings._iDisplayStart + 1;
			}
		},
		{ "data": "brand" },
		{ "data": "category" },
	];
	if (userRole == 'SUPERVISOR'){
		columns.push({
			"data":null,
			"render":function(o){
				return '<button type="button" class="btn btn-info" onclick="displayEditBrand(' + o.id + ')">Edit</button>';
			}
		});
	}
	return columns;
}


function init(){

	$('#brand-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
		"ordering": false,
		"lengthMenu": [5, 10, 25, 50, 100],
		"pageLength":10,
		"ajax": {url : getBrandUrl()},
		"columns": tableColumns(),
	});

	$('#add-Brand').click(addBrand);
	$('#open-add-Brand-modal').click(toggleAddBrandModal)
	$('#update-Brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#BrandFile').on('change', updateFileName)

}

$(document).ready(init);


