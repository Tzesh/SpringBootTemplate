package com.tzesh.springtemplate.entity.auth;

import com.tzesh.springtemplate.base.entity.BaseEntity;
import com.tzesh.springtemplate.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@SequenceGenerator(name = "TOKEN_GEN", sequenceName = "TOKEN_SEQ", allocationSize = 1)
public class Token extends BaseEntity {
    @Id
    @GeneratedValue(generator = "TOKEN_GEN", strategy = GenerationType.SEQUENCE)
    public Long id;

    @Column(name = "TOKEN", unique = true)
    public String token;

    @Column(name = "TYPE", length = 50)
    @Enumerated(EnumType.STRING)
    public com.tzesh.springtemplate.enumeration.auth.Token tokenType = com.tzesh.springtemplate.enumeration.auth.Token.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public User user;
}
