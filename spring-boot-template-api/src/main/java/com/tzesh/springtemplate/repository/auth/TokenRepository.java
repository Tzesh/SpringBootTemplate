package com.tzesh.springtemplate.repository.auth;

import com.tzesh.springtemplate.entity.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * TokenRepository is a repository for Token entity
 * @author tzesh
 */
public interface TokenRepository extends JpaRepository<Token, Long> {
    /**
     * Find all valid token by user
     * @param id user id
     * @return List of Token
     */
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id);

    /**
     * Find token by token
     * @param token token
     * @return Optional of Token
     */
    Optional<Token> findByToken(String token);
}
