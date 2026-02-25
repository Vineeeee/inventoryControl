package com.iventory.inventorycontrol.controller;


import com.iventory.inventorycontrol.entity.RawMaterial;
import com.iventory.inventorycontrol.service.RawMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/raw-materials")
@CrossOrigin(origins = "*")
public class RawMaterialController {

    @Autowired
    private RawMaterialService rawMaterialService;

    // ===== CRUD BÁSICO =====

    // Criar nova matéria-prima
    @PostMapping
    public ResponseEntity<?> createRawMaterial(@RequestBody RawMaterial rawMaterial) {
        try {
            RawMaterial created = rawMaterialService.createRawMaterial(rawMaterial);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Listar todas as matérias-primas
    @GetMapping
    public ResponseEntity<List<RawMaterial>> getAllRawMaterials() {
        List<RawMaterial> materials = rawMaterialService.getAllRawMaterials();
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    // Buscar matéria-prima por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRawMaterialById(@PathVariable Long id) {
        Optional<RawMaterial> material = rawMaterialService.getRawMaterialById(id);
        if (material.isPresent()) {
            return new ResponseEntity<>(material.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Matéria-prima não encontrada com id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // Buscar matéria-prima por código
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getRawMaterialByCode(@PathVariable String code) {
        Optional<RawMaterial> material = rawMaterialService.getRawMaterialByCode(code);
        if (material.isPresent()) {
            return new ResponseEntity<>(material.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Matéria-prima não encontrada com código: " + code, HttpStatus.NOT_FOUND);
        }
    }

    // Atualizar matéria-prima
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRawMaterial(@PathVariable Long id, @RequestBody RawMaterial rawMaterial) {
        try {
            RawMaterial updated = rawMaterialService.updateRawMaterial(id, rawMaterial);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Deletar matéria-prima
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRawMaterial(@PathVariable Long id) {
        try {
            rawMaterialService.deleteRawMaterial(id);
            return new ResponseEntity<>("Matéria-prima deletada com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ===== GERENCIAMENTO DE ESTOQUE =====

    // Adicionar estoque
    @PatchMapping("/{id}/stock/add")
    public ResponseEntity<?> addStock(@PathVariable Long id, @RequestParam BigDecimal quantity) {
        try {
            RawMaterial updated = rawMaterialService.addStock(id, quantity);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Remover estoque
    @PatchMapping("/{id}/stock/remove")
    public ResponseEntity<?> removeStock(@PathVariable Long id, @RequestParam BigDecimal quantity) {
        try {
            RawMaterial updated = rawMaterialService.removeStock(id, quantity);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Atualizar estoque (definir valor exato)
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam BigDecimal quantity) {
        try {
            RawMaterial updated = rawMaterialService.updateStock(id, quantity);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ===== MÉTODOS DE BUSCA =====

    // Buscar matérias-primas por nome
    @GetMapping("/search")
    public ResponseEntity<List<RawMaterial>> searchRawMaterials(@RequestParam String name) {
        List<RawMaterial> materials = rawMaterialService.searchRawMaterialsByName(name);
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    // Listar apenas matérias-primas com estoque
    @GetMapping("/with-stock")
    public ResponseEntity<List<RawMaterial>> getRawMaterialsWithStock() {
        List<RawMaterial> materials = rawMaterialService.getRawMaterialsWithStock();
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    // Verificar estoque mínimo
    @GetMapping("/{id}/has-minimum")
    public ResponseEntity<?> hasMinimumStock(@PathVariable Long id, @RequestParam BigDecimal minimum) {
        try {
            boolean hasMinimum = rawMaterialService.hasMinimumStock(id, minimum);
            return new ResponseEntity<>(hasMinimum, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
