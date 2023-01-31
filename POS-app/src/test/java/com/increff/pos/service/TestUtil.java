package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.UserDao;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.SignupForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.PreProcessingUtil;

@Component
public class TestUtil {

    private static ProductDto productDto;
    private static InventoryDto inventoryDto;
    @Autowired
    BrandDto bDto;
    @Autowired
    ProductDto pDto;
    @Autowired
    InventoryDto iDto;

    private static ProductService productService;
    private static BrandService brandService;
    private static InventoryService inventoryService;
    @Autowired
    BrandService bSer;
    @Autowired
    InventoryService iSer;
    @Autowired
    ProductService pSer;

    private static BrandDao brandDao;
    private static ProductDao productDao;
    private static InventoryDao inventoryDao;
    private static OrderDao orderDao;
    private static OrderItemDao orderItemDao;
    private static UserDao userDao;
    @Autowired
    BrandDao bDao;
    @Autowired
    ProductDao pDao;
    @Autowired
    InventoryDao iDao;
    @Autowired
    OrderDao oDao;
    @Autowired
    OrderItemDao oiDao;
    @Autowired
    UserDao uDao;

	@PostConstruct
	private void init(){
        productDto = this.pDto;
        inventoryDto = this.iDto;
        productService = this.pSer;
        inventoryService = this.iSer;
        brandService = this.bSer;
        brandDao = this.bDao;  
        productDao = this.pDao;
        inventoryDao = this.iDao;   
        orderDao = this.oDao;
        orderItemDao = this.oiDao; 
        userDao = this.uDao;  
	}

    public static ProductPojo createProductWithBrand(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
        createBrand(brand, category);
        ProductPojo p = createProduct(barcode, brand, category, name, mrp);
        return p;
    }

    public static BrandData createBrand(String brand, String category) throws ApiException{
        BrandPojo brandPojo = new BrandPojo(brand, category);
        brandPojo = brandDao.insert(brandPojo);
        BrandData brandData = ConvertUtil.convert(brandPojo, BrandData.class);
        return brandData;
    }

    public static ProductPojo createProduct(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
        ProductPojo product = new ProductPojo(barcode, null, name, mrp);
        BrandPojo brandPojo = brandDao.selectByMultipleColumns(BrandPojo.class, List.of("brand", "category"), List.of(List.of(brand), List.of(category))).get(0);
        product.setBrandCategory(brandPojo.getId());
        return productDao.insert(product);
    }

    public static InventoryPojo createInventory(String barcode, String brand, String category, String name, Double mrp, Integer quantity) throws ApiException {
        createProductWithBrand(barcode, brand, category, name, mrp);
        return createInventorySingle(barcode, quantity);
    }
    public static InventoryPojo createInventorySingle(String barcode, Integer quantity) throws ApiException {
        InventoryPojo inventory = new InventoryPojo(quantity);
        inventory.setId(productDao.selectByColumn(ProductPojo.class, "barcode", barcode).getId());
        return inventoryDao.insert(inventory);
    }

    public static List<OrderItemPojo> createOrder(List<OrderItemForm> orderItemFormList) throws ApiException{
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order = orderDao.insert(order);

        List<OrderItemPojo> orderItemPojoList = new ArrayList<OrderItemPojo>();
        for(OrderItemForm orderItemForm : orderItemFormList){
            OrderItemPojo orderItemPojo = ConvertUtil.convert(orderItemForm, OrderItemPojo.class);
            orderItemPojo.setProductId(productDao.selectByColumn(ProductPojo.class, "barcode", orderItemForm.getBarcode()).getId());
            orderItemPojo.setOrderId(order.getId());
            orderItemPojoList.add(orderItemPojo);
            orderItemDao.insert(orderItemPojo);
        }
        return orderItemPojoList;
    }

    public static BrandForm getBrandFormDto(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public static ProductForm getProductFormDto(String barcode, String brand, String category, String name, Double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }

    public static InventoryForm getInventoryFormDto(String barcode, Integer quantity){
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }


    public static String createFieldEmptyErrorMessage(String field) {
        return createErrorMessage(field + " may not be empty");
    }
    public static String createErrorMessage(String message) {
        return PreProcessingUtil.getErrorStartMessage() + message + PreProcessingUtil.getErrorMessageSeparator();
    }
    public static void validateExceptionMessage(Exception exception, String expectedMessage) {
        String actualMessage = exception.getMessage();
        System.out.println(expectedMessage);
        System.out.println(actualMessage);

        assertEquals(expectedMessage, actualMessage);
    }
    
}
