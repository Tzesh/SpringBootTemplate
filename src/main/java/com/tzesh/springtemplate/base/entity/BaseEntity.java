package com.tzesh.springtemplate.base.entity;

import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Base entity class for all entities
 * implements Serializable, Cloneable, Entity
 * contains BaseAuditableFields
 * @see Serializable
 * @see Cloneable
 * @see Entity
 * @see BaseAuditableFields
 * @author tzesh
 */
@Getter
@Setter
@MappedSuperclass
@Table
public abstract class BaseEntity implements Serializable, Cloneable, Entity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Embedded
    private BaseAuditableFields baseAuditableFields;

    @Override
    public BaseEntity clone() {
        try {
            BaseEntity clone = (BaseEntity) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
