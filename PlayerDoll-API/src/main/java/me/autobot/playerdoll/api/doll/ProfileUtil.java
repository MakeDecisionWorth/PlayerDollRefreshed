package me.autobot.playerdoll.api.doll;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.autobot.playerdoll.api.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.UUID;

// authlib 7 (MC 1.21.9+) turned GameProfile into a record: getName/getId/getProperties -> name/id/properties
public final class ProfileUtil {
    private static final Method NAME;
    private static final Method ID;
    private static final Method PROPERTIES;

    static {
        NAME = resolve("name", "getName");
        ID = resolve("id", "getId");
        PROPERTIES = resolve("properties", "getProperties");
    }

    private static Method resolve(String recordName, String legacyName) {
        try {
            return GameProfile.class.getMethod(recordName);
        } catch (NoSuchMethodException e) {
            try {
                return GameProfile.class.getMethod(legacyName);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private ProfileUtil() {}

    public static String name(GameProfile profile) {
        return ReflectionUtil.invokeMethod(String.class, NAME, profile);
    }

    public static UUID id(GameProfile profile) {
        return ReflectionUtil.invokeMethod(UUID.class, ID, profile);
    }

    public static PropertyMap properties(GameProfile profile) {
        return ReflectionUtil.invokeMethod(PropertyMap.class, PROPERTIES, profile);
    }

    public static void setTextures(GameProfile profile, String value, String signature) {
        PropertyMap properties = properties(profile);
        properties.removeAll("textures");
        properties.put("textures", new Property("textures", value, signature));
    }
}
