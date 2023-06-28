package com.tzesh.springtemplate.base.entity.field;

import jakarta.persistence.Column;
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
public class BaseAuditableFields implements Serializable, Cloneable {
    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "DT_CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "DT_UPDATED_DATE")
    private LocalDateTime updatedDate;
}
