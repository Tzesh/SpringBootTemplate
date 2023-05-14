package com.tzesh.springtemplate.base.dto;

import java.io.Serializable;

/**
 * Interface for DTOs
 * @see Serializable
 * @see Cloneable
 * @author tzesh
 */
public interface BaseDTO extends Serializable, Cloneable {
    /**
     * Get id of the DTO
     * @return id of the DTO
     */
    abstract Long getId();
}
