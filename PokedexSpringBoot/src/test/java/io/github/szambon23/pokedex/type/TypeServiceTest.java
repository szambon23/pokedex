package io.github.szambon23.pokedex.type;

import io.github.szambon23.pokedex.type.dto.TypeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeServiceTest {

    @Mock
    private TypeRepository typeRepository;

    @Test
    void findAllReturnsTypesSortedByName() {
        when(typeRepository.findAll(Sort.by("name"))).thenReturn(List.of(new Type("bug"), new Type("fire")));

        List<TypeResponse> types = new TypeService(typeRepository).findAll();

        assertThat(types).extracting(TypeResponse::name).containsExactly("bug", "fire");
    }
}