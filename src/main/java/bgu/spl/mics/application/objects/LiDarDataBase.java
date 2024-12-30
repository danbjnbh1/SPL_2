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

    // Singleton instance
    private static LiDarDataBase instance;

    // List to hold parsed LiDAR data
    private List<StampedCloudPoints> cloudPoints;

    /**
     * Private constructor to prevent direct instantiation.
     *
     * @param filePath The path to the LiDAR data file.
     */
    private LiDarDataBase(String path) {
        cloudPoints = parseCloudPoints(path);
    }

    /**
     * Parses the JSON file containing LiDAR data into a list of StampedCloudPoints.
     *
     * @param filePath The path to the JSON file.
     * @return A list of StampedCloudPoints objects.
     */
    private List<StampedCloudPoints> parseCloudPoints(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(path)) {
            Type type = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("Error reading the LiDAR data file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static synchronized LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }
        return instance;
    }
}
