package com.tg.async.mapper;

import com.tg.async.annotation.Column;
import com.tg.async.annotation.Id;
import com.tg.async.annotation.Ignore;
import com.tg.async.annotation.Table;

import java.sql.Timestamp;

/**
 * Created by twogoods on 2018/4/12.
 */

@Table(name = "T_User")
public class User {
    @Id("id")
    private Long id;

    private String username;
    private String password;
    private int age;

    @Column("old_address")
    private String oldAddress;
    @Column("now_address")
    private String nowAddress;

    private int state;

    @Column("created_at")
    private Timestamp createdAt;
    @Column("updated_at")
    private Timestamp updatedAt;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
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
