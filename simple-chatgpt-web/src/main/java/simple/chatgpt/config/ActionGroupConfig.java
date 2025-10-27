package simple.chatgpt.config;

import java.util.ArrayList;
import java.util.List;

public class ActionGroupConfig {
    private String id;
    private List<ActionConfig> actions;

    // ✅ Constructor with id only (empty list)
    public ActionGroupConfig(String id) {
        this.id = id;
        this.actions = new ArrayList<>();
    }

    // ✅ Constructor with id and actions list
    public ActionGroupConfig(String id, List<ActionConfig> actions) {
        this.id = id;
        this.actions = actions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ActionConfig> getActions() {
        return actions;
    }

    public void setActions(List<ActionConfig> actions) {
        this.actions = actions;
    }

    // ✅ Add a single action
    public void addAction(ActionConfig action) {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(action);
    }
}
