package com.light.storage.entity;

public class BirthInfo {
    private Integer id;
    private Integer userId;
    private String name;
    private String birth;
    private Boolean subscribe;

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "BirthInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", subscribe=" + subscribe +
                '}';
    }
}
