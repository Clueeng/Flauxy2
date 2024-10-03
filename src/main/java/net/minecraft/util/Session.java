package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;

public class Session {
    private String username;

    private String playerID;

    private String token;

    private Type sessionType;

    public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn) {
        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Type.setSessionType(sessionTypeIn);
    }

    public void switchSession(Session toChange) {
        this.sessionType = toChange.sessionType;
        this.playerID = toChange.playerID;
        this.token = toChange.token;
        this.username = toChange.getUsername();
    }

    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }

    public String getPlayerID() {
        return this.playerID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public GameProfile getProfile() {
        try {
            UUID uuid = UUIDTypeAdapter.fromString(getPlayerID());
            return new GameProfile(uuid, getUsername());
        } catch (IllegalArgumentException var2) {
            return new GameProfile((UUID)null, getUsername());
        }
    }

    public Type getSessionType() {
        return this.sessionType;
    }

    public enum Type {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, Type> SESSION_TYPES = Maps.newHashMap();

        private final String sessionType;

        static {
            for (Type session$type : values())
                SESSION_TYPES.put(session$type.sessionType, session$type);
        }

        Type(String sessionTypeIn) {
            this.sessionType = sessionTypeIn;
        }

        public static Type setSessionType(String sessionTypeIn) {
            return SESSION_TYPES.get(sessionTypeIn.toLowerCase());
        }
    }
}
