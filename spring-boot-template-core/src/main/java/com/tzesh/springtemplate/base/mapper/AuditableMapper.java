package com.tzesh.springtemplate.base.mapper;

import com.tzesh.springtemplate.base.dto.field.BaseDTOAuditableFields;
import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import org.mapstruct.Mapper;

/**
 * @author tzesh
 */
@Mapper
public interface AuditableMapper {

    /**
     * Map the auditable fields from entity to DTO
     */
    BaseDTOAuditableFields BaseAuditableFieldsToBaseDTOAuditableFields(BaseAuditableFields entity);

    /**
     * Map the auditable fields from DTO to entity
     */
    BaseAuditableFields BaseDTOAuditableFieldsToBaseAuditableFields(BaseDTOAuditableFields dto);
}
