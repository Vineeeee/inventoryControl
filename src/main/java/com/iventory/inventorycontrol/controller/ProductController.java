package com.iventory.inventorycontrol.controller;

import com.iventory.inventorycontrol.entity.Product;
import com.iventory.inventorycontrol.entity.ProductRawMaterial;
import com.iventory.inventorycontrol.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Permite chamadas do front-end
public class ProductController {

    @Autowired
    private ProductService productService;

    // ===== CRUD BÁSICO =====

    // Criar novo produto
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Listar todos os produtos
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Produto não encontrado com id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // Buscar produto por código
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getProductByCode(@PathVariable String code) {
        Optional<Product> product = productService.getProductByCode(code);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Produto não encontrado com código: " + code, HttpStatus.NOT_FOUND);
        }
    }

    // Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Deletar produto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>("Produto deletado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ===== ASSOCIAÇÃO COM MATÉRIAS-PRIMAS =====

    // Adicionar matéria-prima a um produto
    @PostMapping("/{productId}/raw-materials/{rawMaterialId}")
    public ResponseEntity<?> addRawMaterialToProduct(
            @PathVariable Long productId,
            @PathVariable Long rawMaterialId,
            @RequestParam BigDecimal quantity) {
        try {
            ProductRawMaterial association = productService.addRawMaterialToProduct(
                    productId, rawMaterialId, quantity);
            return new ResponseEntity<>(association, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Atualizar quantidade de matéria-prima em um produto
    @PutMapping("/{productId}/raw-materials/{rawMaterialId}")
    public ResponseEntity<?> updateRawMaterialQuantity(
            @PathVariable Long productId,
            @PathVariable Long rawMaterialId,
            @RequestParam BigDecimal quantity) {
        try {
            ProductRawMaterial association = productService.updateRawMaterialQuantity(
                    productId, rawMaterialId, quantity);
            return new ResponseEntity<>(association, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Remover matéria-prima de um produto
    @DeleteMapping("/{productId}/raw-materials/{rawMaterialId}")
    public ResponseEntity<?> removeRawMaterialFromProduct(
            @PathVariable Long productId,
            @PathVariable Long rawMaterialId) {
        try {
            productService.removeRawMaterialFromProduct(productId, rawMaterialId);
            return new ResponseEntity<>("Matéria-prima removida do produto com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Listar matérias-primas de um produto
    @GetMapping("/{productId}/raw-materials")
    public ResponseEntity<?> getRawMaterialsByProduct(@PathVariable Long productId) {
        try {
            List<ProductRawMaterial> materials = productService.getRawMaterialsByProduct(productId);
            return new ResponseEntity<>(materials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ===== MÉTODOS DE BUSCA =====

    // Buscar produtos por nome
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Verificar se produto pode ser produzido
    @GetMapping("/{id}/can-produce")
    public ResponseEntity<?> canProductBeProduced(@PathVariable Long id) {
        try {
            boolean canProduce = productService.canProductBeProduced(id);
            return new ResponseEntity<>(canProduce, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Calcular quantas unidades podem ser produzidas
    @GetMapping("/{id}/possible-production")
    public ResponseEntity<?> calculatePossibleProduction(@PathVariable Long id) {
        try {
            Integer quantity = productService.calculatePossibleProduction(id);
            return new ResponseEntity<>(quantity, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
