package simple.chatgpt.pojo.management;

import java.io.Serializable;

public class PropertyManagementPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String propertyName;
    private String propertyKey;
    private String type;
    private String value;

    // ------------------ Getters & Setters ------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // ------------------ toString() ------------------
    @Override
    public String toString() {
        return "PropertyManagementPojo{" +
                "id=" + id +
                ", propertyName='" + propertyName + '\'' +
                ", propertyKey='" + propertyKey + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
