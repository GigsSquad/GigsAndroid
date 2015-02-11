package pl.javaparty.jsoup;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SetList {
    private static final String[] searchUrl = {
            "http://www.setlist.fm/search?query=artist:%28", "%29+city:%28",
            "%29+country:%28Poland%29+date:"
            // date format: yyyy-mm-dd
    };

    private static Document innerUrl(String artist, String city, int d, int m,
                                     int y) throws IOException {
        artist = normalize(artist).replace(' ', '+');
        city = normalize(city);
        String url = searchUrl[0] + artist + searchUrl[1] + city + searchUrl[2]
                + y + "-" + m + "-" + d;
        Log.i("SET","inner: "+url);
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        }catch(Exception e){
            Log.i("SET","zły url, może nie ma na setlist");
            return null;
        }
        Log.i("SET","połączono");
        return doc.getElementsByClass("noResults").size() != 0 ? null : Jsoup
                .connect(
                        "http://www.setlist.fm/"
                                + doc.getElementsByClass("actions").first()
                                .select("a").attr("href")).get();
    }

    public static ArrayList<String> getSetlist(String artist,
                                               String city, int d, int m, int y) throws IOException {
        Log.i("SET","ogarniam "+artist+city);
        Log.i("SET","data:"+d+"."+m+"."+y);
        Document doc = innerUrl(artist, city, d, m, y);
        if (doc == null || doc.getElementsByClass("noSongs").size()!=0)
            return null;
        ArrayList<String> res= new ArrayList<String>();
        int i = 1;
        for (Element el : doc.getElementsByClass("songLabel")) {
            res.add(i++ + ". " + el.text());
        }
        Log.i("SET","znalazłem "+res.size()+" koncertów");
        return res;
    }

    public static String getYT(String artist, String song) throws IOException {
        Log.i("SET","YT start dla "+artist+song);
        Document doc = Jsoup
                .connect(
                        "https://www.youtube.com/results?search_query="
                                + artist + song).get();
        return "https://www.youtube.com"
                + doc.getElementsByClass("yt-lockup-title").first().select("a")
                .attr("href");
    }

    private static String normalize(String text) {
        char[] letters = text.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            switch (letters[i]) {
                case 'ą':
                    letters[i] = 'a';
                    break;
                case 'ć':
                    letters[i] = 'c';
                    break;
                case 'ę':
                    letters[i] = 'e';
                    break;
                case 'ł':
                    letters[i] = 'l';
                    break;
                case 'ń':
                    letters[i] = 'n';
                    break;
                case 'ó':
                    letters[i] = 'o';
                    break;
                case 'ś':
                    letters[i] = 's';
                    break;
                case 'ż':
                case 'ź':
                    letters[i] = 'z';
                    break;
                case 'Ą':
                    letters[i] = 'A';
                    break;
                case 'Ć':
                    letters[i] = 'C';
                    break;
                case 'Ę':
                    letters[i] = 'E';
                    break;
                case 'Ł':
                    letters[i] = 'L';
                    break;
                case 'Ń':
                    letters[i] = 'N';
                    break;
                case 'Ó':
                    letters[i] = 'O';
                    break;
                case 'Ś':
                    letters[i] = 'S';
                    break;
                case 'Ż':
                case 'Ź':
                    letters[i] = 'Z';
                    break;
            }
        }
        return new String(letters);
    }

}
