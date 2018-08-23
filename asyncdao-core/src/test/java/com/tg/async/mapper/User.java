package com.tg.async.mapper;

import com.tg.async.annotation.Column;
import com.tg.async.annotation.Id;
import com.tg.async.annotation.Ignore;
import com.tg.async.annotation.Table;
import org.joda.time.LocalDateTime;

/**
 * Created by twogoods on 2018/4/12.
 */

@Table(name = "T_User")
    public class User {
        @Id("id")
        private Long id;

        private String username;
        private String password;
        private Integer age;

        @Column("old_address")
        private String oldAddress;
        @Column("now_address")
        private String nowAddress;

        private Byte state;

        @Column("created_at")
        private LocalDateTime createdAt;
        @Column("updated_at")
        private LocalDateTime updatedAt;

        @Ignore
        private String remrk;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }

    public String getNowAddress() {
        return nowAddress;
    }

    public void setNowAddress(String nowAddress) {
        this.nowAddress = nowAddress;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRemrk() {
        return remrk;
    }

    public void setRemrk(String remrk) {
        this.remrk = remrk;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", oldAddress='" + oldAddress + '\'' +
                ", nowAddress='" + nowAddress + '\'' +
                ", state=" + state +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
