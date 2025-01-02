package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.OutputData;
import bgu.spl.mics.application.objects.StatisticalFolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputWriter {
    public static void writeOutput(String configPath) {
        // Gather statistics
        StatisticalFolder stats = StatisticalFolder.getInstance();
        OutputData.Statistics statistics = new OutputData.Statistics(
                stats.getSystemRuntime(),
                stats.getNumDetectedObjects(),
                stats.getNumTrackedObjects(),
                stats.getNumLandmarks()
        );

        // Gather world map data
        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<LandMark> landMarksList = fusionSlam.getLandmarks();

        // Sort landmarks by id
        Collections.sort(landMarksList, Comparator.comparing(LandMark::getId));

        // Convert list to map
        Map<String, LandMark> landMarksMap = new HashMap<>();
        for (LandMark landMark : landMarksList) {
            landMarksMap.put(landMark.getId(), landMark);
        }

        // Create output data
        OutputData outputData = new OutputData(statistics, landMarksMap);

        // Write to JSON file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path configAbsolutePath = Paths.get(configPath).toAbsolutePath().getParent();
        Path outputPath = configAbsolutePath.resolve("output_file1.json");

        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            gson.toJson(outputData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}