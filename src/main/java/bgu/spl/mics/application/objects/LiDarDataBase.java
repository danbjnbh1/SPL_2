package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.MessageBusImpl;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    private List<StampedCloudPoints> cloudPoints;
    private static LiDarDataBase instance;

    private LiDarDataBase(String path) {
        cloudPoints = parseCloudPoints(path);
    }

    public List<StampedCloudPoints> parseCloudPoints(String path) {
        Gson gson = new Gson();
        try (FileReader readrer = new FileReader(path)){
            Type type = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            return gson.fromJson(readrer, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static synchronized LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }
        return instance;
    }
}
