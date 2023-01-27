package com.increff.pos.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.ConvertUtil;

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
    @Autowired
    BrandDao bDao;
    @Autowired
    ProductDao pDao;
    @Autowired
    InventoryDao iDao;

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
        product.setBrandCategory(brandService.getCheckBrandCategory(brand, category).getId());
        return productDao.insert(product);
    }

    public static InventoryPojo createInventory(String barcode, String brand, String category, String name, Double mrp, Integer quantity) throws ApiException {
        createProductWithBrand(barcode, brand, category, name, mrp);
        return createInventorySingle(barcode, quantity);
    }
    public static InventoryPojo createInventorySingle(String barcode, Integer quantity) throws ApiException {
        InventoryPojo inventory = new InventoryPojo(quantity);
        inventory.setId(productService.getByBarcode(barcode).getId());
        return inventoryDao.insert(inventory);
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
}
