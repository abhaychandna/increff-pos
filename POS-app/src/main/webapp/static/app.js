
var logoutRedirectUrl = "http://localhost:9000/pos";

function truncateFloat(num, places) {
	num = parseFloat(num);
	return Math.trunc(num * Math.pow(10, places)) / Math.pow(10, places);
}

function validateFormHTML($form){
	return $form[0].reportValidity();
}

var fileErrorMessages = [];

function validateFileHeaders(json, headerColumns){
	for(var i in headerColumns){
		if(!json.hasOwnProperty(headerColumns[i])){
			fileErrorMessages.push('File columns do not match. Please check the file and try again');
		}
	}
}

function validateFileLength(fileData, length){
	if(fileData.length > length){
		fileErrorMessages.push('File contains more than ' + length + ' rows. Please decrease rows and try again');
	}
}

function validateFile(fileData, headerColumns){
	fileErrorMessages = [];
	var maxRowCount = 3;
	var json = fileData[0];
	validateFileHeaders(json, headerColumns);
	validateFileLength(fileData, maxRowCount);
	if(fileErrorMessages.length > 0){
		raiseAlert('Error', fileErrorMessages.join('<br>'), 'error');
		return false;
	}
	return true;
}

function resetForm(formSelector){
	//Reset form values
	var $form = $(formSelector);
	$form.trigger("reset");
}
function toggleModal(modalSelector){
	$(modalSelector).modal('toggle');
}

function raiseAlert(title, text, icon){
	if(typeof title === 'object'){
		var obj = title;
		if (obj.text != null) text = obj.text;
		if (obj.html != null) text = obj.html;
		icon = obj.icon;
		title = obj.title;
	}
	console.log("Alert: " + title + " " + text + " " + icon);
	if(icon == 'error'){
		Swal.fire({
			icon: icon,
			title: title,
			html: text,
		})
	}
	else if (icon == 'success'){
		Swal.fire({
			icon: icon,
			title: title,
			html: text,
			timer: 1000,
		})
	}
}

function getFileExtension(filename) {
	return filename.substr((filename.lastIndexOf(".") + 1));
}

//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}


function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	//alert(response.message);
	raiseAlert('Oops...', response.message, 'error');
}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click(); 
}

function downloadPDF(base64String, OUT_FILENAME) {
	const linkSource = `data:application/pdf;base64,${base64String}`;
	const downloadLink = document.createElement("a");
	const fileName = OUT_FILENAME + ".pdf";
	downloadLink.href = linkSource;
	downloadLink.download = fileName;
	downloadLink.click();
}

function logout(){
	var timer = 2500;
	var title = 'Logging out';
	var text = 'Redirecting to login page...';
	var icon = 'success';
	Swal.fire({
		icon: icon,
		title: title,
		text: text,
		timer: timer,
	});
	setTimeout(function(){
		window.location = logoutRedirectUrl;
	}
	, timer);
}
document.getElementById("logout-btn").addEventListener("click", logout);

function emphasizeNavbarCurrentLink(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	var url = document.URL;
	path = url.substring(url.indexOf(baseUrl));
	var element = document.querySelectorAll('[href="' + path + '"][class="nav-link"]');
	element[0].style.fontWeight = "bold";	
}
emphasizeNavbarCurrentLink();
