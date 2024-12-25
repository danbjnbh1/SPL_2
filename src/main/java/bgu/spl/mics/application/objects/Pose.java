package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    
    private int time;
    private double x;
    private double y;
    private double yaw;
    
    public Pose() {}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    } 

    public double getYaw() {
        return yaw;
    }

    public int getTime() {
        return time;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }   

    public void setTime(int time) {
        this.time = time;
    }   

    @Override
    public String toString() {
        return "Pose{" +
                "time=" + time +
                ", x=" + x +
                ", y=" + y +
                ", yaw=" + yaw +
                '}';
    }
}
