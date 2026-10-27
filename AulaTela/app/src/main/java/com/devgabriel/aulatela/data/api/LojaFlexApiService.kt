package com.devgabriel.aulatela.data.api

import com.devgabriel.aulatela.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface LojaFlexApiService {

    // --- AUTENTICAÇÃO E CADASTRO ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/cadastrar")
    suspend fun cadastrarOperador(@Body request: CadastroRequest): Response<UsuarioResponse>

    @POST("api/auth/solicitar-empresa")
    suspend fun solicitarCadastroEmpresa(@Body request: CadastroEmpresaRequest): Response<Empresa>

    @GET("api/auth/empresas-pendentes")
    suspend fun listarEmpresasPendentes(): Response<List<Empresa>>

    @PUT("api/auth/aprovar-empresa/{id}")
    suspend fun aprovarEmpresa(
        @Path("id") empresaId: Long,
        @Body request: AprovarLojaRequest
    ): Response<Empresa>

    // --- USUÁRIOS PENDENTES ---
    @GET("api/usuarios/pendentes/{empresaId}")
    suspend fun listarUsuariosPendentes(@Path("empresaId") empresaId: Long): Response<List<Usuario>>

    @PUT("api/usuarios/{id}/aprovar")
    suspend fun aprovarUsuario(@Path("id") usuarioId: Long): Response<Void>

    @DELETE("api/usuarios/{id}/rejeitar")
    suspend fun rejeitarUsuario(@Path("id") usuarioId: Long): Response<Void>

    // --- PRODUTOS E ESTOQUE ---
    @GET("api/produtos/empresa/{empresaId}")
    suspend fun listarProdutosPorEmpresa(@Path("empresaId") empresaId: Long): Response<List<Produto>>

    @GET("api/produtos")
    suspend fun listarProdutos(): Response<List<Produto>>

    @POST("api/produtos")
    suspend fun cadastrarProduto(@Body produto: Produto): Response<Produto>

    @PUT("api/produtos/{id}/estoque")
    suspend fun ajustarEstoque(
        @Path("id") produtoId: Long,
        @Query("quantidade") novaQuantidade: Int
    ): Response<Produto>

    // --- PDV E PEDIDOS ---
    @POST("api/pedidos")
    suspend fun criarPedido(@Body request: PedidoRequest): Response<PedidoResponse>

    // --- OPERAÇÕES, DASHBOARD, FINANCEIRO, FORNECEDORES E FIADO ---
    @GET("api/operacoes/dashboard/{empresaId}")
    suspend fun obterMetricasDashboard(@Path("empresaId") empresaId: Long): Response<DashboardMetricas>

    @GET("api/operacoes/fornecedores/{empresaId}")
    suspend fun listarFornecedores(@Path("empresaId") empresaId: Long): Response<List<Fornecedor>>

    @POST("api/operacoes/fornecedores")
    suspend fun salvarFornecedor(@Body fornecedor: Fornecedor): Response<Fornecedor>

    @GET("api/operacoes/contas/{empresaId}")
    suspend fun listarContasPagar(@Path("empresaId") empresaId: Long): Response<List<ContaPagar>>

    @POST("api/operacoes/contas")
    suspend fun salvarContaPagar(@Body conta: ContaPagar): Response<ContaPagar>

    @PUT("api/operacoes/contas/{id}/pagar")
    suspend fun baixarContaPagar(@Path("id") contaId: Long): Response<ContaPagar>

    @GET("api/operacoes/fiados/{empresaId}")
    suspend fun listarClientesFiado(@Path("empresaId") empresaId: Long): Response<List<ClienteFiado>>

    @POST("api/operacoes/fiados")
    suspend fun salvarClienteFiado(@Body clienteFiado: ClienteFiado): Response<ClienteFiado>

    @PUT("api/operacoes/fiados/{id}/quitar")
    suspend fun quitarDividaFiado(@Path("id") clienteId: Long): Response<ClienteFiado>



}