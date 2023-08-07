package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
//@Entity
public class Member {
    @Id
    private Long id;
    @Column(name = "name", nullable = false) // 필드명과 데이터베이스의 컬럼명이 다를때
    private String username;
    private Integer age;
    @Enumerated(EnumType.STRING) // EnumType.ORDINAL 처리하면 추가 될때마다 꼬인다.
    private RoleType roleType;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    private LocalDate testLocalDate; // 연월일 -> date형으로 저장
    private LocalDateTime testLocalDateTime; // 연월일시 -> timestamp형으로 저장
    @Lob
    private String description;
    public Member(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //Getter, Setter…
}
