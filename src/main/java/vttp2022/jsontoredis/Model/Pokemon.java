package vttp2022.jsontoredis.Model;

import java.io.Serializable;

public class Pokemon implements Serializable{
    
    private String name;
    private Integer height;
    private String imageLink;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public String getImageLink() {
        return imageLink;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}