package org.fidoshenyata;

import org.fidoshenyata.db.DAO.Impl.CategoryDao;
import org.fidoshenyata.db.DAO.Impl.ProductDao;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;
import org.fidoshenyata.service.CategoryService;
import org.fidoshenyata.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class DbTest {

    private CategoryService categoryService;
    private ProductService productService;

    @BeforeClass
    public static void initDB() throws Exception {
        CategoryService categoryService = new CategoryService(new CategoryDao());
        ProductService productService = new ProductService(new ProductDao());

        productService.deleteAllEntities();
        categoryService.deleteAllEntities();

        initTest(categoryService, productService);
    }

    private static void initTest(CategoryService categoryService, ProductService productService) throws Exception {
        // to guarantee nothing else in the DB

        Category[] categories = new Category[]{
                new Category(101, "_C1", null),
                new Category(102, "_C2", null),
                new Category(103, "_C3", null),
                new Category(104, "_C4", null),
        };
        Product[] products = new Product[]{
                new Product(101, "_P1", "PC1", null, 2, new BigDecimal("2.00"), 101),
                new Product(102, "_P2", "PC1", null, 1, new BigDecimal("5.00"), 101),
                new Product(103, "_P3", "PC2", null, 2, new BigDecimal("2.00"), 102),
                new Product(104, "_P4", "PC2", null, 1, new BigDecimal("7.00"), 102),
                new Product(105, "_P5", "PC3", null, 2, new BigDecimal("4.00"), 103),
                new Product(106, "_P6", "PC3", null, 1, new BigDecimal("6.00"), 103),
                new Product(107, "_P7", "PC4", null, 2, new BigDecimal("3.00"), 104),
                new Product(108, "_P8", "PC4", null, 1, new BigDecimal("5.00"), 104)
        };

        // fill DB
        for (Category category : categories) {
            Assert.assertTrue(categoryService.addCategory(category));
        }
        for (Product product : products) {
            Assert.assertTrue(productService.addProduct(product));
        }

        // getCategories
        List<Category> fetchedCategories = categoryService.getCategories(new PagingInfo(0, 4));
        Collections.reverse(fetchedCategories);
        Assert.assertArrayEquals(categories, fetchedCategories.toArray());

        // getProducts
        List<Product> fetchedProducts = productService.getProducts(new PagingInfo(0, 8));
        Collections.reverse(fetchedProducts);
        Assert.assertArrayEquals(products, fetchedProducts.toArray());

        // getCategoriesByName
        List<Category> fetchedCategoriesByName = categoryService.getCategoriesByName("C");
        Assert.assertArrayEquals(categories, fetchedCategoriesByName.toArray());

        // getProductsByName
        List<Product> fetchedProductsByName = productService.getProductsByName("P");
        Assert.assertArrayEquals(products, fetchedProductsByName.toArray());

        // overall cost
        BigDecimal cost = productService.getCost();
        Assert.assertEquals(new BigDecimal("45.00"), cost);
    }

    @Before
    public void setUP() {
        categoryService = new CategoryService(new CategoryDao());
        productService = new ProductService(new ProductDao());
    }

    @Test
    public void testCategoryCRUD() throws Exception {
        final int categoryId = 1;

        // insert
        Category category = new Category(categoryId, "Category 1.1", "Category Description 1.1");
        Assert.assertTrue(categoryService.addCategory(category));

        // get
        Category fetched1 = categoryService.getCategory(categoryId);
        Assert.assertEquals(category, fetched1);

        // update
        category = new Category(categoryId, "Category 1.1 New", "Category Description 1.1 New");
        Assert.assertTrue(categoryService.updateCategory(category));

        // get
        Category fetched2 = categoryService.getCategory(categoryId);
        Assert.assertEquals(category, fetched2);

        // delete
        Assert.assertTrue(categoryService.deleteCategory(categoryId));
        Assert.assertNull(categoryService.getCategory(categoryId));
    }

    @Test
    public void testProductCRUD() throws Exception {
        final int categoryId = 2;
        final int productId = 1;

        // insert category
        Category category = new Category(categoryId, "Category 2.1", "Category Description 2.1");
        Assert.assertTrue(categoryService.addCategory(category));

        // insert
        Product product = new Product(productId, "Product 2.1", "Producer 2.1",
                null, 3, new BigDecimal("1.50"), categoryId);
        Assert.assertTrue(productService.addProduct(product));

        // get
        Product fetched1 = productService.getProduct(productId);
        Assert.assertEquals(product, fetched1);

        // update
        product = new Product(productId, "Product 2.1 New", "Producer 2.1 New",
                null, 5, new BigDecimal("10.50"), categoryId);
        Assert.assertTrue(productService.updateProduct(product));

        // get
        Product fetched2 = productService.getProduct(productId);
        Assert.assertEquals(product, fetched2);

        // delete
        Assert.assertTrue(productService.deleteEntity(productId));
        Assert.assertNull(productService.getProduct(productId));
    }

    @Test(expected = NameAlreadyTakenException.class)
    public void testCategorySameName() throws Exception {
        final int categoryId1 = 3;
        final int categoryId2 = 4;

        // insert category1
        Category category1 = new Category(categoryId1, "Category 3.1", null);
        Assert.assertTrue(categoryService.addCategory(category1));

        // try to insert category2
        Category category2 = new Category(categoryId2, "Category 3.1", null);
        categoryService.addCategory(category2);
    }

    @Test(expected = NameAlreadyTakenException.class)
    public void testProductSameName() throws Exception {
        final int categoryId = 5;

        final int productId1 = 3;
        final int productId2 = 4;

        // insert category
        Category category = new Category(categoryId, "Category 4.1", null);
        Assert.assertTrue(categoryService.addCategory(category));

        // insert product1
        Product product1 = new Product(productId1, "Product 4.1", "Producer 4.1",
                null, 5, new BigDecimal("10.50"), categoryId);
        Assert.assertTrue(productService.addProduct(product1));

        // try to insert product2
        Product product2 = new Product(productId2, "Product 4.1", "Producer 4.1",
                null, 5, new BigDecimal("10.50"), categoryId);
        productService.addProduct(product2);
    }

    @Test
    public void testProductsByCategory() throws Exception {
        final int categoryId = 6;
        final int productId1 = 5;
        final int productId2 = 6;

        // insert category
        Category category = new Category(categoryId, "Category 5.1", null);
        Assert.assertTrue(categoryService.addCategory(category));

        // insert
        Product[] products = new Product[]{
                new Product(productId1, "Product 5.1", "PC",
                        null, 3, new BigDecimal("10.00"), categoryId),
                new Product(productId2, "Product 5.2", "PC",
                        null, 5, new BigDecimal("20.00"), categoryId)
        };
        for (Product product : products) {
            Assert.assertTrue(productService.addProduct(product));
        }

        // cost by category
        BigDecimal cost = productService.getCost(categoryId);
        Assert.assertEquals(new BigDecimal("130.00"), cost);

        // products by category
        List<Product> fetchedProducts =
                productService.getProducts(categoryId, new PagingInfo(0, 2));
        Collections.reverse(fetchedProducts);
        Assert.assertArrayEquals(products, fetchedProducts.toArray());

        // products by name by category
        List<Product> fetchedProductsByName =
                productService.getProductsByName(categoryId, "uct");
        Assert.assertArrayEquals(products, fetchedProductsByName.toArray());
    }
}
