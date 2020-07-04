import java.util.Arrays;
import java.util.List;

public class VideoParseFactory {

    private static final List<Class<? extends IVideoParse>> PARSES = Arrays.asList(DouyinVideoParse.class);

    public static VideoInfo parse(String link, IVideoParse.ParseListener listener) throws Exception {
        for (Class<? extends IVideoParse> pars : PARSES) {
            try {
                IVideoParse videoLink = pars.newInstance();
                videoLink.setParseListener(listener);
                return videoLink.getVideoInfo(link);
            } catch (Exception e) {
                listener.onParse(e.toString());
            }
        }

        throw new IllegalStateException("can't find available video parse. link:" + link);
    }

}
