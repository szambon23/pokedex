package io.github.szambon23.pokedex.type;

import io.github.szambon23.pokedex.type.dto.TypeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(TypeController.class)
class TypeControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private TypeService typeService;

    @Test
    void getAllReturnsTypes() {
        when(typeService.findAll()).thenReturn(List.of(new TypeResponse("bug"), new TypeResponse("fire")));

        MvcTestResult result = mvc.get().uri("/api/v1/types").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson()
                .extractingPath("$[*].name")
                .asArray()
                .containsExactly("bug", "fire");
    }
}