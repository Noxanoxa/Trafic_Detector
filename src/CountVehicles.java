import jxl.write.WriteException;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/**
 * The CountVehicles class is responsible for detecting and classifying vehicles in a video feed,
 * and determining when a vehicle crosses the counting line or the speed line.
 * It uses the OpenCV library for image processing.
 */
public class CountVehicles {
    // Fields for storing image, contours and points for drawing lines
    private Mat image;
    public List<MatOfPoint> goodContours = new ArrayList<MatOfPoint>();
    private int areaThreshold;
    private int vehicleSizeThreshold;
    private Point lineCount1;
    private Point lineCount2;
//    private Point lineCount3;
    private Point lineSpeed1;
    private Point lineSpeed2;
    CheckCrossLine checkRectLine;
    CheckCrossLine checkSpeedLine;
    boolean countingFlag = false;
    boolean speedFlag = false;
    boolean crossingLine;
    boolean crossingSpeedLine;
    MatOfPoint contourVehicle;

    /**
     * Constructor for the CountVehicles class.
     * Initializes the fields with the provided parameters.
     */
    public CountVehicles(int areaThreshold, int vehicleSizeThreshold, Point lineCount1, Point lineCount2, Point lineSpeed1, Point lineSpeed2, boolean crossingLine, boolean crossingSpeedLine) {
        this.areaThreshold = areaThreshold;
        this.vehicleSizeThreshold = vehicleSizeThreshold;
        this.lineCount1 = lineCount1;
        this.lineCount2 = lineCount2;
//        this.lineCount3 = lineCount3;
        this.lineSpeed1 = lineSpeed1;
        this.lineSpeed2 = lineSpeed2;
        this.crossingLine = crossingLine;
        this.crossingSpeedLine = crossingSpeedLine;
        this.checkRectLine = new CheckCrossLine(lineCount1, lineCount2);
        this.checkSpeedLine = new CheckCrossLine(lineSpeed1, lineSpeed2);
    }

    /**
     * Method for finding and drawing contours on the image.
     * Uses the OpenCV library to find contours in the binary image.
     * Draws lines on the image and adds contours that have an area greater than a certain threshold to the goodContours list.
     */

    public Mat findAndDrawContours(Mat image, Mat binary) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        this.image = image;
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.CHAIN_APPROX_NONE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.line(image, lineCount1, lineCount2, new Scalar(255, 255, 255), 1);
//        Imgproc.line(image, lineCount1, lineCount2, new Scalar(255, 0, 255), 1);
        Imgproc.line(image, lineSpeed1, lineSpeed2, new Scalar(255, 255, 0), 1);

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);

            if (currentArea > areaThreshold) {
                goodContours.add(contours.get(i));
                drawBoundingBox(currentContour);
            }
        }

        return image;
    }

    /**
     * Method for checking if a vehicle should be added to the count.
     * Checks if any of the good contours contain the line for counting vehicles.
     * If a contour does contain the line and the line was not previously crossed, the method returns true.
     */
    public boolean isVehicleToAdd() {
        for (int i = 0; i < goodContours.size(); i++) {
            Rect rectangle = Imgproc.boundingRect(goodContours.get(i));
            if (checkRectLine.rectContainLine(rectangle)) {
                contourVehicle = getGoodContours().get(i);
                countingFlag = true;
                break;
            }
        }
        if (countingFlag == true) {
            if (crossingLine == false) {
                crossingLine = true;
                return true;
            } else {

                return false;
            }
        } else {
            crossingLine = false;
            return false;
        }
    }

    /**
     * Method for classifying the vehicle based on the area of the contour.
     * If the area is less than or equal to a certain threshold, it is classified as a "Car".
     * If the area is less than or equal to 1.9 times the threshold, it is classified as a "Van".
     * Otherwise, it is classified as a "Lorry".
     */
    public String classifier() {
        double currentArea = Imgproc.contourArea(contourVehicle);
        if (currentArea <= (double) vehicleSizeThreshold)
            return "Car";
        else if (currentArea <= 1.9 * (double) vehicleSizeThreshold)
            return "Van";
        else return "Lorry";
    }

    /**
     * The drawBoundingBox method draws a bounding box around the given contour on the image.
     * The color of the bounding box is specified by the Scalar object (255, 100, 10),
     * which represents the color in BGR format.
     * @param currentContour
     */
    private void drawBoundingBox(MatOfPoint currentContour) {
        Rect rectangle = Imgproc.boundingRect(currentContour);
        Imgproc.rectangle(image, rectangle.tl(), rectangle.br(), new Scalar(255, 100, 10), 1);

    }
    /**
     * The isToSpeedMeasure method checks if a vehicle's speed should be measured.
     * It does this by checking if any of the good contours contain the line for measuring speed.
     * If a contour does contain the line and the line was not previously crossed,
     * the method returns true.
     * If the line was previously crossed, the method returns false.
     * @return boolean
     */


    public boolean isToSpeedMeasure() {
        for (int i = 0; i < goodContours.size(); i++) {
            Rect rectangle = Imgproc.boundingRect(goodContours.get(i));
            if (checkSpeedLine.rectContainLine(rectangle)) {
                speedFlag = true;
                break;
            }
        }
        if (speedFlag == true) {
            if (crossingSpeedLine == false) {
                crossingSpeedLine = true;
                return true;
            } else {
                return false;
            }
        } else {
            crossingSpeedLine = false;
            return false;
        }
    }

    /**
     * The isCrossingSpeedLine and isCrossingLine methods
     * return the current state of crossingSpeedLine
     * and crossingLine respectively.These methods are used to check
     * if a vehicle is currently crossing the speed line or the counting line.
     * @return
     */
    public boolean isCrossingSpeedLine() {
        return crossingSpeedLine;
    }

    public boolean isCrossingLine() {
        return crossingLine;
    }

    public List<MatOfPoint> getGoodContours() {
        return goodContours;
    }

    /**
     * In summary, the CountVehicles class is responsible
     * for detecting and classifying vehicles in a video feed,
     * and determining when a vehicle crosses the counting line or the speed line.
     */

}
