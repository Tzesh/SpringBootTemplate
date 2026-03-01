package com.tzesh.springtemplate.base.entity;

import com.tzesh.springtemplate.base.annotation.ExcludeFromCodeCoverage;

import java.util.UUID;

/**
 * Interface for entities
 * @author tzesh
 */
@ExcludeFromCodeCoverage
public interface Entity {
    UUID getId();
}
