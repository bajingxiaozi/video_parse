import java.util.List;

public class VideoInfo {

    private final String downloadLink;
    private final String description;

    public VideoInfo(String downloadLink, String description) {
        this.downloadLink = downloadLink;
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getDescription() {
        return description;
    }

}
