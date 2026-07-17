package me.autobot.playerdoll.api.doll;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.autobot.playerdoll.api.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

// authlib 7 (MC 1.21.9+) turned GameProfile into an immutable record:
// getName/getId/getProperties -> name/id/properties, and properties can no longer be mutated
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

    public static GameProfile withTextures(GameProfile profile, String value, String signature) {
        Property textures = new Property("textures", value, signature);
        PropertyMap properties = properties(profile);
        try {
            properties.removeAll("textures");
            properties.put("textures", textures);
            return profile;
        } catch (UnsupportedOperationException immutable) {
            // authlib 7: rebuild the record
            try {
                Multimap<String, Property> map = ArrayListMultimap.create();
                map.put("textures", textures);
                Constructor<?> mapCtor = PropertyMap.class.getConstructor(Multimap.class);
                Constructor<?> profileCtor = GameProfile.class.getConstructor(UUID.class, String.class, PropertyMap.class);
                return (GameProfile) profileCtor.newInstance(id(profile), name(profile), mapCtor.newInstance(map));
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
