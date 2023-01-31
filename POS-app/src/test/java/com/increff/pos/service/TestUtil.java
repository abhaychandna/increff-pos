package com.increff.pos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.UserDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.LoginForm;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.Role;
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

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private UserDao userDao;

    public ProductPojo createProductWithBrand(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
        createBrand(brand, category);
        ProductPojo p = createProduct(barcode, brand, category, name, mrp);
        return p;
    }

    public BrandData createBrand(String brand, String category) throws ApiException{
        BrandPojo brandPojo = new BrandPojo(brand, category);
        brandPojo = brandDao.insert(brandPojo);
        BrandData brandData = ConvertUtil.convert(brandPojo, BrandData.class);
        return brandData;
    }

    public ProductPojo createProduct(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
        ProductPojo product = new ProductPojo(barcode, null, name, mrp);
        BrandPojo brandPojo = brandDao.selectByMultipleColumns(BrandPojo.class, List.of("brand", "category"), List.of(List.of(brand), List.of(category))).get(0);
        product.setBrandCategory(brandPojo.getId());
        return productDao.insert(product);
    }

    public InventoryPojo createInventory(String barcode, String brand, String category, String name, Double mrp, Integer quantity) throws ApiException {
        createProductWithBrand(barcode, brand, category, name, mrp);
        return createInventorySingle(barcode, quantity);
    }
    public InventoryPojo createInventorySingle(String barcode, Integer quantity) throws ApiException {
        InventoryPojo inventory = new InventoryPojo(quantity);
        inventory.setId(productDao.selectByColumn(ProductPojo.class, "barcode", barcode).getId());
        return inventoryDao.insert(inventory);
    }

    public List<OrderItemPojo> createOrder(List<OrderItemForm> orderItemFormList) throws ApiException{
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

    public UserPojo createUser(String username, String password) throws ApiException {
        return createUser(username, password,Role.operator);
    }

    public UserPojo createUser(String username, String password, Role role) throws ApiException {
        UserPojo user = new UserPojo(username, password, role);
        return userDao.insert(user);
    }

    
    public BrandForm getBrandFormDto(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public ProductForm getProductFormDto(String barcode, String brand, String category, String name, Double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }

    public InventoryForm getInventoryFormDto(String barcode, Integer quantity){
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public SignupForm getSignupFormDto(String username, String password){
        return new SignupForm(username, password);
    }
    public LoginForm getLoginFormDto(String username, String password){
        return new LoginForm(username, password);
    }



    public String createFieldEmptyErrorMessage(String field) {
        return createErrorMessage(field + " may not be empty");
    }
    public String createErrorMessage(String message) {
        return PreProcessingUtil.getErrorStartMessage() + message + PreProcessingUtil.getErrorMessageSeparator();
    }
    public void validateExceptionMessage(Exception exception, String expectedMessage) {
        String actualMessage = exception.getMessage();
        System.out.println(expectedMessage);
        System.out.println(actualMessage);

        assertEquals(expectedMessage, actualMessage);
    }
    
}
