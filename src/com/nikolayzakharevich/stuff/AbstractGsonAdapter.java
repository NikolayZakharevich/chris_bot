package com.nikolayzakharevich.stuff;

import com.google.gson.*;

import java.lang.reflect.Type;

public class AbstractGsonAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject member = (JsonObject) json;
        final JsonElement typeString = get(member, "type");
        final JsonElement data = get(member, "data");
        final Type actualType = typeForName(typeString);

        return context.deserialize(data, actualType);
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {

        final JsonObject member = new JsonObject();

        member.addProperty("type", src.getClass().getName());

        member.add("data", context.serialize(src));

        return member;
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, final String memberName) {
        final JsonElement elem = wrapper.get(memberName);

        if (elem == null) {
            throw new JsonParseException(
                    "no '" + memberName + "' member found in json file.");
        }
        return elem;
    }
}
