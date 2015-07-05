//Imports
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.SocketTimeoutException;
import java.net.MalformedURLException;
import java.lang.IllegalArgumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;

//Web crawler class
public class Crawler {

    //Instantiation of database object
    public static DB db = new DB();

    //Main method
    public static void main(String[] args) throws SQLException, IOException {
        //Clear database
        db.runSql2("TRUNCATE _records;");
        //Begin crawling
        processPage("http://www.goodreads.com/");
    }


    public static void processPage(String URL) throws SQLException, IOException{
        //Check database for URLe
        String sql = "select * from _records where URL = '"+URL+"'";
        ResultSet rs = db.runSql(sql);

        //If there's something in the first row of the result set
        if(rs.next()){
            //Do nothing
        }else{
            //Store the URL to database to avoid parsing again
            sql = "INSERT INTO _dipre._records " + "(URL) VALUES " + "(?);";
            PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, URL);
            stmt.execute();

            //Get page contents
            try {
                Document doc = Jsoup.connect(URL).get();

                //Check for string
                if(doc.text().contains("Dan Brown")){
                    System.out.println("Positive - " + URL);
                }

                //Get all links
                Elements questions = doc.select("a[href]");

                //For each link that contains mit.edu, recursively call the processPage method
                for(Element link: questions){
                    String Link = link.attr("abs:href");
                    Link = trimURL(Link, '?');
                    Link = trimURL(Link, '#');

                    if(Link.contains("http://www.goodreads.com/"))
                        processPage(Link);
                }
            } catch (SocketTimeoutException | HttpStatusException | UnsupportedMimeTypeException | IllegalArgumentException | MalformedURLException e) {
                //Do nothing
            }
        }
    }

    //Trim parameters and anchors from the end of the URL
    public static String trimURL(String URL, char trimCharacter) {
        //Get the index of ? or #
        int trimIndex = URL.indexOf(trimCharacter);

        //If found in string, create substring
        if (trimIndex != -1) {
            URL = URL.substring(0, trimIndex);
        }

        return URL;
    }
}