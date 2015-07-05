import java.io.IOException;
import java.sql.SQLException;

public class Iterator {

    public static final String URL = "http://www.goodreads.com/";
    public static final String SEARCH_TERM = "Dan Brown";
    public DB db = new DB();

    public Iterator () {

        try {
            Crawler crawler = new Crawler(this.db);
        } catch (SQLException | IOException e) {
            //Do nothing
            System.out.println(e.toString());
        }


    }


    public static void main (String[] args) {

        Iterator iterator = new Iterator();

        System.out.println("Complete");

    }

}
