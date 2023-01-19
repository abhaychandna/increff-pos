var wholeOrder = []

function getOrderItemUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/orders";
}

function getOrderUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/orders";
}

function getProductUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/products";
}

function resetForm() {
    var element = document.getElementById("order-item-form");
    element.reset()
}

function deleteOrderItem(id) {
    wholeOrder.splice(id, 1);
    displayOrderItemList(wholeOrder);
} 

function displayOrderItemList(data){
	var $tbody = $('#order-item-table').find('tbody');
	$tbody.empty();

	for(var i in wholeOrder) {
        var e = wholeOrder[i];  
        var buttonHtml = '<button onclick="deleteOrderItem('+i+')" class="btn">Delete<i class="fa-regular fa-circle-xmark"></i></button>';
        var row = '<tr>'
            + '<td>' + JSON.parse(wholeOrder[i]).barcode + '</td>'
            + '<td>'  + JSON.parse(wholeOrder[i]).quantity + '</td>'
            + '<td>'  + JSON.parse(wholeOrder[i]).sellingPrice + '</td>'
            + '<td>'  + buttonHtml + '</td>'
            + '</tr>';

        $tbody.append(row);
    }

}

function checkOrderItemExist() {
    for(i in wholeOrder) {
        var barcode = JSON.parse(wholeOrder[i]).barcode;
        console.log(barcode);
        var temp_barcode = $("#order-item-form input[name=barcode]").val();
        console.log(temp_barcode);
        if(temp_barcode == barcode) {
            console.log("Index" + i);
            return i;
        }
    }
    return -1;
}


function changeQty(vars) {
    var barcode = vars[0];
    console.log(barcode);
    var quantity = parseInt(vars[1]);
    //console.log(quantity);
    // console.log(wholeOrder);
    for(i in wholeOrder) {
        let data = {}
        var temp_barcode = JSON.parse(wholeOrder[i]).barcode;
        if(temp_barcode == barcode) {
            var prev = parseInt(JSON.parse(wholeOrder[i]).quantity);
            var new_quantity = prev + quantity;
            console.log(new_quantity);
            var string1 = new_quantity.toString();
            console.log(string1);
            data["barcode"]=JSON.parse(wholeOrder[i]).barcode;
            data["quantity"]=string1;
            data["sellingPrice"]=JSON.parse(wholeOrder[i]).sellingPrice;
            console.log(data);
            var new_data = JSON.stringify(data);
            wholeOrder[i] = new_data;
        }
    }
    console.log(wholeOrder);
}

function checkSellingPrice(vars) {
    var barcode = vars[0];
    var sp = vars[2];
    for (i in wholeOrder) {
        var temp_barcode = JSON.parse(wholeOrder[i]).barcode;
        if (temp_barcode == barcode) {
            var cur_sp = parseInt(JSON.parse(wholeOrder[i]).sellingPrice);
            if (cur_sp == sp) {
                return true;
            }
        }
    }
    return false;
}
var barcode = []
function getBarcode(data) {
    for (i in data) {
        var b = data[i].barcode;
        barcode.push(b);
    }
}

function getProductList() {
    var url = getProductUrl();
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            barcode = []
            getBarcode(data);
            
        },
        error: handleAjaxError
     });
}

function checkBarcode(data) {
    for (i in barcode) {
        if (barcode[i] == data) {
            return true;
        }

    }
    return false;
}

function validateBarcode(barcode){
    var error = false;
    if(barcode == ""){
        alert("Barcode cannot be empty");
        return true;
    }
    return error;
}
function validateQuantity(quantity){
    var error = false;
    if(quantity == ""){
        alert("Quantity cannot be empty");
        error = true;
    }
    if(quantity <= 0){
        alert("Quantity has to be positive");
        error = true;
    }
    return error;
}
function validateSellingPrice(sellingPrice){
    var error = false;
    if(sellingPrice == ""){
        alert("Selling Price cannot be empty");
        error = true;
    }
    if(sellingPrice <= 0){
        alert("Selling Price has to be positive");
        error = true;
    }
    return error;
}

function addOrderItem(event) {
    var $form = $("#order-item-form");
    var json = toJson($form);
    var jsonObj = $.parseJSON(json);
    var barcode = $("#order-item-form input[name=barcode]").val();
    var quantity = $("#order-item-form input[name=quantity]").val();
    var sp = $("#order-item-form input[name=sellingPrice]").val();
    
    var error = false;

    
    var error = false;
    error = error | validateBarcode(barcode);
    error = error | validateQuantity(quantity);
    error = error | validateSellingPrice(sp);
    if(error)return;

    var index = checkOrderItemExist();
    if(index != -1) {
        console.log("inside check");
        wholeOrder[i] = json;
    }
    else {
        // TODO : Add check if barcode exists ?
        wholeOrder.push(json)        
    }
    resetForm();
    displayOrderItemList(wholeOrder)
}

