import org.opencv.core.Mat;

/**
 * The VideoProcessor interface is responsible for processing a video.
 */
public interface VideoProcessor {
    Mat process(Mat inputImage);

    void setImageThreshold(double imageThreshold);

    void setHistory(int history);

}
