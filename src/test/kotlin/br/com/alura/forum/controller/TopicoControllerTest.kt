package br.com.alura.forum.controller

import br.com.alura.forum.model.Role
import br.com.alura.forum.model.UsuarioTest
import br.com.alura.forum.security.config.JWTUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TopicoControllerTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    private var token: String? = null

    companion object {
        private const val RECURSO = "/topicos/"
        private const val RECURSO_ID = RECURSO.plus("%s")
    }

    @BeforeEach
    fun setup() {
        token = generateToken()

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder?>(
                SecurityMockMvcConfigurers.springSecurity()
            )
            .build()
    }

    @Test
    fun `deve retornar codigo 400 quando chamar topicos sem token`() {
        mockMvc.get(RECURSO).andExpect { status { is4xxClientError() } }
    }

    private fun generateToken(): String? {
        val authorities = mutableListOf(Role(1, "LEITURA_ESCRITA"))
        val usuario = UsuarioTest.buildToToken()

        return jwtUtil.generateToken("kratos@email.com", authorities)
    }

    @Test
    fun `deve retornar codigo 200 quando chamar topicos com token`() {
        mockMvc.get(RECURSO) {
            headers { token?.let { this.setBearerAuth(it) } }
        }.andExpect { status { is2xxSuccessful() } }
    }

    @Test
    fun `deve retornar codigo 200 quando chamar topicos de id com token`() {
        mockMvc.get(RECURSO_ID.format("1")) {
            headers { token?.let { this.setBearerAuth(it) } }
        }.andExpect { status { is2xxSuccessful() } }
    }

}