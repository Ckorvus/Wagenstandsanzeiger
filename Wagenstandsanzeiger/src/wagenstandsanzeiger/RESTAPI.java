/**
 * 
 */
package wagenstandsanzeiger;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author TayHo
 *
 */
public class RESTAPI {
	// Creates an array in which we will store the names of files and directories
    String[] pathnames;
    
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/station/", (exchange -> {
        	String[] segments = exchange.getRequestURI().toString().split("/");
        	if ("GET".equals(exchange.getRequestMethod())) {
        		if (segments[1].equals("station") && segments[3].equals("train") && segments[5].equals("waggon")) {
        			RESTAPI restAPI = new RESTAPI();
        			String sections = restAPI.getStations(new String[]{segments[2], segments[4], segments[6]});
        			String responseText = "{\"sections\":[" + sections + "]}";
                    exchange.sendResponseHeaders(200, responseText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
                } else {
                    exchange.sendResponseHeaders(400, -1);// 400 Bad Request
    	        }
            } else {
	            exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
	        }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	/**
	 * @param args
	 */
	public String getStations(String[] args) {
		String station = args[0];
		String trainNumber = args[1];
		String waggonNumber = args[2];
		String response = "";

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
        // optional, but recommended
        // process XML securely, avoid attacks like XML External Entities (XXE)
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = null;
        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File folder = new File(System.getProperty("user.dir") + "\\src\\wagenstandsanzeiger\\resource\\");
        // Populates the array with names of files and directories
        pathnames = folder.list();
        String FILENAME = "";
        // For each pathname
        for (String pathname : pathnames) {
            // Print the names of files and directories
        	if(pathname.startsWith(station + "_")) {
            	FILENAME = pathname;
            	doc = db.parse(new File(folder + "\\" + FILENAME));
            	break;
        	}
        }
        // optional, but recommended
        doc.getDocumentElement().normalize();
        // get every <track>
        NodeList tracks = ((Element) doc.getElementsByTagName("tracks").item(0)).getElementsByTagName("track");
        for (int i = 0; i < tracks.getLength(); i++) {
        	 // get every <train>
            NodeList trains = ((Element) ((Element) tracks.item(i)).getElementsByTagName("trains").item(0)).getElementsByTagName("train");
            for (int j = 0; j < trains.getLength(); j++) {
            	 // get every <trainNumber>
                NodeList trainNumbers = ((Element) ((Element) trains.item(j)).getElementsByTagName("trainNumbers").item(0)).getElementsByTagName("trainNumber");
            	 for (int k = 0; k < trainNumbers.getLength(); k++) {
                 	if (trainNumbers.item(k).getTextContent().equals(trainNumber)) {
                 		 // get every <waggon>
                        NodeList waggons = ((Element) ((Element) trains.item(j)).getElementsByTagName("waggons").item(0)).getElementsByTagName("waggon");
                         for (int l = 0; l < waggons.getLength(); l++) {
                         	if (((Element) waggons.item(l)).getElementsByTagName("number").item(0).getTextContent().equals(waggonNumber)) {
                         		 // get every <identifier>
                                NodeList sections = ((Element) ((Element) waggons.item(l)).getElementsByTagName("sections").item(0)).getElementsByTagName("identifier");
                         		for (int m = 0; m < sections.getLength(); m++) {
                         			String section = sections.item(m).getTextContent();
                         			response += (response.equals("")? "" : ",") + "\"" + section + "\"";
                                 }
                         		return response;
                             }
                         }
                     }
                 }
            }
        }
    } catch (ParserConfigurationException | SAXException | IOException error) {
    	error.printStackTrace();
    }
	return response;
	}
}
