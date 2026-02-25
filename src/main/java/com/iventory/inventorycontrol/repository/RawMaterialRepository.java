package com.iventory.inventorycontrol.repository;

import com.iventory.inventorycontrol.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Optional<RawMaterial> findByCode(String code);

    List<RawMaterial> findByNameContainingIgnoreCase(String name);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stockQuantity > 0")
    List<RawMaterial> findAllWithStock();

    boolean existsByCode(String code);
}