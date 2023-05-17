package com.tzesh.springtemplate.base.dto;

import com.tzesh.springtemplate.base.dto.field.BaseDTOAuditableFields;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * BaseDTO is an interface for DTOs
 * @see DTO
 * @author tzesh
 */
@Data
@MappedSuperclass
public abstract class BaseDTO implements DTO {

    @Embedded
    private BaseDTOAuditableFields auditableFields;

    protected Long id;

    /**
     * Get id of the DTO
     * @return id of the DTO
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Set id of the DTO
     * @param id id of the DTO
     */
    public void setId(Long id) {
        this.id = id;
    }
}
