package org.patform.test.bean;

import java.util.List;
import java.util.Map;

public class User {

    private Integer id ;
    private String username;
    private Cat cat;
    private List<String> list;
    private Map<String,Cat> map;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, Cat> getMap() {
        return map;
    }

    public void setMap(Map<String, Cat> map) {
        this.map = map;
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", cat=" + cat +
                ", list=" + list +
                ", map=" + map +
                '}';
    }
}
