package com.increff.pos.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.increff.pos.model.data.DaySalesData;
import com.increff.pos.model.data.OrderItemData;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.config.PropertiesTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dao.UserDao;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.Role;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.UserPojo;

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
    private DaySalesDao daySalesDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private PropertiesTest Properties;

    public PropertiesTest getProperties() {
        return Properties;
    }

    public ProductPojo createProductCascade(String barcode, String brand, String category, String name, Double mrp) throws ApiException {
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
        BrandPojo brandPojo = brandDao.selectByMultipleColumns(List.of("brand", "category"), List.of(List.of(brand), List.of(category))).get(0);
        product.setBrandCategory(brandPojo.getId());
        return productDao.insert(product);
    }

    public InventoryPojo createInventoryCascade(String barcode, String brand, String category, String name, Double mrp, Integer quantity) throws ApiException {
        createProductCascade(barcode, brand, category, name, mrp);
        return createInventory(barcode, quantity);
    }
    public InventoryPojo createInventory(String barcode, Integer quantity) throws ApiException {
        InventoryPojo inventory = new InventoryPojo(quantity);
        inventory.setProductId(productDao.selectByColumn("barcode", barcode).getId());
        return inventoryDao.insert(inventory);
    }

    public List<OrderItemPojo> createOrder(List<OrderItemForm> orderItemFormList) throws ApiException{
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order = orderDao.insert(order);

        List<OrderItemPojo> orderItemPojoList = new ArrayList<OrderItemPojo>();
        for(OrderItemForm orderItemForm : orderItemFormList){
            OrderItemPojo orderItemPojo = ConvertUtil.convert(orderItemForm, OrderItemPojo.class);
            orderItemPojo.setProductId(productDao.selectByColumn("barcode", orderItemForm.getBarcode()).getId());
            orderItemPojo.setOrderId(order.getId());
            orderItemPojoList.add(orderItemPojo);
            orderItemDao.insert(orderItemPojo);
        }
        return orderItemPojoList;
    }

    public DaySalesPojo createDaySales(ZonedDateTime date, Integer invoicedOrdersCount, Integer invoicedItemsCount, Double totalRevenue) throws ApiException {
        DaySalesPojo daySales = new DaySalesPojo(date, invoicedOrdersCount, invoicedItemsCount, totalRevenue);
        daySalesDao.insert(daySales);
        return daySales;
    }

    public UserPojo createUser(String username, String password) throws ApiException {
        return createUser(username, password,Role.OPERATOR);
    }

    public UserPojo createUser(String username, String password, Role role) throws ApiException {
        UserPojo user = new UserPojo(username, password, role);
        return userDao.insert(user);
    }

    
    public BrandForm getBrandForm(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public ProductForm getProductForm(String barcode, String brand, String category, String name, Double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }

    public InventoryForm getInventoryForm(String barcode, Integer quantity){
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public SignupForm getSignupForm(String username, String password){
        return new SignupForm(username, password);
    }
    public LoginForm getLoginForm(String username, String password){
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
        assertEquals(expectedMessage, actualMessage);
    }

    public void checkEquals(DaySalesPojo daySales, DaySalesData daySalesData) {
        assertEquals(daySales.getDate().withNano(0), daySalesData.getDate().withNano(0));
        assertEquals(daySales.getInvoicedOrdersCount(), daySalesData.getInvoicedOrdersCount());
        assertEquals(daySales.getInvoicedItemsCount(), daySalesData.getInvoicedItemsCount());
        assertEquals(daySales.getTotalRevenue(), daySalesData.getTotalRevenue());
    }

    public void checkEquals(List<OrderItemData> orderItems, List<OrderItemForm> orderItemFormList) {
        Assert.assertEquals(orderItems.size(), orderItemFormList.size());
        assertTrue(orderItems.size() > 0);
        for (int i = 0; i < orderItems.size(); i++) {
            Assert.assertEquals(orderItems.get(i).getBarcode(), orderItemFormList.get(i).getBarcode());
            Assert.assertEquals(orderItems.get(i).getQuantity(), orderItemFormList.get(i).getQuantity());
            Assert.assertEquals(orderItems.get(i).getSellingPrice(), orderItemFormList.get(i).getSellingPrice());
        }
    }
    public void checkEquals(OrderItemData orderItemData, OrderItemForm orderItemForm) {
        checkEquals(List.of(orderItemData), List.of(orderItemForm));
    }
    
}
