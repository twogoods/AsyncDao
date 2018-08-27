package com.tg.mapper;
import java.util.List;

/**
 * Created by twogoods on 2017/8/2.
 */
public class UserSearch {
    private int id;
    private String username;
    private int age;

    private List<Integer> ids;
    private int[] idArr;

    private int minAge;
    private int maxAge;

    private Integer offset;
    private Integer limit;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public int[] getIdArr() {
        return idArr;
    }

    public void setIdArr(int[] idArr) {
        this.idArr = idArr;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
