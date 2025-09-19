package simple.chatgpt.pojo.management;

import java.io.Serializable;

import simple.chatgpt.validator.management.property.ValidManagementProperty;

/*
Class-level validation (like @ValidProperty)
1>If you put @ValidProperty on a class but never validate the 
object (either manually or via @Valid in a service/controller), 
it won’t run. 
2>Class-level validators usually implement ConstraintValidator<ValidProperty, 
Property> and can check multiple fields together.
*/
@ValidManagementProperty
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
