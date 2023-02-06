var wholeOrder = []
var alertErrorMessages = [];
var maxQuantity = 99999;
var maxSellingPrice = 99999;
// TODO : Remove testing variable from here
var globalData;
var E;

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

function getInventoryUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/inventory";
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
        var rowNo = parseInt(i)+1; 
        var buttonHtml = '<button onclick="deleteOrderItem('+i+')" class="btn btn-danger">Delete</button>';
        var row = '<tr>'
            + '<td>' + rowNo + '</td>'
            + '<td>' + JSON.parse(wholeOrder[i]).barcode + '</td>'
            + '<td>'  + JSON.parse(wholeOrder[i]).quantity + '</td>'
            + '<td>'  + JSON.parse(wholeOrder[i]).sellingPrice + '</td>'
            + '<td>'  + buttonHtml + '</td>'
            + '</tr>';

        $tbody.append(row);
    }

}

function displayOrderItemViewList(data){
	var $tbody = $('#order-view-table').find('tbody');
	$tbody.empty();

    for(var i in data) {
        var e = data[i];
        var rowNo = parseInt(i)+1;  
        var row = '<tr>'
            + '<td>' + rowNo + '</td>'
            + '<td>' + data[i].barcode + '</td>'
            + '<td>'  + data[i].quantity + '</td>'
            + '<td>'  + data[i].sellingPrice + '</td>'
            + '</tr>';

        $tbody.append(row);
    }

}

function validateInventory(barcode, quantity, inputJson) {
    var url = getInventoryUrl() + "/barcode/" + barcode;
    $.ajax({
        async: false,
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            response["quantity"];
            if(response["quantity"] < quantity) {
                var errorString = "Not enough quantity in inventory. Available: " + response["quantity"];
                raiseAlert({
                    icon: 'error',
                    title: 'Oops...',
                    html: errorString,
                })
                return;
            }

            var index = itemInCart();
            if(index != -1) {
                
                wholeOrder[i] = inputJson;
            }
            else{
                wholeOrder.push(inputJson);        
            }
            resetForm();
        },
        error: function(data) {
            errorString = data.responseJSON.message;
            raiseAlert({
                icon: 'error',
                title: 'Oops...',
                html: errorString,
            })
        }
    });
    displayOrderItemList(wholeOrder);

}

