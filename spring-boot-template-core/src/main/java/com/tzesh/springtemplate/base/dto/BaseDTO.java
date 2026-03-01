package com.tzesh.springtemplate.base.dto;

import com.tzesh.springtemplate.base.annotation.ExcludeFromCodeCoverage;
import com.tzesh.springtemplate.base.dto.field.BaseDTOAuditableFields;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.util.UUID;

/**
 * BaseDTO is an interface for DTOs
 * @see DTO
 * @author tzesh
 */
@ExcludeFromCodeCoverage
@Data
@MappedSuperclass
public abstract class BaseDTO implements DTO {

    @Embedded
    private BaseDTOAuditableFields auditableFields;

    protected UUID id;

    /**
     * Get id of the DTO
     * @return id of the DTO
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Set id of the DTO
     * @param id id of the DTO
     */
    public void setId(final UUID id) {
        this.id = id;
    }
}