function displayCart() {
    $('#add-order-item-modal').modal('toggle');
    // table should be empty
    var $tbody = $('#order-item-table').find('tbody');
    $tbody.empty();
    wholeOrder = [];
}

function getOrderItemList() {
    var jsonObj = $.parseJSON('[' + wholeOrder + ']');
    console.log(jsonObj);
}

function OrderView(id) {
    $('#order-view-modal').modal('toggle');

}

function displayOrderList(data) {
    
    var $tbody = $('#Order-table').find('tbody');
    $tbody.empty();
    for(var i in data){
        var e = data[i];
        console.log(data[i].orderDate); 
   		var buttonHtml = ' <button  onclick="OrderView(' + e.id + ')">view</button>'
   		var row = '<tr>'
   		+ '<td>' + e.id + '</td>'
    	+ '<td>'  + e.orderDate + '</td>'
    	+ '<td>' + buttonHtml + '</td>'
   		+ '</tr>';
   		$tbody.append(row);
    }
}


function getOrderList() {
	$("#Order-table").DataTable().ajax.reload();
}

function arrayToJson() {
    let json = [];
    for(i in wholeOrder) {
        let data = {};
        data["barcode"]=JSON.parse(wholeOrder[i]).barcode;
        data["quantity"]=JSON.parse(wholeOrder[i]).quantity;
        data["sellingPrice"]=JSON.parse(wholeOrder[i]).sellingPrice;
        json.push(data);
    }
    return JSON.stringify(json);
}

function getProductName(productId) {
    var url = getProductUrl() + "/" + productId;
    var name = "";
    $.ajax({
        url: url,
        type: 'GET',
        async: false,
        success: function(data) {
            name = data.name;
        },
        error: handleAjaxError
     });
    return name;
}

var E;
function placeOrder() {
    var url = getOrderItemUrl();

    var jsonObj = arrayToJson();
    console.log(jsonObj);
    $.ajax({
        url: url,
        type: 'POST',
        data: jsonObj,
        headers: {
       	 'Content-Type': 'application/json'
        },
        success: function(response) {
            $('#add-order-item-modal').modal('toggle');
            getOrderList();
            wholeOrder = []
        },
        error: function(e) {
            E = e
            console.log(e.responseJSON)
            
            var error = e.responseJSON.message
            
            error = error.toLowerCase();
            if (error.startsWith("insufficient")) {
                var searchString = "productId: "
                searchString = searchString.toLowerCase();
                startIndex = error.indexOf(searchString) + searchString.length;
                // extract number after productId
                // subs                
                endIndex = error.indexOf(" ", startIndex);
                croppedError = error.substring(startIndex, error.length);

                // extract till first space
                endIndex = croppedError.substring(0, croppedError.indexOf("."));
                productId = croppedError.substring(0, endIndex);
                // convert to integer
                productId = parseInt(productId);
                console.log(productId); 

                // convert productId to product int
                console.log(productId);
                productId = parseInt(productId);
                console.log(productId);
                // get product name
                var productName = getProductName(productId);
                console.log(productName);

                var newError = e.responseJSON.message.replace("productId: " + productId, "barcode: " + productName);
                alert(newError);
                //alert(error);
            }
        } 
    });

    return false;
}


function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}
function init(){

    
	$('#add-order').click(displayCart);
	$('#add-order-item').click(addOrderItem);
	$('#place-order').click(placeOrder);
	$('#refresh-data').click(getOrderList);

    $('#Order-table').DataTable( {
		"processing": true,
		"serverSide": true,
		"searching": false,
		"lengthMenu": [2,5,10,20, 40, 60, 80, 100],
		"pageLength":10,
		"ajax": {url : getOrderUrl() + "?pageNo=0&pageSize=10"},
		"columns": [
            { "data": "id" },
            { "data": "time" },
			{
				"data":null,
				"render":function(o){return '<button onclick="displayEditBrand(' + o.id + ')">edit</button>'}
			}
        ]
	});

}

$(document).ready(init);
$(document).ready(getOrderItemList)
//$(document).ready(getOrderList)
//$(document).ready(getProductList)