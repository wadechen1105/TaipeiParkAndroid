package app.android.wade.taipeiparks;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * DTO for park info
 */
public class ParksInfo {

    @SerializedName("_id")
    private String _id;

    @SerializedName("ParkName")
    private String parkName;

    @SerializedName("Name")
    private String viewSpot;

    @SerializedName("YearBuilt")
    private String yearBuilt;

    @SerializedName("Image")
    private String imageUrl;

    @SerializedName("Introduction")
    private String introduction;


    public static ArrayList<ParksInfo> fromJsonString(String json) {
        ArrayList<ParksInfo> detailData = null;

        if (json == null) return null;

        try {
            JSONObject rootJObj = new JSONObject(json);
            JSONObject usefulJObj = rootJObj.getJSONObject("result");

            String jsonArr = usefulJObj.getJSONArray("results").toString();
            Type listType = new TypeToken<ArrayList<ParksInfo>>() {
            }.getType();
            Gson gson = new Gson();
            detailData = gson.fromJson(jsonArr, listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detailData;
    }

    public String getId() {
        return _id;
    }

    public String getParkName() {
        return parkName;
    }

    public String getViewSpot() {
        return viewSpot;
    }

    public String getYearBuilt() {
        return yearBuilt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

}
