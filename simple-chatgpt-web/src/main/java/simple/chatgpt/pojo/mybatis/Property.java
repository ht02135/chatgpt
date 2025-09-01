package simple.chatgpt.pojo.mybatis;

public class Property {
    private String key;
    private String type;
    private String value;

    public Property() {}

    public Property(String key, String type, String value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getKey() { return key; }

    public String getType() { return type; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}