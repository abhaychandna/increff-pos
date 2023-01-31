
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

//BUTTON ACTIONS

function toggleAddBrandModal(){
	$('#add-Brand-modal').modal('toggle');
	resetAddDialog();
}
function toggleEditBrandModal(){
	$('#edit-Brand-modal').modal('toggle');
}


function addBrand(event){
	//Set the values to update
	var $form = $("#Brand-form");
	if(!validateFormHTML($form)) return;
	var json = toJson($form);
	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			Swal.fire('Success', 'Brand added successfully', 'success');
	   		getBrandList();  
			resetAddDialog();
			toggleAddBrandModal();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){
	//Get the ID
	var id = $("#Brand-edit-form input[name=id]").val();	
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#Brand-edit-form");
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
			Swal.fire('Success', 'Brand updated successfully', 'success');
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

function deleteBrand(id){
	var url = getBrandUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getBrandList();  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#BrandFile')[0].files[0];
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
	var url = getBrandUrl() + "/add/bulk";
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
			Swal.fire('Success', 'Brands uploaded successfully', 'success');
            resetUploadDialog();
			toggleModal("upload-Brand-modal");
			getBrandList();
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			console.log(errorData);
			processCount = fileData.length;
			var $file = $('#BrandFile');
	        $file.val('');       
            $('#BrandFileName').html("Choose File");
			document.getElementById('download-errors').disabled=false;
			$('#statusView').html("Status : Failed to upload " + errorData.length + " rows. Download errors to see error descriptions.");
			Swal.fire('Error', 'Failed to upload brands. Download errors to see detailed descriptions.', 'error');
		}
	 });

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

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
	//Reset file name
	var $file = $('#BrandFile');
	$file.val('');
	$('#BrandFileName').html("Choose File");
	$('#statusView').html('Please Upload TSV file');
	document.getElementById('download-errors').disabled=true;

	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function resetAddDialog(){
	//Reset form values
	var $form = $("#Brand-form");
	$form.trigger("reset");
}

function updateUploadDialog(){
	//$('#statusView').html("" + fileData.length);
	//$('#processCount').html("" + processCount);
	//$('#errorCount').html("" + errorData.length);
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
	if (userRole == 'supervisor'){
		columns.push({
			"data":null,
			"render":function(o){
				return '<button type="button" class="btn btn-info" onclick="displayEditBrand(' + o.id + ')">Edit</button>';
			}
		});
	}
	return columns;
}

//INITIALIZATION CODE
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

	$('#Brand-edit-form').on('input change', function() {
		$('#update-Brand').attr('disabled', false);
	});
	$('#Brand-form').on('input change', function() {
		$('#add-Brand').attr('disabled', false);
	});
}

$(document).ready(init);
//$(document).ready(getBrandList);

