package mshower.refine.config;

import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RefineConfig {
    private static final Path CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("refine.yml");

    private static final DumperOptions OPTIONS = new DumperOptions();
    private static final Yaml YAML;

    static {
        OPTIONS.setIndent(2);
        OPTIONS.setPrettyFlow(true);
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YAML = new Yaml(OPTIONS);
    }

    public static RefineConfig INSTANCE;

    public FeatureConfig featureConfig = new FeatureConfig();
    public EndermanGriefingBlacklistConfig endermanGriefingBlacklistConfig = new EndermanGriefingBlacklistConfig();

    public static class FeatureConfig {
        public Boolean EnableEndermanGriefingBlacklist = false;
        public Boolean OPNoCheating = false;
        public Boolean SpecCommand = false;
        public Boolean SpectatorTeleport = false;
    }

    public static class EndermanGriefingBlacklistConfig {
        public List<String> EndermanGriefingBlacklist = new ArrayList<>(Arrays.asList(
                "minecraft:sand",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:red_mushroom",
                "minecraft:brown_mushroom",
                "minecraft:crimson_fungus",
                "minecraft:warped_fungus",
                "minecraft:crimson_nylium",
                "minecraft:warped_nylium"
        ));
    }

    public static void load() {
        File file = CONFIG_FILE.toFile();
        RefineConfig defaults = new RefineConfig();

        if (!file.exists()) {
            INSTANCE = defaults;
            save();
            System.out.println("[RefineConfig] Created default config at " + file.getAbsolutePath());
            return;
        }

        RefineConfig loaded = null;
        try (Reader reader = new FileReader(file)) {
            loaded = YAML.loadAs(reader, RefineConfig.class);
        } catch (Exception e) {
            System.err.println("[RefineConfig] Error reading YAML, will use defaults for missing parts.");
            e.printStackTrace();
        }

        if (loaded == null) {
            System.err.println("[RefineConfig] YAML parsed to null — using defaults.");
            INSTANCE = defaults;
            save();
            return;
        }

        INSTANCE = mergeObjectsWithLogging(loaded, defaults);

        save();
    }

    @SuppressWarnings("unchecked")
    private static <T> T mergeObjectsWithLogging(T loaded, T defaults) {
        if (defaults == null) return loaded;
        if (loaded == null) return defaults;

        Class<?> clazz = defaults.getClass();
        try {
            for (java.lang.reflect.Field field : clazz.getFields()) {
                Object loadedVal = field.get(loaded);
                Object defaultVal = field.get(defaults);
                String fullFieldName = clazz.getSimpleName() + "." + field.getName();

                if (loadedVal == null) {
                    field.set(loaded, defaultVal);
                    System.out.println("[RefineConfig] Filled missing field: " + fullFieldName + " -> " + stringify(defaultVal));
                    continue;
                }

                Class<?> type = field.getType();

                if (List.class.isAssignableFrom(type)) {
                    List<?> listLoaded = (List<?>) loadedVal;
                    if ((listLoaded == null || listLoaded.isEmpty()) && defaultVal != null) {
                        List<Object> listTarget = (List<Object>) field.get(loaded);
                        listTarget.clear();
                        listTarget.addAll((List<?>) defaultVal);
                        System.out.println("[RefineConfig] Filled empty list: " + fullFieldName + " -> " + stringify(defaultVal));
                    }
                } else if (!isPrimitiveOrWrapperOrString(type)
                        && !type.isEnum()
                        && !type.getName().startsWith("java.")) {
                    Object merged = mergeObjectsWithLogging(loadedVal, defaultVal);
                    field.set(loaded, merged);
                }
            }
        } catch (Exception e) {
            System.err.println("[RefineConfig] Error while merging config: " + e);
            e.printStackTrace();
        }
        return loaded;
    }

    private static String stringify(Object o) {
        if (o == null) return "null";
        return o.toString();
    }

    private static boolean isPrimitiveOrWrapperOrString(Class<?> type) {
        return type.isPrimitive()
                || type == Boolean.class
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == Float.class
                || type == Short.class
                || type == Byte.class
                || type == Character.class
                || type == String.class;
    }

    public static void save() {
        File file = CONFIG_FILE.toFile();
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            YAML.dump(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === 动态修改 ===
    @SuppressWarnings("unchecked")
    public void setValue(String key, Object value) {
        /*
         * Key: featureConfig.EnableEndermanGriefingBlacklist, Value: true
         */
        try {
            String[] parts = key.split("\\.");
            if (parts.length != 2) {
                System.err.println("[RefineConfig] setValue: invalid key format: " + key);
                return;
            }
            String module = parts[0];
            String fieldName = parts[1];

            Object targetModule;
            switch (module) {
                case "featureConfig": targetModule = featureConfig; break;
                case "endermanGriefingBlacklistConfig": targetModule = endermanGriefingBlacklistConfig; break;
                default:
                    System.err.println("[RefineConfig] setValue: unknown module: " + module);
                    return;
            }

            java.lang.reflect.Field field = targetModule.getClass().getField(fieldName);
            Class<?> type = field.getType();

            if ((type == Boolean.class || type == boolean.class) && value instanceof Boolean) {
                field.set(targetModule, value);
            } else if ((type == Integer.class || type == int.class) && value instanceof Number) {
                if (type == int.class) field.setInt(targetModule, ((Number) value).intValue());
                else field.set(targetModule, ((Number) value).intValue());
            } else if (type == String.class && value instanceof String) {
                field.set(targetModule, value);
            } else if (List.class.isAssignableFrom(type) && value instanceof List) {
                List<?> current = (List<?>) field.get(targetModule);
                current.clear();
                ((List<Object>) current).addAll((List<?>) value);
            } else {
                System.err.println("[RefineConfig] setValue: type mismatch for " + key + ", expected " + type.getSimpleName());
                return;
            }

            save();
        } catch (NoSuchFieldException nsf) {
            System.err.println("[RefineConfig] setValue: no such field: " + nsf.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
