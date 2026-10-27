package com.lojaflex.backend.config

import com.lojaflex.backend.model.*
import com.lojaflex.backend.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val empresaRepository: EmpresaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val produtoRepository: ProdutoRepository,
    private val fornecedorRepository: FornecedorRepository,
    private val clienteFiadoRepository: ClienteFiadoRepository,
    private val contaPagarRepository: ContaPagarRepository,
    private val solicitacaoEmpresaRepository: SolicitacaoEmpresaRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // 1. Cadastrar Usuário Super Admin do Suporte
        if (usuarioRepository.findByEmail("suporte@lojaflex.com.br") == null) {
            usuarioRepository.save(
                Usuario(
                    nome = "Suporte LojaFlex",
                    email = "suporte@lojaflex.com.br",
                    senha = "admin",
                    perfil = PerfilAcesso.SUPER_ADMIN
                )
            )
        }

        // 2. Cadastrar Empresa Modelo
        var empresa = empresaRepository.findByCnpjCpf("12.345.678/0001-90")
        if (empresa == null) {
            empresa = empresaRepository.save(
                Empresa(
                    nomeFantasia = "Distribuidora do Vitiin",
                    razaoSocial = "Vitiin Comercio Ltda",
                    cnpjCpf = "12.345.678/0001-90",
                    telefone = "(11) 98765-4321",
                    emailAdmin = "gerente@lojaflex.com.br",
                    nomeAdmin = "Gabriel Rocha",
                    senhaAdmin = "admin123"
                )
            )
        }

        // 3. Cadastrar Gerente e Caixa da Empresa Modelo
        if (usuarioRepository.findByEmail("gerente@lojaflex.com.br") == null) {
            usuarioRepository.save(
                Usuario(
                    nome = "Gabriel Rocha (Gerente)",
                    email = "gerente@lojaflex.com.br",
                    senha = "admin",
                    perfil = PerfilAcesso.GERENTE,
                    empresa = empresa
                )
            )
        }

        if (usuarioRepository.findByEmail("caixa@lojaflex.com.br") == null) {
            usuarioRepository.save(
                Usuario(
                    nome = "Operador Caixa 01",
                    email = "caixa@lojaflex.com.br",
                    senha = "admin",
                    perfil = PerfilAcesso.CAIXA,
                    empresa = empresa
                )
            )
        }

        // 4. Cadastrar Catálogo Inicial de Produtos no MySQL
        if (produtoRepository.findByEmpresaId(empresa.id!!).isEmpty()) {
            produtoRepository.saveAll(
                listOf(
                    Produto(nome = "Cerveja Heineken 350ml", preco = 6.50, precoCusto = 4.50, codigoBarra = "789123456789", quantidadeEstoque = 50, empresa = empresa),
                    Produto(nome = "Refrigerante Coca-Cola 2L", preco = 8.00, precoCusto = 5.50, codigoBarra = "789123456790", quantidadeEstoque = 30, empresa = empresa),
                    Produto(nome = "Água Mineral 500ml", preco = 2.50, precoCusto = 1.20, codigoBarra = "789123456791", quantidadeEstoque = 100, empresa = empresa),
                    Produto(nome = "Salgadinho Elma Chips 100g", preco = 12.00, precoCusto = 8.00, codigoBarra = "789123456792", quantidadeEstoque = 20, empresa = empresa),
                    Produto(nome = "Energético Red Bull 250ml", preco = 11.50, precoCusto = 7.50, codigoBarra = "789123456793", quantidadeEstoque = 40, empresa = empresa)
                )
            )
        }

        // 5. Cadastrar Fornecedores no MySQL
        if (fornecedorRepository.findByEmpresaId(empresa.id!!).isEmpty()) {
            fornecedorRepository.saveAll(
                listOf(
                    Fornecedor(nomeFantasia = "Ambev Brasil Bebidas", razaoSocial = "Ambev S.A.", cnpj = "00.360.308/0001-04", telefone = "(11) 98765-4321", email = "contato@ambev.com.br", empresa = empresa),
                    Fornecedor(nomeFantasia = "Coca-Cola FEMSA", razaoSocial = "FEMSA Brasil", cnpj = "45.992.831/0001-90", telefone = "(11) 97654-3210", email = "vendas@femsa.com.br", empresa = empresa)
                )
            )
        }

        // 6. Cadastrar Clientes de Fiado Iniciais no MySQL
        if (clienteFiadoRepository.findByEmpresaId(empresa.id!!).isEmpty()) {
            clienteFiadoRepository.saveAll(
                listOf(
                    ClienteFiado(nomeCliente = "João Carlos da Silva", cpfCliente = "123.456.789-00", telefoneWhatsapp = "11987654321", limiteCredito = 200.00, valorDevido = 145.00, itensComprados = "2x Cerveja Heineken, 1x Coca 2L", dataCompra = "02/09/2026", horaCompra = "19:30", dataVencimento = "15/09/2026", empresa = empresa),
                    ClienteFiado(nomeCliente = "Maria Aparecida Santos", cpfCliente = "987.654.321-11", telefoneWhatsapp = "11976543210", limiteCredito = 150.00, valorDevido = 85.00, itensComprados = "1x Salgadinho, 2x Água Mineral", dataCompra = "01/09/2026", horaCompra = "18:15", dataVencimento = "10/09/2026", empresa = empresa)
                )
            )
        }

        // 7. Cadastrar Contas a Pagar no MySQL
        if (contaPagarRepository.findByEmpresaId(empresa.id!!).isEmpty()) {
            contaPagarRepository.saveAll(
                listOf(
                    ContaPagar(descricao = "Fornecedor Ambev - Bebidas", valor = 450.00, dataVencimento = "10/09/2026", paga = false, empresa = empresa),
                    ContaPagar(descricao = "Conta de Energia Elétrica", valor = 400.00, dataVencimento = "15/09/2026", paga = false, empresa = empresa),
                    ContaPagar(descricao = "Aluguel da Loja", valor = 1200.00, dataVencimento = "01/09/2026", paga = true, empresa = empresa)
                )
            )
        }

        // 8. Cadastrar Solicitações do Suporte Pendentes no MySQL
        if (solicitacaoEmpresaRepository.findAll().isEmpty()) {
            solicitacaoEmpresaRepository.saveAll(
                listOf(
                    SolicitacaoEmpresa(cnpjCpf = "12.345.678/0001-90", nomeFantasia = "Distribuidora do Vitiin", telefone = "11987654321", nomeAdmin = "Gabriel Rocha", email = "vitiin@gmail.com"),
                    SolicitacaoEmpresa(cnpjCpf = "45.992.831/0001-22", nomeFantasia = "Mercadinho Silva & Cia", telefone = "11976543210", nomeAdmin = "Marcos Silva", email = "silva@mercadinho.com")
                )
            )
        }
    }
}
