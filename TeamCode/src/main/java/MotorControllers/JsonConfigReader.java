package MotorControllers;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jeremy on 8/7/2017.
 */

/*
    A class to help us more easily use .json files with java
 */
public class JsonConfigReader {
    private JsonParser parser;
    private JSONObject jsonObject;

    public JsonConfigReader(InputStream stream){
        parser = new JsonParser();
        try{
            jsonObject = parseJSONData(stream);
        }
        catch(Exception e){
            //TODO implement something
        }
    }
    private JSONObject parseJSONData(InputStream inputStream) {
        String JSONString = null;
        JSONObject JSONObject = null;
        try {
            int sizeOfJSONFile = inputStream.available();
            byte[] bytes = new byte[sizeOfJSONFile];
            inputStream.read(bytes);
            inputStream.close();
            JSONString = new String(bytes, "UTF-8");
            JSONObject = new JSONObject(JSONString);
        } catch (IOException ex) {
             return null;
        }
        catch (JSONException x) {
            x.printStackTrace();
            return null;
        }
        return JSONObject;
    }
    public String getString(String n) throws Exception{
        return (String) jsonObject.get(n);
    }

    public int getInt(String n) throws Exception{
        return jsonObject.getInt(n);
    }

    public double getDouble(String n) throws Exception{
        return jsonObject.getDouble(n);
    }

    public long getLong(String n) throws Exception{
        return jsonObject.getLong(n);
    }

    public boolean getBoolean(String n) throws Exception{
        return jsonObject.getBoolean(n);
    }
}
