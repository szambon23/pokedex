package io.github.szambon23.pokedex.type;

import io.github.szambon23.pokedex.type.dto.TypeResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TypeService {
    private final TypeRepository typeRepository;
    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public List<TypeResponse> findAll() {
        return typeRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(type -> new TypeResponse(type.getName()))
                .toList();
    }
}
