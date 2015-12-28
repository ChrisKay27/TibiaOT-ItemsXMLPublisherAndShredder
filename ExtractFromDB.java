
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;

public class ExtractFromDB {

    private static String sqlUser = "databaseUsername";
    private static String sqlPass = "databasePassword";
    private static String database = "databasename";
    private static String pathToItemsXml = "C:\path to\items2.xml";


    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost/"+database+"?" +
                            "user="+sqlUser+"&password="+sqlPass);
        } catch (SQLException ex) {
            // handle any errors

            System.err.println("SQLException: " + ex.getMessage());
            System.err.println("SQLState: " + ex.getSQLState());
            System.err.println("VendorError: " + ex.getErrorCode());
            System.exit(1);
        }

        String item = "\t<item ", ATTR = "\t\t<attribute ",ENDITEM = "\t</item>\n";
        String ID = "id", NAME = "name", FROMID = "fromid", TOID = "toid", ARTICLE = "article", KEY = "key", VALUE = "value", CHANCE = "chance", RANDOM_MIN="random_min", RANDOM_MAX="random_max";
        String EQ = "=\"",CLOSE = "\" ",ENDSLASH="/>\n",END=">\n";


        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(pathToItemsXml));
            writer.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
            writer.write("<items>\n");

            stmt = conn.createStatement();
            String query = "SELECT * FROM items;";

            //System.out.println(query);
            ResultSet items = stmt.executeQuery(query);

            while(items.next()){

                int id = items.getInt(ID);
                String name = items.getString(NAME);
                int fromid = items.getInt(FROMID);

                int toid = items.getInt(TOID);
                String article = items.getString(ARTICLE);

                writer.write(item);

                if( id == fromid )
                    writer.write(FROMID+EQ+fromid+CLOSE);
                else
                    writer.write(ID+EQ+id+CLOSE);

                if( toid != -1 )
                    writer.write(TOID+EQ+toid+CLOSE);

                if( article != null )
                    writer.write(ARTICLE+EQ+article+CLOSE);

                if( name != null )
                    writer.write(NAME+EQ+name+CLOSE);



                boolean hasAttributes = false;
                stmt = conn.createStatement();
                ResultSet attributes = stmt.executeQuery("SELECT * FROM item_attributes WHERE item_id="+id+";");

                while( attributes.next() ){
                    if( !hasAttributes ) {
                        writer.write(END);
                        hasAttributes = true;
                    }

                    writer.write(ATTR);


                    String key = attributes.getString(KEY);
                    String value = attributes.getString(VALUE);

                    int chance = attributes.getInt(CHANCE);
                    int random_min = attributes.getInt(RANDOM_MIN);
                    int random_max = attributes.getInt(RANDOM_MIN);

                    writer.write(KEY+EQ+key+CLOSE);
                    writer.write(VALUE+EQ+value+CLOSE);

                    if( chance != -1 )
                        writer.write(CHANCE+EQ+chance+CLOSE);
                    if( random_min != -1 )
                        writer.write(RANDOM_MIN+EQ+random_min+CLOSE);
                    if( random_max != -1 )
                        writer.write(RANDOM_MAX+EQ+random_max+CLOSE);
                    writer.write(ENDSLASH);

                }

                if(hasAttributes)
                    writer.write(ENDITEM);
                else
                    writer.write(ENDSLASH);
            }

            writer.write("</items>");        
        }
        finally{
            writer.close();
        }

        System.out.println("COMPLETE!");
    }
}
