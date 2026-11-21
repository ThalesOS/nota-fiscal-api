package com.notafiscalapi.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AjusteStatusNotaFiscalSchema implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public AjusteStatusNotaFiscalSchema(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("ALTER TABLE IF EXISTS nota_fiscal DROP CONSTRAINT IF EXISTS nota_fiscal_status_check");
    }
}
