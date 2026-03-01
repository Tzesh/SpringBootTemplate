package com.tzesh.springtemplate.base.dto;

import com.tzesh.springtemplate.base.annotation.ExcludeFromCodeCoverage;

import java.io.Serializable;
import java.util.UUID;

/**
 * Interface for DTOs
 * @see Serializable
 * @see Cloneable
 * @author tzesh
 */
@ExcludeFromCodeCoverage
public interface DTO extends Serializable, Cloneable {
    /**
     * Get id of the DTO
     * @return id of the DTO
     */
    abstract UUID getId();
}
