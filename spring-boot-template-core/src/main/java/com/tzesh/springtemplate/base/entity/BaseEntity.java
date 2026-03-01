package com.tzesh.springtemplate.base.entity;

import com.tzesh.springtemplate.base.annotation.ExcludeFromCodeCoverage;
import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Base entity class for all entities
 * implements Serializable, Cloneable, Entity
 * contains BaseAuditableFields
 * @see Serializable
 * @see Cloneable
 * @see jakarta.persistence.Entity
 * @see BaseAuditableFields
 * @author tzesh
 */
@ExcludeFromCodeCoverage
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable, Cloneable, Entity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Embedded
    private BaseAuditableFields auditableFields;

    @Override
    public BaseEntity clone() {
        try {
            final BaseEntity clone = (BaseEntity) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Get the class name of the entity to use it in the error messages
     * @return class name of the entity
     */
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

}
