package diotviet.server.services;

import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.repositories.CategoryRepository;
import diotviet.server.repositories.GroupRepository;
import diotviet.server.repositories.ProductRepository;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LegacyImportService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * Category repository
     */
    @Autowired
    private CategoryRepository categoryRepository;
    /**
     * Group repository
     */
    @Autowired
    private GroupRepository groupRepository;

    // ****************************
    // Cache
    // ****************************

    /**
     * Cache category
     */
    private HashMap<String, Category> categoryMap;
    /**
     * Cache Group
     */
    private HashMap<String, Group> groupMap;

    // ****************************
    // Public Prep API
    // ****************************

    /**
     * Prepare to import Product
     *
     * @return
     */
    public List<Product> prepImportProduct() {
        // Cache category
        categoryMap = new HashMap<>();
        for (Category category : categoryRepository.findAll()) {
            categoryMap.put(category.getName(), category);
        }
        // Cache group map
        groupMap = new HashMap<>();
        for (Group group : groupRepository.findAll()) {
            groupMap.put(group.getName(), group);
        }

        return new ArrayList<>();
    }

    // ****************************
    // Public Convert API
    // ****************************

    public Product convertToProduct(Row row) {
        // Create output
        Product product = new Product();

        // Set basic data
        product.setTitle(row.getCell(3).getRawValue());
        product.setOriginalPrice(row.getCell(5).getRawValue().replaceAll(",|\\.\\d*", ""));
        product.setActualPrice(product.getOriginalPrice());
        product.setDiscount("0");
        product.setDiscountUnit("%");
        product.setDescription("");
        product.setMeasureUnit(Objects.isNull(row.getCell(12).getValue()) ? "" : row.getCell(12).getRawValue());
        product.setSrc(Objects.isNull(row.getCell(15).getValue()) ? "" : row.getCell(12).getRawValue());
        product.setWeight("0");
        product.setCanBeAccumulated(row.getCell(17).getRawValue().equals("1"));
        product.setIsInBusiness(row.getCell(18).getRawValue().equals("1"));
        product.setCategory(categoryMap.get(row.getCell(0).getRawValue()));
        product.setGroups(new HashSet<>(List.of(new Group[]{groupMap.get(row.getCell(1).getRawValue())})));

        // Return
        return product;
    }

    // ****************************
    // Public SQL API
    // ****************************

    /**
     * Import Product
     *
     * @param products
     */
    @Transactional
    public void importProduct(List<Product> products) {
        // Bulk insert
        productRepository.saveAll(products);
    }
}
