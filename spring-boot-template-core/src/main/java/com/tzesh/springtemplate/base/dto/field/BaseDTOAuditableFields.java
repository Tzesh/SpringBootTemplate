package com.tzesh.springtemplate.base.dto.field;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseAuditableFields is an embeddable class that is used to store data about
 * createdBy, createdDate, updatedBy, updatedDate.
 * @author tzesh
 */
@Embeddable
@Getter
@Setter
public class BaseDTOAuditableFields implements Serializable, Cloneable {

    private String createdBy;

    private LocalDateTime createdDate;

    private String updatedBy;

    private LocalDateTime updatedDate;
}