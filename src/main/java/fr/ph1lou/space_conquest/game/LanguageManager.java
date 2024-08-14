package fr.ph1lou.space_conquest.game;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.utils.FileUtils_;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LanguageManager {

    private final Map<String, JsonValue> language;

    public LanguageManager(Main main) {
        language = loadTranslations(FileUtils_.loadContent(buildLanguageFile(main)));
    }

    private Map<String, JsonValue> loadTranslations(String file) {
        try {
            JsonObject jsonObject = Json.parse(file).asObject();
            return this.loadTranslationsRec("", jsonObject, new HashMap<>());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new HashMap<>();
    }

    private Map<String, JsonValue> loadTranslationsRec(String currentPath, JsonValue jsonValue, Map<String, JsonValue> keys) {
        // This value is an object - it means she contains sub-section that should be analyzed
        if (jsonValue.isObject()) {

            // For each child
            for (JsonObject.Member member : jsonValue.asObject()) {

                String newPath = String.format("%s%s%s", currentPath, currentPath.equals("") ? "" : ".", member.getName());

                this.loadTranslationsRec(newPath, member.getValue(), keys);
            }
        }

        else if (!jsonValue.isNull()) {
            keys.put(currentPath.toLowerCase(), jsonValue);
        }

        return keys;
    }


    private File buildLanguageFile(Plugin plugin) {


        File file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, "fr.json");

        if (!file.exists()) {
            FileUtils_.copy(plugin.getResource("fr.json"), plugin.getDataFolder() + File.separator + "languages" + File.separator + "fr" + ".json");
            return new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, "fr.json");
        } else {
            String defaultText = FileUtils_.convert(plugin.getResource("fr.json"));
            Map<String, JsonValue> fr = loadTranslations(defaultText);
            Map<String, JsonValue> custom = loadTranslations(FileUtils_.loadContent(file));
            JsonObject jsonObject = Json.parse(FileUtils_.loadContent(file)).asObject();

            for (String string : fr.keySet()) {
                if (!custom.containsKey(string)) {
                    JsonObject temp = jsonObject;
                    String tempString = string;
                    while (temp.get(tempString.split("\\.")[0]) != null) {
                        String temp2 = tempString.split("\\.")[0];
                        tempString = tempString.replaceFirst(temp2 + "\\.", "");
                        temp = temp.get(temp2).asObject();
                    }
                    String[] strings =tempString.split("\\.");

                    for (int i=0;i<strings.length-1;i++){
                        temp.set(strings[i],new JsonObject());
                        temp = temp.get(strings[i]).asObject();
                    }
                    temp.set(strings[strings.length - 1], fr.get(string));
                }
            }
            FileUtils_.saveJson(file, jsonObject);
        }
        return file;
    }

    public List<String> getTranslationList(String key) {

        if (!language.containsKey(key) || !language.get(key).isArray()) {
            return Collections.singletonList("Array Message error");
        }
        return language.get(key)
                .asArray().values()
                .stream().filter(JsonValue::isString)
                .map(JsonValue::asString)
                .collect(Collectors.toList());
    }

    public String getTranslation(String key) {

        if (!language.containsKey(key) || !language.get(key).isString()) {
            return String.format("Message error (%s) ", key.toLowerCase());
        }
        return language.get(key).asString();
    }
}
