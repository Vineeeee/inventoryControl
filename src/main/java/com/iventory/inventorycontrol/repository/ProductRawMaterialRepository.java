package com.iventory.inventorycontrol.repository;

import com.iventory.inventorycontrol.entity.Product;
import com.iventory.inventorycontrol.entity.ProductRawMaterial;
import com.iventory.inventorycontrol.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRawMaterialRepository extends JpaRepository<ProductRawMaterial, Long> {

    List<ProductRawMaterial> findByProduct(Product product);

    List<ProductRawMaterial> findByRawMaterial(RawMaterial rawMaterial);

    Optional<ProductRawMaterial> findByProductAndRawMaterial(Product product, RawMaterial rawMaterial);

    @Query("SELECT prm FROM ProductRawMaterial prm WHERE prm.product.id = :productId")
    List<ProductRawMaterial> findByProductId(@Param("productId") Long productId);

    @Query("SELECT prm FROM ProductRawMaterial prm WHERE prm.rawMaterial.id = :rawMaterialId")
    List<ProductRawMaterial> findByRawMaterialId(@Param("rawMaterialId") Long rawMaterialId);
}