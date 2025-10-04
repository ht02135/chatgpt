package simple.chatgpt.pojo.mybatis;

import simple.chatgpt.validator.mybatis.property.ValidProperty;

/*
Class-level validation (like @ValidProperty)
1>If you put @ValidProperty on a class but never validate the 
object (either manually or via @Valid in a service/controller), 
it won’t run. 
2>Class-level validators usually implement ConstraintValidator<ValidProperty, 
Property> and can check multiple fields together.
*/
@ValidProperty
public class MyBatisProperty {
    private String key;
    private String type;
    private String value;

    public MyBatisProperty() {}

    public MyBatisProperty(String key, String type, String value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getKey() { return key; }

    public String getType() { return type; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    @Override
    public String toString() {
        return "Property{" +
                "key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}