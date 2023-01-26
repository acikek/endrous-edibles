package com.acikek.ended.load;

import com.acikek.ended.EndrousEdibles;
import com.acikek.ended.api.impl.EndrousEdiblesAPIImpl;
import com.acikek.ended.edible.Edible;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class EdibleLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {

    public static Map<Identifier, Edible> loadedEdibles = new HashMap<>();
    public static long lastReloadTime;

    public static final String TYPE = "edibles";
    public static final Identifier ID = EndrousEdibles.id(TYPE);

    public EdibleLoader() {
        super(new Gson(), TYPE);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        loadedEdibles.clear();
        int loaded = 0;
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();
                loadedEdibles.put(entry.getKey(), Edible.fromJson(entry.getKey(), obj));
                loaded++;
            }
            catch (Exception e) {
                EndrousEdibles.LOGGER.error("Error when deserializing edible '" + entry.getKey() + "': ", e);
            }
        }
        EndrousEdiblesAPIImpl.refreshAllEdibles();
        lastReloadTime = System.currentTimeMillis();
        EndrousEdibles.LOGGER.info("Loaded " + loaded + " endrous edible" + (loaded != 1 ? "s" : ""));
        System.out.println(loadedEdibles);
    }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new EdibleLoader());
    }
}
