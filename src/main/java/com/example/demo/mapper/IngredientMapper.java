package com.example.demo.mapper;

import com.example.demo.entity.Ingredient;
import com.example.demo.models.IngredientRequest;
import com.example.demo.models.IngredientUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    Ingredient toEntity(IngredientRequest ingredientRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    Ingredient toEntity(IngredientUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    void updateFromRequest(IngredientUpdateRequest request,
                           @MappingTarget Ingredient ingredient);
}
