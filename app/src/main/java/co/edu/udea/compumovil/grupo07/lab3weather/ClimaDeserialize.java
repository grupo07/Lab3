package co.edu.udea.compumovil.grupo07.lab3weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Created by Usuario on 26/09/2016.
 */

public class ClimaDeserialize implements JsonDeserializer<Clima>{

    @Override
    public Clima deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        Clima clima=null;
        int responseCode = jsonObject.get("cod").getAsInt();
        if(responseCode==200) {
            JsonArray jsonDescriptionArray = jsonObject.getAsJsonArray("weather");
            String descripcion = jsonDescriptionArray.get(0).getAsJsonObject().get("description").getAsString();
            String icono = jsonDescriptionArray.get(0).getAsJsonObject().get("icon").getAsString();
            JsonObject jsonMain = jsonObject.get("main").getAsJsonObject();
            double temperatura = jsonMain.get("temp").getAsDouble();
            int humedad = jsonMain.get("humidity").getAsInt();
            clima = new Clima();
            clima.setDescripcion(descripcion);
            clima.setHumedad(humedad);
            clima.setTemperatura(temperatura);
            clima.setIcono(icono);
        }
        return clima;
    }
}
