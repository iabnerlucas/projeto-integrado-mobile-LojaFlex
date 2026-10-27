-- ============================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS MYSQL PARA O LOJAFLEX
-- (Baseado no Diagrama de Entidade e Relacionamento)
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_lojaflex CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_lojaflex;

-- 1. TABELA DE CADASTRO DA LOJA / EMPRESA
CREATE TABLE IF NOT EXISTS cadastro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_loja VARCHAR(150) NOT NULL,
    nome_dono VARCHAR(150) NOT NULL,
    celular_whatsapp VARCHAR(20) NOT NULL,
    cpf_cnpj VARCHAR(18),
    email_contato VARCHAR(150),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABELA DE LOGIN / CREDENCIAIS DO ADMIN DA LOJA
CREATE TABLE IF NOT EXISTS login (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cadastro_id BIGINT NOT NULL,
    email_admin VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    FOREIGN KEY (cadastro_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 3. TABELA DE USUÁRIOS DE OPERAÇÃO
CREATE TABLE IF NOT EXISTS tb_usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    perfil VARCHAR(20) NOT NULL, -- SUPER_ADMIN, GERENTE, CAIXA
    empresa_id BIGINT,
    FOREIGN KEY (empresa_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 4. TABELA DE PRODUTOS / ESTOQUE
CREATE TABLE IF NOT EXISTS tb_produto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    preco_custo DECIMAL(10,2) DEFAULT 0.00,
    codigo_barra VARCHAR(50),
    quantidade_estoque INT NOT NULL DEFAULT 0,
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (empresa_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 5. TABELA DE FORNECEDORES
CREATE TABLE IF NOT EXISTS tb_fornecedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(150) NOT NULL,
    razao_social VARCHAR(150),
    cnpj VARCHAR(20),
    telefone VARCHAR(20),
    email VARCHAR(100),
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (empresa_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 6. TABELA DE CLIENTES DE FIADO / CADERNETA
CREATE TABLE IF NOT EXISTS tb_cliente_fiado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_cliente VARCHAR(150) NOT NULL,
    cpf_cliente VARCHAR(20),
    telefone_whatsapp VARCHAR(20) NOT NULL,
    foto_cliente_uri TEXT,
    limite_credito DECIMAL(10,2) NOT NULL DEFAULT 200.00,
    valor_devido DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    itens_comprados TEXT,
    data_compra VARCHAR(20),
    hora_compra VARCHAR(20),
    data_vencimento VARCHAR(20),
    pago BOOLEAN DEFAULT FALSE,
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (empresa_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 7. TABELA DE CONTAS A PAGAR
CREATE TABLE IF NOT EXISTS tb_conta_pagar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(200) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_vencimento VARCHAR(20) NOT NULL,
    paga BOOLEAN DEFAULT FALSE,
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (empresa_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 8. TABELA DE PEDIDOS / VENDAS DO PDV
CREATE TABLE IF NOT EXISTS tb_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    forma_pagamento VARCHAR(50) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    valor_recebido DECIMAL(10,2) NOT NULL,
    troco DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (empresa_id) REFERENCES cadastro(id) ON DELETE CASCADE
);

-- 9. ITENS DO PEDIDO
CREATE TABLE IF NOT EXISTS tb_item_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES tb_produto(id)
);
