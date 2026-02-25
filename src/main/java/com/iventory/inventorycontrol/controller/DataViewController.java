package com.iventory.inventorycontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DataViewController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/view/products")
    public List<Map<String, Object>> viewProducts() {
        return jdbcTemplate.queryForList("SELECT * FROM products");
    }

    @GetMapping("/view/raw-materials")
    public List<Map<String, Object>> viewRawMaterials() {
        return jdbcTemplate.queryForList("SELECT * FROM raw_materials");
    }

    @GetMapping("/view/associations")
    public List<Map<String, Object>> viewAssociations() {
        return jdbcTemplate.queryForList(
                "SELECT p.name as product, rm.name as raw_material, prm.quantity_required " +
                        "FROM product_raw_materials prm " +
                        "JOIN products p ON prm.product_id = p.id " +
                        "JOIN raw_materials rm ON prm.raw_material_id = rm.id"
        );
    }
}