
const HOST_URL = location.protocol + '//' + location.host;
const BASE_URL = $("meta[name=baseUrl]").attr("content");
const LOGOUT_URL = HOST_URL + BASE_URL + "/session/logout";
const LOGOUT_REDIRECT_URL = HOST_URL + BASE_URL;

const LOGOUT_ALERT_TIME_MILLI = 1500;

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
function toJsonString($form){
	return JSON.stringify(toJson($form));
}

function toJson($form){
	var serialized = $form.serializeArray();
	
	var s = '';
	var data = {};
	for(s in serialized){
		data[serialized[s]['name']] = serialized[s]['value']
	}
	return data;
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


function increaseTimeByTimeZoneOffset(datetime){
	// datetime.getTimezoneOffset() return offset in minutes. GMT +5:30 reutrn -330
	var millSecondsOffset = datetime.getTimezoneOffset() * 60000;
	return new Date(datetime.getTime() - millSecondsOffset);
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
	$.ajax({
		url: LOGOUT_URL,
		type: 'GET',
		success: function(response){			
			showLogoutAlert();
		},
		error: function(response){
			handleAjaxError(response);
		}
	});

}

function showLogoutAlert(){
	const title = 'Logging out';
	const text = 'Redirecting to login page...';
	const icon = 'success';
	Swal.fire({
		icon: icon,
		title: title,
		text: text,
		timer: LOGOUT_ALERT_TIME_MILLI,
	});
	setTimeout(function(){
		window.location = LOGOUT_REDIRECT_URL;
	}, LOGOUT_ALERT_TIME_MILLI);
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


function convertValuesToString(json){
	for(var key in json){
		json[key] = json[key].toString();
	}
	return json;
}
function checkFormEqualsData(form, data, fieldList){
	var json = toJson(form);
	
	// if field list is not null, check only those fields
	if(fieldList != null){
		var jsonFieldList = {};
		var dataFieldList = {};
		for(var i = 0; i < fieldList.length; i++){
			jsonFieldList[fieldList[i]] = json[fieldList[i]];
			dataFieldList[fieldList[i]] = data[fieldList[i]];
		}
		json = jsonFieldList;
		data = dataFieldList;
	}
	json = JSON.stringify(convertValuesToString(json));
	data = JSON.stringify(convertValuesToString(data));
	console.log(json);
	console.log(data);
	console.log(fieldList);
	return JSON.stringify(json) == JSON.stringify(data);
}
function enableButtonOnFormChange(formSelector, buttonSelector, jsonData, fieldList){
	$(formSelector).on('input change', function() {
		var $form = $(formSelector);
		var $button = $(buttonSelector);
		if(checkFormEqualsData($form, jsonData, fieldList)){
			$button.attr('disabled',true);
		}else{
			$button.attr('disabled', false);
		}
	});
}