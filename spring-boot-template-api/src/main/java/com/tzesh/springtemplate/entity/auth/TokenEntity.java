package com.tzesh.springtemplate.entity.auth;

import com.tzesh.springtemplate.base.entity.BaseEntity;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.enumeration.auth.Token;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Token entity for storing tokens in database
 * @author tzesh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TOKEN")
@EqualsAndHashCode(callSuper = true)
public class TokenEntity extends BaseEntity {
    @Column(name = "TOKEN", unique = true)
    public String token;

    @Column(name = "TYPE", length = 50)
    @Enumerated(EnumType.STRING)
    public final Token type = Token.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public User user;
}
