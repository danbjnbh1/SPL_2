package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for
 * tracked objects.
 */
public class LiDarDataBase {
    private static String path;

    private static class LiDarDataBaseHolder {
        private static final LiDarDataBase instance = new LiDarDataBase(path);
    }

    // List to hold parsed LiDAR data
    private List<StampedCloudPoints> cloudPoints;
    private AtomicInteger numOfConsumedCloudPoints = new AtomicInteger(0);

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
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<JsonObject>>() {
            }.getType();
            List<JsonObject> jsonObjects = gson.fromJson(reader, type);
            List<StampedCloudPoints> stampedCloudPointsList = new ArrayList<>();
            for (JsonObject jsonObject : jsonObjects) {
                int time = jsonObject.get("time").getAsInt();
                String id = jsonObject.get("id").getAsString();
                JsonArray cloudPointsArray = jsonObject.getAsJsonArray("cloudPoints");
                List<CloudPoint> cloudPoints = new ArrayList<>();
                for (JsonElement element : cloudPointsArray) {
                    JsonArray pointArray = element.getAsJsonArray();
                    double x = pointArray.get(0).getAsDouble();
                    double y = pointArray.get(1).getAsDouble();
                    cloudPoints.add(new CloudPoint(x, y));
                }
                stampedCloudPointsList.add(new StampedCloudPoints(cloudPoints, time, id));
            }
            return stampedCloudPointsList;
        } catch (IOException e) {
            System.err.println("Error reading the LiDAR data file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance() {
        if (path == null) {
            throw new IllegalStateException("LiDarDataBase has not been initialized.");
        }
        return LiDarDataBaseHolder.instance;
    }

    public static void init(String path) {
        LiDarDataBase.path = path;
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    public List<StampedCloudPoints> getListOfStampedCloudPointsByTime(int time) {
        List<StampedCloudPoints> stampedCloudPointsList = new ArrayList<>();
        for (StampedCloudPoints stampedCloudPoint : cloudPoints) {
            if (stampedCloudPoint.getTime() == time) {
                stampedCloudPointsList.add(stampedCloudPoint);
            }
        }
        return stampedCloudPointsList;
    }

    public StampedCloudPoints getLastPoint() {
        return cloudPoints.get(cloudPoints.size() - 1);
    }

    public void incrementNumOfConsumedCloudPoints() {
        numOfConsumedCloudPoints.incrementAndGet();
    }

    public AtomicInteger getNumOfConsumedCloudPoints() {
        return numOfConsumedCloudPoints;
    }
}
