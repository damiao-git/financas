package com.house.financas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.house.financas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MonexaCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveExecutarCrudCompletoDoMvpComJwt() throws Exception {
        String token = registrarEObterToken();

        mockMvc.perform(options("/fluxo-caixa/projecao")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"));

        mockMvc.perform(get("/usuarios/me").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mvp@monexa.com"));

        mockMvc.perform(put("/usuarios/me")
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Usuario MVP Atualizado"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Usuario MVP Atualizado"));

        long categoriaId = criarCategoria(token);
        mockMvc.perform(get("/categorias").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(categoriaId));
        mockMvc.perform(put("/categorias/{id}", categoriaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Moradia atualizada"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Moradia atualizada"));

        long receitaId = criarReceita(token);
        mockMvc.perform(get("/receitas?ano=2026&mes=7").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(receitaId));
        mockMvc.perform(put("/receitas/{id}", receitaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "descricao", "Salario atualizado",
                                "valor", 8100,
                                "diaRecebimento", 5,
                                "mes", 7,
                                "ano", 2026,
                                "tipoReceita", "SALARIO",
                                "origem", "Empresa",
                                "recorrente", true
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(8100));

        long despesaId = criarDespesa(token, categoriaId);
        mockMvc.perform(get("/despesas").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(despesaId));
        mockMvc.perform(put("/despesas/{id}", despesaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "descricao", "Aluguel atualizado",
                                "valor", 1850,
                                "diaVencimento", 10,
                                "tipoDespesa", "FIXA",
                                "recorrente", true,
                                "categoriaId", categoriaId
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(1850));

        long contaId = criarConta(token, categoriaId, despesaId);
        mockMvc.perform(get("/contas-mensais?ano=2026&mes=7").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(contaId));
        mockMvc.perform(put("/contas-mensais/{id}", contaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "descricao", "Conta aluguel atualizada",
                                "valorPrevisto", 1850,
                                "dataVencimento", "2026-07-10",
                                "mes", 7,
                                "ano", 2026,
                                "categoriaId", categoriaId,
                                "despesaId", despesaId,
                                "observacao", "Teste"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorPrevisto").value(1850));

        long pagamentoId = criarPagamento(token, contaId);
        mockMvc.perform(get("/contas-mensais/{id}/pagamentos", contaId).headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pagamentoId));
        mockMvc.perform(put("/pagamentos/{id}", pagamentoId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "valorPago", 1850,
                                "dataPagamento", "2026-07-06",
                                "observacao", "Pago atualizado"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorPago").value(1850));

        long dividaId = criarDivida(token);
        mockMvc.perform(get("/dividas?status=ATIVA").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dividaId));
        mockMvc.perform(put("/dividas/{id}", dividaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dividaPayload("Emprestimo atualizado", 8500, 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoAtual").value(8500));

        long amortizacaoId = criarAmortizacao(token, dividaId);
        mockMvc.perform(get("/dividas/{id}/amortizacoes", dividaId).headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(amortizacaoId));
        mockMvc.perform(put("/amortizacoes/{id}", amortizacaoId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "valor", 1200,
                                "dataAmortizacao", "2026-07-20",
                                "mes", 7,
                                "ano", 2026,
                                "observacao", "Amortizacao atualizada"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(1200));

        mockMvc.perform(get("/fluxo-caixa?ano=2026&mes=7").headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ano").value(2026))
                .andExpect(jsonPath("$.mes").value(7));

        mockMvc.perform(get("/fluxo-caixa/projecao?anoInicial=2026&mesInicial=7&quantidadeMeses=6&saldoInicial=7600")
                        .headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeMeses").value(6));

        mockMvc.perform(post("/planejamento/gerar-recorrencias?anoInicial=2026&mesInicial=8&quantidadeMeses=2")
                        .headers(auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeMeses").value(2));

        mockMvc.perform(delete("/pagamentos/{id}", pagamentoId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/contas-mensais/{id}", contaId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/amortizacoes/{id}", amortizacaoId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/dividas/{id}", dividaId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/despesas/{id}", despesaId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/receitas/{id}", receitaId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/categorias/{id}", categoriaId).headers(auth(token))).andExpect(status().isNoContent());
        mockMvc.perform(delete("/usuarios/me").headers(auth(token))).andExpect(status().isNoContent());
    }

    @Test
    void deveRegistrarTentativasFalhasEBloquearLoginTemporariamente() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Usuario Bloqueio",
                                "email", "bloqueio@monexa.com",
                                "senha", "Senha123"
                        ))))
                .andExpect(status().isCreated());

        for (int tentativa = 0; tentativa < 5; tentativa++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of(
                                    "email", "bloqueio@monexa.com",
                                    "senha", "SenhaErrada123"
                            ))))
                    .andExpect(status().isUnauthorized());
        }

        var usuario = usuarioRepository.findByEmail("bloqueio@monexa.com").orElseThrow();
        assertThat(usuario.getTentativasLoginFalhas()).isEqualTo(5);
        assertThat(usuario.getBloqueadoAte()).isNotNull();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", "bloqueio@monexa.com",
                                "senha", "Senha123"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    private String registrarEObterToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Usuario MVP",
                                "email", "mvp@monexa.com",
                                "senha", "Senha123"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("mvp@monexa.com"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", "mvp@monexa.com",
                                "senha", "Senha123"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        String token = read(result).get("token").asText();
        assertThat(token).isNotBlank();
        return token;
    }

    private long criarCategoria(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/categorias")
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Moradia"))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private long criarReceita(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/receitas")
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "descricao", "Salario",
                                "valor", 8000,
                                "diaRecebimento", 5,
                                "mes", 7,
                                "ano", 2026,
                                "tipoReceita", "SALARIO",
                                "origem", "Empresa",
                                "recorrente", true
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private long criarDespesa(String token, long categoriaId) throws Exception {
        MvcResult result = mockMvc.perform(post("/despesas")
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "descricao", "Aluguel",
                                "valor", 1800,
                                "diaVencimento", 10,
                                "tipoDespesa", "FIXA",
                                "recorrente", true,
                                "categoriaId", categoriaId
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private long criarConta(String token, long categoriaId, long despesaId) throws Exception {
        MvcResult result = mockMvc.perform(post("/contas-mensais")
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "descricao", "Conta aluguel",
                                "valorPrevisto", 1800,
                                "dataVencimento", "2026-07-10",
                                "mes", 7,
                                "ano", 2026,
                                "categoriaId", categoriaId,
                                "despesaId", despesaId,
                                "observacao", "Teste"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private long criarPagamento(String token, long contaId) throws Exception {
        MvcResult result = mockMvc.perform(post("/contas-mensais/{id}/pagamentos", contaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "valorPago", 1800,
                                "dataPagamento", "2026-07-05",
                                "observacao", "Pago"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private long criarDivida(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/dividas")
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(dividaPayload("Emprestimo", 9000, 2))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private long criarAmortizacao(String token, long dividaId) throws Exception {
        MvcResult result = mockMvc.perform(post("/dividas/{id}/amortizacoes", dividaId)
                        .headers(auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "valor", 1000,
                                "dataAmortizacao", "2026-07-20",
                                "mes", 7,
                                "ano", 2026,
                                "observacao", "Amortizacao"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    private HttpHeaders auth(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private Map<String, Object> dividaPayload(String descricao, int saldoAtual, int parcelasPagas) {
        return Map.ofEntries(
                Map.entry("descricao", descricao),
                Map.entry("instituicao", "Banco"),
                Map.entry("tipoDivida", "EMPRESTIMO"),
                Map.entry("saldoInicial", 10000),
                Map.entry("saldoAtual", saldoAtual),
                Map.entry("valorParcela", 500),
                Map.entry("diaVencimento", 15),
                Map.entry("quantidadeParcelas", 20),
                Map.entry("parcelasPagas", parcelasPagas),
                Map.entry("taxaJurosMensal", 1.5),
                Map.entry("observacao", "Teste")
        );
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private JsonNode read(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
