package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

    private double x;
    private double y;
    private double z;

    /**
     * Constructor for CloudPoint.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param z The z-coordinate of the point.
     */

     public CloudPoint(double x, double y, double z) {
         this.x = x;
         this.y = y;
     }

     public double getX() {
         return x;
     }

     public double getY() {
         return y;
     }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

}
