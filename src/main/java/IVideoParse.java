import java.util.List;

public interface IVideoParse {

    VideoInfo getVideoInfo(String link) throws Exception;

    static interface ParseListener {

        void onParse(String message);

    }

    void setParseListener(ParseListener listener);

}
