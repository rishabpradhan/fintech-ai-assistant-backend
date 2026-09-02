package com.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.SqlTypedJdbcType;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_activity")
public class UserAudit{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_detail")
    private JsonNode userActivity;

    @Column(name = "ip_addresss")
    private String ipAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
