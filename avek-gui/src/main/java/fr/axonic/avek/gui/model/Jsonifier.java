package fr.axonic.avek.gui.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.axonic.base.engine.AEntity;
import fr.axonic.base.engine.AList;
import fr.axonic.base.engine.AVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nathaël N on 12/07/16.
 */
class Jsonifier<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jsonifier.class);
    private final Class<T> tClass;

    Jsonifier(Class<T> tClass) {
        this.tClass = tClass;
    }

    static String toJson(Object o) {
        LOGGER.debug("Object to JSON:" + o);
        return correct(new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(o));
    }

    T fromJson(String s) {
        LOGGER.debug("Creating new " + tClass.getTypeName() + " from Json");
        return new GsonBuilder()
                .create()
                .fromJson(s, tClass);
    }

    static String fromAEntity(AEntity entity) {
        JsonObject json = new JsonObject();
        json.addProperty("class_name", entity.getClass().getCanonicalName());

        if (entity instanceof AList) {
            @SuppressWarnings("unchecked")
            AList<AEntity> list = (AList<AEntity>) entity;
            JsonArray array = new JsonArray();

            for (AEntity ae : list.getList()) {
                array.add(new JsonParser().parse(fromAEntity(ae)));
            }

            json.add("value", array);
            if (list.getLabel() != null) {
                json.addProperty("label", list.getLabel());
            }
        } else {
            json.add("value", new JsonParser().parse(toJson(entity)).getAsJsonObject());
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }

    static AEntity toAEntity(String src) {
        JsonObject element = new JsonParser().parse(src).getAsJsonObject();
        String type = element.get("class_name").getAsString();

        if (type.equals(AList.class.getName())) {
            JsonArray list = element.get("value").getAsJsonArray();
            AList alAE = new AList();

            for (int i = 0; i < list.size(); i++) {
                //noinspection unchecked
                alAE.add(toAEntity(list.get(i).toString()));
            }

            if (element.has("label")) {
                alAE.setLabel(element.get("label").getAsString());
            }
            return alAE;
        } else {
            try {
                return (AEntity) new Jsonifier<>(Class.forName(type)).fromJson(element.get("value").toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            JsonObject object = element.get("value").getAsJsonObject();
            if (object.has("format")) {
                return new Jsonifier<>(AVar.class).fromJson(element.get("value").toString());
            }
        }

        return null;
    }

    private static String correct(String json) {
        return json.replaceAll("([0-9]*)\\.0([^0-9])", "$1$2");
    }

}
