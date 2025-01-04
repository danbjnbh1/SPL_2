package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ErrorOutputData;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputWriter {
    public static void writeOutput(String configPath) {
        StatisticalFolder stats = StatisticalFolder.getInstance();

        List<LandMark> landMarksList = FusionSlam.getInstance().getLandmarks();

        // Convert list to map
        Map<String, LandMark> landMarksMap = new HashMap<>();
        for (LandMark landMark : landMarksList) {
            landMarksMap.put(landMark.getId(), landMark);
        }

        OutputData outputData = OutputData.getInstance();
        ErrorOutputData errorOutputData = ErrorOutputData.getInstance();

        if (errorOutputData.getError() != null) {
            ErrorOutputData.Statistics statistics = new ErrorOutputData.Statistics(
                    stats.getSystemRuntime(),
                    stats.getNumDetectedObjects(),
                    stats.getNumTrackedObjects(),
                    stats.getNumLandmarks(),
                    landMarksMap

            );
            errorOutputData.setStatistics(statistics);

            // Write to JSON file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Path configAbsolutePath = Paths.get(configPath).toAbsolutePath().getParent();
            Path outputPath = configAbsolutePath.resolve("error_output1.json");

            try (FileWriter writer = new FileWriter(outputPath.toFile())) {
                gson.toJson(errorOutputData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            OutputData.Statistics statistics = new OutputData.Statistics(
                    stats.getSystemRuntime(),
                    stats.getNumDetectedObjects(),
                    stats.getNumTrackedObjects(),
                    stats.getNumLandmarks());
            outputData.setStatistics(statistics);
            outputData.setLandMarks(landMarksMap);

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
        // Gather statistics

    }
}