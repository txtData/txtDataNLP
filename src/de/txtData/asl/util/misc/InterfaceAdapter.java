/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.misc;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Used in conjunction with GSON to serialize and deserialize classes that implement interfaces.
 */
public final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("iType", object.getClass().getName());
        wrapper.add("iData", context.serialize(object));
        return wrapper;
    }

    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement typeName = get(wrapper, "iType");
        final JsonElement data = get(wrapper, "iData");
        final Type actualType = typeForName(typeName);
        return context.deserialize(data, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement elem = wrapper.get(memberName);
        if (elem == null) throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
        return elem;
    }

    public static <I> String serialize(Object object, Class<I> classOfInterface){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(classOfInterface, new InterfaceAdapter<I>())
                .setPrettyPrinting()
                .create();
        return gson.toJson(object);
    }

    public static <S,I> S deserialize(String json, Class<S> classOfT, Class<I> classOfInterface){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(classOfInterface, new InterfaceAdapter<I>())
                .create();
        return gson.fromJson(json, classOfT);
    }
}
