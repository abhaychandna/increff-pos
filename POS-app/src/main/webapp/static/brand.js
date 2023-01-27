
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

//BUTTON ACTIONS

function openAddBrandModal(){
	$('#add-Brand-modal').modal('toggle');
}


function addBrand(event){
	//Set the values to update
	var $form = $("#Brand-form");
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
	   		getBrandList();  
			resetAddDialog();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){
	$('#edit-Brand-modal').modal('toggle');
	//Get the ID
	var id = $("#Brand-edit-form input[name=id]").val();	
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#Brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBrandList();   
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
			$('#statusView').html("Upload Successful");
		},
		error: function(response){
			errorData = JSON.parse(response.responseJSON.message) ;
			console.log(errorData);
			processCount = fileData.length;
			$('#statusView').html("Failed to upload " + errorData.length + " rows. Download errors to see error descriptions.");
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
	$('#statusView').html('');
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
	var fileName = $file.val();
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
	$('#edit-Brand-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){

	$('#brand-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
		"ordering": false,
		"lengthMenu": [2,5,10,20, 40, 60, 80, 100],
		"pageLength":10,
		"ajax": {url : getBrandUrl()},
		"columns": [
            { "data": "id" },
            { "data": "brand" },
            { "data": "category" },
			{
				"data":null,
				"render":function(o){

					return '<button type="button" class="btn btn-info" onclick="displayEditBrand(' + o.id + ')"th:if="${info.getRole() == "supervisor"}>Edit</button>'
				}
			}
        ]
	});

	$('#add-Brand').click(addBrand);
	$('#open-add-Brand-modal').click(openAddBrandModal)
	$('#update-Brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#BrandFile').on('change', updateFileName)
}

$(document).ready(init);
//$(document).ready(getBrandList);

