package simple.chatgpt.config;

import java.util.ArrayList;
import java.util.List;

public class GridConfig {
    private String id;
    private List<ColumnConfig> columns = new ArrayList<>();

    public GridConfig() {}
    public GridConfig(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<ColumnConfig> getColumns() { return columns; }
    public void setColumns(List<ColumnConfig> columns) { this.columns = columns; }

    public void addColumn(ColumnConfig column) { this.columns.add(column); }
}