package bert.trafficapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by bert on 26/10/2017.
 */

final class Utils {

    private Utils(){}

    public static JSONObject loadJSONFromAsset(Context context, String file) throws IOException, JSONException {
        JSONObject json = new JSONObject();
        InputStream is = null;
        try {
            is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new JSONObject(new String(buffer, "UTF-8"));
        } finally {
            if (is != null)
                is.close();
        }
        return json;
    }

    /**
     * Update the content provider from the json data. This data has a lot of errors and needs to be
     * as robust as possible therefor we allow empty strings if the keys are not found.
     * @param result
     * @throws JSONException
     */
    public static List<TrafficNotification> parseJSON(JSONObject result) throws JSONException {
        List<TrafficNotification> list = new ArrayList<>();
        JSONArray json = result.getJSONArray("result");
        TrafficNotification notification;
        for (int i=0; i<json.length(); i++) {
            JSONObject entity = json.getJSONObject(i);
            JSONObject parent = entity;
            if (entity.has("objectsToStore")) // to fix some strange data of tweets
                entity = entity.optJSONArray("objectsToStore").optJSONObject(0) != null ? entity.optJSONArray("objectsToStore").optJSONObject(0) : entity;

            JSONObject payload = entity.optJSONObject("payload"); // can be null
            if (payload == null) {
                payload = new JSONObject("{}"); // fix bad data
            }
            JSONObject sourcePayload = entity.optJSONObject("sourcePayload"); // can be null
            if (sourcePayload == null)
                sourcePayload = new JSONObject("{}"); // fix bad data

            String name = entity.optString("alarmName");
            String type = entity.optString("type");
            String source = entity.has("source") ? entity.getString("source") : payload.optString("source");
            String transport = entity.optString("transport");
            float latitude = (float) payload.optDouble("latitude");
            float longitude = (float) payload.optDouble("longitude");
            Date date = new Date(parent.optLong("timestamp"));

            UUID id = null;
            String message = null;
            try {
                if (source.equalsIgnoreCase("waze")) {
                    id = UUID.fromString(sourcePayload.optString("uuid", null));
                    message =  payload.optString("message",null);
                } else if (source.equalsIgnoreCase("irail")) {
                    message =  payload.optString("message", null);
                    id = UUID.fromString(String.format(Locale.getDefault(), "%d-%f-%f", parent.optLong("timestamp"), parent.optDouble("longitude"), parent.optDouble("latitude"))); // no real ID on iRail
                } else if (source.equalsIgnoreCase("coyote")) {
                    id = UUID.fromString(payload.optString("id",null));
                    message = String.format(Locale.getDefault(), "Speed limit of %d km/h on %s.", payload.optInt("speed_limit", -1), payload.optString("road_name", "unknown")); // no real message for coyote
                }
            } catch (Exception ex) {
            } finally {
                // fix bad json
                if (id == null) {
                    id = UUID.randomUUID();
                }
                if (message == null)
                    message = "No message included.";
            }

            notification = new TrafficNotification(id);
            notification.setName(name);
            notification.setType(type);
            notification.setSource(source);
            notification.setTransport(transport);
            notification.setLatitude(latitude);
            notification.setLongitude(longitude);
            notification.setDate(date);
            notification.setMessage(message);
            list.add(notification);
        }
        return list;
    }
}
