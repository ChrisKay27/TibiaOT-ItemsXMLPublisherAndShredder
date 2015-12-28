
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;


public class LoadIntoDB {

    private static String sqlUser = "databaseUsername";
    private static String sqlPass = "databasePassword";
    private static String database = "databasename";
    private static String pathToItemsXml = "C:\path to\items.xml";

   public static void main(String[] args) throws Exception {
        //if( true ) return;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
            // handle the error
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

        stmt = conn.createStatement();
        System.out.println("TRUNCATE items;");
        stmt.execute("TRUNCATE items;");
        stmt.execute("TRUNCATE item_attributes;");

        String ID = "id", NAME = "name", FROMID = "fromid", TOID = "toid", ARTICLE = "article", KEY = "key", VALUE = "value", CHANCE = "chance", RANDOM_MIN="random_min", RANDOM_MAX="random_max";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(pathToItemsXml));


        NodeList items = doc.getElementsByTagName("item");

        for (int i = 0; i < items.getLength() ; i++) {
            if( i == items.getLength()/2 )
                System.out.println("Half way done!");

            Node item = items.item(i);

            NamedNodeMap nnm = item.getAttributes();
            Node tmpN = nnm.getNamedItem(ID);
            String tmp;

            if( tmpN == null )
                tmpN = nnm.getNamedItem(FROMID);

            int id = Integer.parseInt(tmpN.getNodeValue());
            int fromid = -1;
            int toid = -1;
            String article=null;
            String name;


            tmpN = nnm.getNamedItem(FROMID);
            if( tmpN != null )
                fromid = Integer.parseInt(tmpN.getNodeValue());

            tmpN = nnm.getNamedItem(TOID);
            if( tmpN != null )
                toid = Integer.parseInt(tmpN.getNodeValue());

            tmpN = nnm.getNamedItem(ARTICLE);
            if( tmpN != null )
                article = tmpN.getNodeValue();

            tmpN = nnm.getNamedItem(NAME);
            if( tmpN != null )
                name = tmpN.getNodeValue();
            else
                name = "";

            if( name != null ) name = name.replaceAll("'","''");


            try {

                stmt = conn.createStatement();
                String query = "INSERT INTO items values ("+id+",\'"+name+"\',"+fromid+","+toid+","+(article==null?null:"\'"+article+"\'")+");";
                //System.out.println(query);
                stmt.execute(query);


                NodeList attrs = item.getChildNodes();

                for (int j = 0; j < attrs.getLength(); j++) {
                    Node attr = attrs.item(j);
                    if( !attr.hasAttributes()) continue;

                    nnm = attr.getAttributes();

                    String key = "",value ="";
                    int chance=-1, random_min=-1,random_max= -1;


                    tmpN = nnm.getNamedItem(KEY);
                    if( tmpN != null )
                        key = tmpN.getNodeValue();
                    tmpN = nnm.getNamedItem(VALUE);
                    if( tmpN != null )
                        value = tmpN.getNodeValue();
                    tmpN = nnm.getNamedItem(CHANCE);
                    if( tmpN != null )
                        chance = Integer.parseInt(tmpN.getNodeValue());
                    tmpN = nnm.getNamedItem(RANDOM_MIN);
                    if( tmpN != null )
                        random_min = Integer.parseInt(tmpN.getNodeValue());
                    tmpN = nnm.getNamedItem(RANDOM_MAX);
                    if( tmpN != null )
                        random_max = Integer.parseInt(tmpN.getNodeValue());

                    if( key != null ) key = key.replaceAll("'","''");
                    if( value != null ) value = value.replaceAll("'","''");


                    query = "INSERT INTO item_attributes  values ("+
                            id+",\'"+key+"\',\'"+value+"\',"+chance+","+random_min+","+random_max+");";
                   // System.out.println(query);
                    stmt.execute(query);

                }


            }
            catch (SQLException ex){
                // handle any errors
                System.out.println("Error occurred at element " + id);
                System.out.println("Attribute SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                System.exit(1);
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) { } // ignore
                    rs = null;
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) { } // ignore
                    stmt = null;
                }
            }
        }
        System.out.println("COMPLETE!");
    }



}
