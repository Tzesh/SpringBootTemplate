package com.tzesh.springtemplate.base.service;

import com.tzesh.springtemplate.base.dto.BaseDTO;
import com.tzesh.springtemplate.base.entity.BaseEntity;
import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import com.tzesh.springtemplate.base.mapper.BaseMapper;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Base service for all services in the application
 * @param <E> Entity
 *           @see BaseEntity
 * @param <D> DTO
 *           @see BaseDTO
 * @param <R> Repository
 *           @see JpaRepository
 * @param <M> Mapper
 *           @see BaseMapper
 * @author tzesh
 */
@MappedSuperclass
@Getter
public abstract class BaseService<E extends BaseEntity, D extends BaseDTO, R extends JpaRepository<E, Long>, M extends BaseMapper<E, D>> {
    protected final R repository;
    protected final M mapper;
    protected final UserDetailsService userDetailsService;


    /**
     * Constructor for the service
     * @param repository Repository for the service
     * @param service UserDetailsService for the service
     */
    public BaseService(R repository, UserDetailsService service) {
        this.mapper = initializeMapper();
        this.repository = repository;
        this.userDetailsService = service;
    }

    /**
     * Initialize mapper for the service
     * @return class of the mapper to be initialized
     */
    protected abstract M initializeMapper();

    /**
     * Find entity by id
     * @param id id of the entity
     * @return Entity
     */
    public D findById(Long id) {
        // check if the entity exists
        this.checkIfEntityExists(id);

        // return the entity
        return mapper.toDTO(repository.findById(id).get());
    }

    /**
     * Get entities list
     * @return List of entities
     */
    public List<D> findAll() {
        // return the list of entities
        return mapper.toDTO(repository.findAll());
    }

    /**
     * Save entity
     * @param dto DTO of the entity
     * @return Entity
     */
    public D save(D dto) {
        // initialize entity by mapping the dto
        E entity = mapper.toEntity(dto);

        // initialize auditable fields
        BaseAuditableFields auditableFields;

        // check if the entity is new or not
        if (dto.getId() == null) {
            // if new, set the created by field
            auditableFields = new BaseAuditableFields();

            // set created by field
            auditableFields.setCreatedBy(getCurrentUser());

            // set created date
            auditableFields.setCreatedDate(LocalDateTime.now());
        }
        else {
            // check if the entity exists
            this.checkIfEntityExists(dto.getId());

            // if not new, get the entity from the database
            E existingEntity = repository.findById(dto.getId()).get();

            // set auditable fields
            auditableFields = existingEntity.getBaseAuditableFields();

            // set updated by field
            auditableFields.setUpdatedBy(getCurrentUser());

            // set updated date
            auditableFields.setUpdatedDate(LocalDateTime.now());
        }

        // set auditable fields to the entity
        entity.setBaseAuditableFields(auditableFields);

        // save the entity to the database and return the mapped dto
        return mapper.toDTO(repository.save(mapper.toEntity(dto)));
    }

    /**
     * Delete entity by id
     * @param id id of the entity
     */
    public D deleteById(Long id) {
        // check if the entity exists
        this.checkIfEntityExists(id);

        // get the entity from the database
        E entity = repository.findById(id).get();

        // delete the entity
        repository.deleteById(id);

        // return the mapped dto
        return mapper.toDTO(entity);
    }

    /**
     * Check if the entity exists
     * @param id id of the entity
     * @throws RuntimeException if the entity does not exist
     */
    protected void checkIfEntityExists(Long id) {
        // check if the entity exists
        if (!repository.existsById(id))
            throw new RuntimeException("Entity with id " + id + " does not exist");
    }

    /**
     * Get current user from the security context
     * @return username of the current user
     */
    protected String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
