function truncateFloat(num, places) {
	num = parseFloat(num);
	return Math.trunc(num * Math.pow(10, places)) / Math.pow(10, places);
}

function validateFormHTML($form){
	return $form[0].reportValidity();
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
	console.log("Alert: " + title + " " + text + " " + icon);
	Swal.fire({
		icon: icon,
		title: title,
		text: text,
	})
}

function getFileExtension(filename) {
	return filename.slice((filename.lastIndexOf(".") - 1 >>> 0) + 2);
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
