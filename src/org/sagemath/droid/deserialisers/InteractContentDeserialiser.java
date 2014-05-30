package org.sagemath.droid.deserialisers;

import com.google.gson.*;
import org.sagemath.droid.models.InteractContent;
import org.sagemath.droid.models.InteractData;

import java.lang.reflect.Type;

public class InteractContentDeserialiser implements JsonDeserializer<InteractContent> {

    private static String KEY_SOURCE = "source";
    private static String KEY_DATA = "data";

    @Override
    public InteractContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        String source = jsonObject.get(KEY_SOURCE).getAsString();
        JsonElement data = jsonObject.get(KEY_DATA);

        InteractData interactData = context.deserialize(data, InteractData.class);

        final InteractContent content = new InteractContent();
        content.setData(interactData);
        content.setSource(source);

        return content;
    }
}
