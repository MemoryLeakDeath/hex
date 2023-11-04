package tv.memoryleakdeath.hex.common;

import java.util.Arrays;
import java.util.List;

public enum ApiScopes {
    BASICREAD("api.scopes.basicread.name", "api.scopes.basicread.desc"),
    BASICWRITE("api.scopes.basicwrite.name", "api.scopes.basicwrite.desc"),
    CHAT("api.scopes.chat.name", "api.scopes.chat.desc"),
    CHANNELREAD("api.scopes.channelread.name", "api.scopes.channelread.desc"),
    CHANNELWRITE("api.scopes.channelwrite.name", "api.scopes.channelwrite.desc"),
    STREAMCHAT("api.scopes.streamchat.name", "api.scopes.streamchat.desc"),
    ACCOUNTFULL("api.scopes.accountfull.name", "api.scopes.accountfull.desc"),
    INTERACTIVE("api.scopes.interactive.name", "api.scopes.interactive.desc");

    private String scopeNameLookup;
    private String descriptionLookup;

    ApiScopes(String scopeNameLookup, String descriptionLookup) {
        this.scopeNameLookup = scopeNameLookup;
        this.descriptionLookup = descriptionLookup;
    }

    public String getScopeNameLookup() {
        return scopeNameLookup;
    }

    public String getDescriptionLookup() {
        return descriptionLookup;
    }

    @Override
    public String toString() {
        return ("SCOPE_" + name());
    }

    public static List<ApiScopes> asList() {
        return Arrays.asList(ApiScopes.values());
    }
}
