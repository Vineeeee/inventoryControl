package com.iventory.inventorycontrol.service;


import com.iventory.inventorycontrol.entity.RawMaterial;
import com.iventory.inventorycontrol.repository.RawMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RawMaterialService {

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    // ===== CRUD BÁSICO =====

    // Criar nova matéria-prima
    @Transactional
    public RawMaterial createRawMaterial(RawMaterial rawMaterial) {
        // Verifica se já existe matéria-prima com o mesmo código
        if (rawMaterialRepository.existsByCode(rawMaterial.getCode())) {
            throw new RuntimeException("Já existe uma matéria-prima com o código: " + rawMaterial.getCode());
        }

        // Se quantidade não foi informada, inicializa como zero
        if (rawMaterial.getStockQuantity() == null) {
            rawMaterial.setStockQuantity(BigDecimal.ZERO);
        }

        return rawMaterialRepository.save(rawMaterial);
    }

    // Listar todas as matérias-primas
    public List<RawMaterial> getAllRawMaterials() {
        return rawMaterialRepository.findAll();
    }

    // Buscar matéria-prima por ID
    public Optional<RawMaterial> getRawMaterialById(Long id) {
        return rawMaterialRepository.findById(id);
    }

    // Buscar matéria-prima por código
    public Optional<RawMaterial> getRawMaterialByCode(String code) {
        return rawMaterialRepository.findByCode(code);
    }

    // Atualizar matéria-prima
    @Transactional
    public RawMaterial updateRawMaterial(Long id, RawMaterial materialDetails) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada com id: " + id));

        rawMaterial.setName(materialDetails.getName());

        // Atualiza código se fornecido
        if (materialDetails.getCode() != null && !materialDetails.getCode().isEmpty()) {
            Optional<RawMaterial> existing = rawMaterialRepository.findByCode(materialDetails.getCode());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new RuntimeException("Já existe uma matéria-prima com o código: " + materialDetails.getCode());
            }
            rawMaterial.setCode(materialDetails.getCode());
        }

        return rawMaterialRepository.save(rawMaterial);
    }

    // Deletar matéria-prima
    @Transactional
    public void deleteRawMaterial(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada com id: " + id));

        // Verifica se está sendo usada por algum produto
        if (!rawMaterial.getProducts().isEmpty()) {
            throw new RuntimeException("Não é possível deletar: matéria-prima está associada a produtos");
        }

        rawMaterialRepository.delete(rawMaterial);
    }

    // ===== GERENCIAMENTO DE ESTOQUE =====

    // Adicionar estoque
    @Transactional
    public RawMaterial addStock(Long id, BigDecimal quantity) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        BigDecimal newStock = rawMaterial.getStockQuantity().add(quantity);
        rawMaterial.setStockQuantity(newStock);

        return rawMaterialRepository.save(rawMaterial);
    }

    // Remover estoque
    @Transactional
    public RawMaterial removeStock(Long id, BigDecimal quantity) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        if (rawMaterial.getStockQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " +
                    rawMaterial.getStockQuantity() + ", Solicitado: " + quantity);
        }

        BigDecimal newStock = rawMaterial.getStockQuantity().subtract(quantity);
        rawMaterial.setStockQuantity(newStock);

        return rawMaterialRepository.save(rawMaterial);
    }

    // Atualizar estoque (definir valor exato)
    @Transactional
    public RawMaterial updateStock(Long id, BigDecimal newQuantity) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Estoque não pode ser negativo");
        }

        rawMaterial.setStockQuantity(newQuantity);
        return rawMaterialRepository.save(rawMaterial);
    }

    // ===== MÉTODOS DE BUSCA =====

    // Buscar matérias-primas por nome
    public List<RawMaterial> searchRawMaterialsByName(String name) {
        return rawMaterialRepository.findByNameContainingIgnoreCase(name);
    }

    // Listar apenas matérias-primas com estoque
    public List<RawMaterial> getRawMaterialsWithStock() {
        return rawMaterialRepository.findAllWithStock();
    }

    // Verificar estoque mínimo
    public boolean hasMinimumStock(Long id, BigDecimal minimum) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria-prima não encontrada"));

        return rawMaterial.getStockQuantity().compareTo(minimum) >= 0;
    }
}
