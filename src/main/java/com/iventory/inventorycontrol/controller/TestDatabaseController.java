package com.iventory.inventorycontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestDatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-database")
    public String testDatabase() {
        try {
            // Testa se consegue conectar e executar uma query simples
            String result = jdbcTemplate.queryForObject("SELECT 'Conexão OK!' as resultado", String.class);
            return "✅ Banco de dados conectado! " + result;
        } catch (Exception e) {
            return "❌ Erro de conexão: " + e.getMessage();
        }
    }

    @GetMapping("/test-tables")
    public String testTables() {
        try {
            // Lista todas as tabelas do banco
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SHOW TABLES"
            );

            StringBuilder sb = new StringBuilder("📊 Tabelas encontradas:\n");
            for (Map<String, Object> table : tables) {
                sb.append("- ").append(table.values()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Erro ao listar tabelas: " + e.getMessage();
        }
    }
}