function itemInCart() {
    for(i in wholeOrder) {
        var barcode = JSON.parse(wholeOrder[i]).barcode;
        
        var temp_barcode = $("#order-item-form input[name=barcode]").val();
        
        if(temp_barcode == barcode) {
            
            return i;
        }
    }
    return -1;
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

function validateBarcode(barcode){
    if(barcode == ""){
        alertErrorMessages.push("Barcode cannot be empty");
    }
}
function validateQuantity(quantity){
    if(quantity == ""){
        alertErrorMessages.push("Quantity cannot be empty");
    }
    if(quantity <= 0){
        alertErrorMessages.push("Quantity has to be positive");
    }
    if(quantity > maxQuantity){
        alertErrorMessages.push("Quantity cannot be greater than " + maxQuantity);
    }
}
function validateSellingPrice(sellingPrice){
    if(sellingPrice == ""){
        alertErrorMessages.push("Selling Price cannot be empty");
    }
    if(sellingPrice <= 0){
        alertErrorMessages.push("Selling Price has to be positive");
    }
    if(sellingPrice > maxSellingPrice){
        alertErrorMessages.push("Selling Price cannot be greater than " + maxSellingPrice);
    }
}

function addOrderItem(event) {
    var $form = $("#order-item-form");
    if(validateFormHTML($form) == false)return;
    var json = toJson($form);
    var barcode = $("#order-item-form input[name=barcode]").val();
    var quantity = $("#order-item-form input[name=quantity]").val();
    var sp = $("#order-item-form input[name=sellingPrice]").val();
    
    alertErrorMessages = [];
    validateBarcode(barcode);
    validateQuantity(quantity);
    validateSellingPrice(sp);
    if(alertErrorMessages.length > 0) {
        var errorString = alertErrorMessages.join("<br>");
        raiseAlert({
            icon: 'error',
            title: 'Oops...',
            html: errorString,
        })
        return;
    }
    

    validateInventory(barcode, quantity, json);
    
}

function displayCart() {
    $('#add-order-item-modal').modal('toggle');
    // table should be empty
    var $tbody = $('#order-item-table').find('tbody');
    $tbody.empty();
    wholeOrder = [];
}

function displayOrderDetails(id) {
    $('#view-order-modal').modal('toggle');
    var url = getOrderUrl();
    // ajax - Get request to baseUrl/{id}/items
    $.ajax({
        url: url + '/' + id + '/items',
        type: 'GET',
        success: function(data) {
            displayOrderItemViewList(data);
        },
        error: handleAjaxError
     });
}

function getInvoice(id){
    var url = getOrderUrl();
    url = url + '/' + id + '/invoice';
    $.ajax({
        url: url,
        type: 'GET',
        success: function(base64String) {
            
            downloadPDF(base64String, "Invoice_orderId_" + id);
        },
        error: handleAjaxError
     });
}

function getOrderItemList() {
    var jsonObj = $.parseJSON('[' + wholeOrder + ']');
    
}

function displayOrderList(data) {
    
    var $tbody = $('#Order-table').find('tbody');
    $tbody.empty();
    for(var i in data){
        var e = data[i];
        
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

function placeOrder() {
    var url = getOrderItemUrl();

    var jsonObj = arrayToJson();
    
    $.ajax({
        url: url,
        type: 'POST',
        data: jsonObj,
        headers: {
       	 'Content-Type': 'application/json'
        },
        success: function(response) {
            $('#add-order-item-modal').modal('toggle');
            raiseAlert({
                title: 'Order Placed!',
                text: 'Congratulations! Your order has been placed.',
                icon: 'success',
                confirmButtonText: 'Cool'
              });
            getOrderList();
            wholeOrder = []
        },
        error: function(e) {
            E = e
            
            
            var error = e.responseJSON.message
            
            error = error.toLowerCase();
            if (error.startsWith("insufficient")) {
                var searchString = "productId: "
                searchString = searchString.toLowerCase();
                startIndex = error.indexOf(searchString) + searchString.length;
                // extract number after productId
                endIndex = error.indexOf(" ", startIndex);
                croppedError = error.substring(startIndex, error.length);

                // extract till first space
                endIndex = croppedError.substring(0, croppedError.indexOf("."));
                productId = croppedError.substring(0, endIndex);
                // convert to integer
                productId = parseInt(productId);
                

                // convert productId to product int
                
                productId = parseInt(productId);
                
                // get product name
                var productName = getProductName(productId);
                

                var newError = e.responseJSON.message.replace("productId: " + productId, "barcode: " + productName);
                error = newError;
            }
            else{
                error = e.responseJSON.message;
            }
            var errorString = error;
            raiseAlert({
                icon: 'error',
                title: 'Oops...',
                html: errorString,
            })
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
        "ordering": false,
		"lengthMenu": [5, 10, 25, 50, 100],
		"pageLength":10,
		"ajax": {url : getOrderUrl() + "?pageNo=0&pageSize=10"},
		"columns": [
            {
			"data": "id",
			render: function (data, type, row, meta) {
				return meta.row + meta.settings._iDisplayStart + 1;
			}
		},
            { "data": "time",
            "render": function (timestamp) {
                
                var millisecondTimestamp = timestamp * 1000;
                return  new Date(millisecondTimestamp).toLocaleString();
            } },
			{
				"data":null,
				"render":function(o){return '<button class="btn btn-info" onclick="displayOrderDetails(' + o.id + ')">View</button>' + 
                        '&nbsp;&nbsp;' + 
                        '<button class="btn btn-info" onclick="getInvoice(' + o.id + ')">Download Invoice</button>'} 
			}
        ]
	});

}

$(document).ready(init);
$(document).ready(getOrderItemList)