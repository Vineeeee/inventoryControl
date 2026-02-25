package com.iventory.inventorycontrol.service;


import com.iventory.inventorycontrol.entity.Product;
import com.iventory.inventorycontrol.entity.ProductRawMaterial;
import com.iventory.inventorycontrol.entity.RawMaterial;
import com.iventory.inventorycontrol.repository.ProductRawMaterialRepository;
import com.iventory.inventorycontrol.repository.ProductRepository;
import com.iventory.inventorycontrol.repository.RawMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private ProductRawMaterialRepository productRawMaterialRepository;

    // ===== CRUD BÁSICO =====

    // Criar um novo produto
    @Transactional
    public Product createProduct(Product product) {
        // Verifica se já existe produto com o mesmo código
        if (productRepository.existsByCode(product.getCode())) {
            throw new RuntimeException("Já existe um produto com o código: " + product.getCode());
        }
        return productRepository.save(product);
    }

    // Listar todos os produtos
    public List<Product> getAllProducts() {
        return productRepository.findAllOrderByPriceDesc();
    }

    // Buscar produto por ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Buscar produto por código
    public Optional<Product> getProductByCode(String code) {
        return productRepository.findByCode(code);
    }

    // Atualizar produto
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());

        // Não atualiza o código se ele veio nulo ou vazio
        if (productDetails.getCode() != null && !productDetails.getCode().isEmpty()) {
            // Verifica se o novo código já não está em uso por outro produto
            Optional<Product> existingProduct = productRepository.findByCode(productDetails.getCode());
            if (existingProduct.isPresent() && !existingProduct.get().getId().equals(id)) {
                throw new RuntimeException("Já existe um produto com o código: " + productDetails.getCode());
            }
            product.setCode(productDetails.getCode());
        }

        return productRepository.save(product);
    }

    // Deletar produto
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        productRepository.delete(product);
    }

    // ===== ASSOCIAÇÃO COM MATÉRIAS-PRIMAS =====

    // Adicionar matéria-prima a um produto
    @Transactional
    public ProductRawMaterial addRawMaterialToProduct(Long productId, Long rawMaterialId, BigDecimal quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        // Verifica se a associação já existe
        Optional<ProductRawMaterial> existing = productRawMaterialRepository
                .findByProductAndRawMaterial(product, rawMaterial);

        if (existing.isPresent()) {
            throw new RuntimeException("Esta matéria-prima já está associada ao produto");
        }

        // Cria nova associação
        ProductRawMaterial productRawMaterial = new ProductRawMaterial(product, rawMaterial, quantity);
        return productRawMaterialRepository.save(productRawMaterial);
    }

    // Atualizar quantidade de matéria-prima em um produto
    @Transactional
    public ProductRawMaterial updateRawMaterialQuantity(Long productId, Long rawMaterialId, BigDecimal newQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        ProductRawMaterial productRawMaterial = productRawMaterialRepository
                .findByProductAndRawMaterial(product, rawMaterial)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada"));

        productRawMaterial.setQuantityRequired(newQuantity);
        return productRawMaterialRepository.save(productRawMaterial);
    }

    // Remover matéria-prima de um produto
    @Transactional
    public void removeRawMaterialFromProduct(Long productId, Long rawMaterialId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        ProductRawMaterial productRawMaterial = productRawMaterialRepository
                .findByProductAndRawMaterial(product, rawMaterial)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada"));

        productRawMaterialRepository.delete(productRawMaterial);
    }

    // Listar todas as matérias-primas de um produto
    public List<ProductRawMaterial> getRawMaterialsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return productRawMaterialRepository.findByProduct(product);
    }

    // ===== MÉTODOS DE BUSCA =====

    // Buscar produtos por nome (parcial)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // Verificar se produto pode ser produzido
    public boolean canProductBeProduced(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<ProductRawMaterial> materials = productRawMaterialRepository.findByProduct(product);

        for (ProductRawMaterial pm : materials) {
            RawMaterial rawMaterial = pm.getRawMaterial();
            if (rawMaterial.getStockQuantity().compareTo(pm.getQuantityRequired()) < 0) {
                return false; // Falta matéria-prima
            }
        }
        return true; // Tem todas as matérias-primas necessárias
    }

    // Calcular quantas unidades podem ser produzidas
    public Integer calculatePossibleProduction(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<ProductRawMaterial> materials = productRawMaterialRepository.findByProduct(product);

        if (materials.isEmpty()) {
            return 0; // Produto não tem matérias-primas cadastradas
        }

        Integer maxUnits = Integer.MAX_VALUE;

        for (ProductRawMaterial pm : materials) {
            RawMaterial rawMaterial = pm.getRawMaterial();
            BigDecimal stock = rawMaterial.getStockQuantity();
            BigDecimal required = pm.getQuantityRequired();

            if (required.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Calcula quantas unidades podem ser feitas com o estoque atual
            int possibleUnits = stock.divide(required, 0, java.math.RoundingMode.DOWN).intValue();

            if (possibleUnits < maxUnits) {
                maxUnits = possibleUnits;
            }
        }

        return maxUnits == Integer.MAX_VALUE ? 0 : maxUnits;
    }
}
