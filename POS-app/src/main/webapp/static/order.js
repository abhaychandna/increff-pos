var wholeOrder = [];

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

function displayOrderItemList(){
	var $tbody = $('#order-item-table').find('tbody');
	$tbody.empty();
	for(var i in wholeOrder) {
        var rowNo = parseInt(i)+1; 
        var buttonHtml = '<button onclick="deleteOrderItem('+i+')" class="btn btn-danger">Delete</button>';
        var row = '<tr>'
            + '<td>' + rowNo + '</td>'
            + '<td>' + wholeOrder[i].barcode + '</td>'
            + '<td>'  + wholeOrder[i].quantity + '</td>'
            + '<td>'  + wholeOrder[i].sellingPrice + '</td>'
            + '<td>'  + buttonHtml + '</td>'
            + '</tr>';

        $tbody.append(row);
    }

}

function displayOrderItemViewList(data){
    var $title = $('#order-view-title');
    $title.html("Order ID : " + data[0].orderId);

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

function addInCart(inputJson) {
    var barcode = inputJson["barcode"];
    var quantity = inputJson["quantity"];
    var url = getInventoryUrl() + "/barcode/" + barcode;
    $.ajax({
        async: false,
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            if(response["quantity"] < quantity) {
                var errorString = "Not enough quantity in inventory. Available: " + response["quantity"];
                raiseAlert({
                    icon: 'error',
                    title: 'Oops...',
                    html: errorString,
                })
                return;
            }

            var index = itemInCart(barcode);
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
    displayOrderItemList();

}

function itemInCart(barcode) {
    for(i in wholeOrder) {
        var existingBarcode = wholeOrder[i].barcode;
        if(barcode == existingBarcode) {
            return i;
        }
    }
    return -1;
}

function addOrderItem(event) {
    var $form = $("#order-item-form");
    if(validateFormHTML($form) == false)return;
    var json = toJson($form);  
    normalizeJson(json);
    addInCart(json);
    
}

function displayCart() {
    $('#add-order-item-modal').modal('toggle');
    
    var $tbody = $('#order-item-table').find('tbody');
    $tbody.empty();
    wholeOrder = [];
}

function displayOrderDetails(id) {
    $('#view-order-modal').modal('toggle');
    var url = getOrderUrl();
    
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

    var jsonStr = JSON.stringify(wholeOrder);
    
    $.ajax({
        url: url,
        type: 'POST',
        data: jsonStr,
        headers: {
       	 'Content-Type': 'application/json'
        },
        success: function(response) {
            $('#add-order-item-modal').modal('toggle');
            raiseAlert({
                title: 'Order Placed!',
                text: 'Congratulations! Your order has been placed.',
                icon: 'success',
              });
            getOrderList();
            wholeOrder = []
        },
        error: function(e) {
            var error = e.responseJSON.message
            
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
