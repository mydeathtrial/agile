package com.agile.common.base.swagger;

/**
 * Created by 佟盟 on 2018/10/4
 */
public class Property {
    private String type;
    private String description;

    public Property(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
