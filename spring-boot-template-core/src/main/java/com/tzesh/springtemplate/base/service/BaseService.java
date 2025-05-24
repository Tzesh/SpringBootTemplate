package com.tzesh.springtemplate.base.service;

import com.tzesh.springtemplate.base.dto.BaseDTO;
import com.tzesh.springtemplate.base.entity.BaseEntity;
import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.base.exception.OperationFailedException;
import com.tzesh.springtemplate.base.mapper.BaseMapper;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

@MappedSuperclass
@Getter
public abstract class BaseService<E extends BaseEntity, D extends BaseDTO, R extends JpaRepository<E, Long>, M extends BaseMapper<E, D>> {
    protected final R repository;
    protected final M mapper;
    protected final UserDetailsService userDetailsService;
    protected final String subject = getClass().getSimpleName().replace("Service", "");

    public BaseService(final R repository, final UserDetailsService service) {
        this.mapper = initializeMapper();
        this.repository = repository;
        this.userDetailsService = service;
    }

    protected abstract M initializeMapper();

    public D findById(final Long id) {
        final E entity = repository.findById(id).orElseThrow(() -> notFound(id));
        return mapper.toDTO(entity);
    }

    public List<D> findAll() {
        return mapper.toDTO(repository.findAll());
    }

    public D save(final D dto) {
        final E entity = mapper.toEntity(dto);
        final BaseAuditableFields auditableFields = getAuditableFields(dto, entity);
        entity.setAuditableFields(auditableFields);
        return mapper.toDTO(trySave(entity));
    }

    public D deleteById(final Long id) {
        final E entity = repository.findById(id).orElseThrow(() -> notFound(id));
        repository.deleteById(id);
        return mapper.toDTO(entity);
    }

    private BaseAuditableFields getAuditableFields(final D dto, final E entity) {
        final String user = getCurrentUser();
        final LocalDateTime now = LocalDateTime.now();
        if (dto.getId() == null) {
            final BaseAuditableFields fields = new BaseAuditableFields();
            fields.setCreatedBy(user);
            fields.setCreatedDate(now);
            return fields;
        } else {
            final E existing = repository.findById(dto.getId()).orElseThrow(() -> notFound(dto.getId()));
            final BaseAuditableFields fields = existing.getAuditableFields();
            fields.setUpdatedBy(user);
            fields.setUpdatedDate(now);
            return fields;
        }
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException(GenericErrorMessage.builder()
                .message(String.format("%s with id %d not found", subject, id))
                .build());
    }

    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    protected E trySave(E entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            throw new OperationFailedException(GenericErrorMessage.builder()
                    .message(String.format("Failed to save %s", subject))
                    .build());
        }
    }
}
