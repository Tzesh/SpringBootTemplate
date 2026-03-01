package com.tzesh.springtemplate.repository.auth;

import com.tzesh.springtemplate.entity.auth.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TokenRepository is a repository for Token entity
 * @author tzesh
 */
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    /**
     * Find all valid tokenEntity by user
     * @param id user id
     * @return List of Token
     */
    @Query(value = """
      select t from TokenEntity t inner join User u
      on t.user.id = u.id
      where u.id = :id and (t.expired = false or t.revoked = false)
      """)
    List<TokenEntity> findAllValidTokenByUser(UUID id);

    /**
     * Find tokenEntity by tokenEntity
     * @param tokenEntity tokenEntity
     * @return Optional of Token
     */
    Optional<TokenEntity> findByToken(String tokenEntity);
}
