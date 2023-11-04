package tv.memoryleakdeath.hex.frontend.controller.developer;

import java.util.List;

import tv.memoryleakdeath.hex.common.pojo.HexRegisteredClientPojo;

public class HexRegisteredClientModel extends HexRegisteredClientPojo {

    private static final long serialVersionUID = 1L;

    private List<String> selectedScopes;

    public List<String> getSelectedScopes() {
        return selectedScopes;
    }

    public void setSelectedScopes(List<String> selectedScopes) {
        this.selectedScopes = selectedScopes;
    }
}
