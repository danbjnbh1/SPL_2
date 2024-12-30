package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CameraDataBase {
    private Map<String, List<StampedDetectedObjects>> cameraDataMap;

    public CameraDataBase(String path) {
        parseCameras(path);

    }

    private void parseCameras(String path) {
        try (FileReader reader = new FileReader(path)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
            cameraDataMap = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<StampedDetectedObjects> getCameraData(String cameraKey) {
        return cameraDataMap.get(cameraKey);
    }

}
