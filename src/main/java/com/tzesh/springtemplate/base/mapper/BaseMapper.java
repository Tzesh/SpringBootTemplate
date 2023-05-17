package com.tzesh.springtemplate.base.mapper;

import com.tzesh.springtemplate.base.dto.BaseDTO;
import com.tzesh.springtemplate.base.dto.DTO;
import com.tzesh.springtemplate.base.entity.BaseEntity;
import jakarta.persistence.MappedSuperclass;

import java.util.List;

/**
 * Interface for mappers
 * @param <E> the entity
 *           @see BaseEntity
 * @param <D> the DTO
 *           @see DTO
 * @author tzesh
 */
@MappedSuperclass
public interface BaseMapper<E extends BaseEntity, D extends BaseDTO> {
    /**
     * Converts the DTO to entity
     * @param dto the DTO
     * @return the entity
     */
    E toEntity(D dto);

    /**
     * Converts the entity to DTO
     * @param entity the entity
     * @return the DTO
     */
    D toDTO(E entity);

    /**
     * Converts the list of DTOs to list of entities
     * @param dtoList the list of DTOs
     * @return the list of entities
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Converts the list of entities to list of DTOs
     * @param entityList the list of entities
     * @return the list of DTOs
     */
    List<D> toDTO(List<E> entityList);
}
