import java.net.*;
import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import org.xml.sax.*;
import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SockServer5 {
	public static void main(String args[]) throws Exception {
		ServerSocket serv = null;
		InputStream in = null;
		OutputStream out = null;
		Socket sock = null;
		int cId = 0;
		String clientId = "";
		Map<String, Integer> totals = null;
		String fileName = "";

		try {
			if (args.length != 0) {
				fileName = args[0];
				totals = loadXmlToMap(new FileInputStream(args[0]));
				System.out.println("Parsed successfully!");

			} else {
				totals = new HashMap<String, Integer>();
			}

			serv = new ServerSocket(8888);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (serv.isBound() && !serv.isClosed()) {
			System.out.println("SockServer5 Ready...");

			try {
				sock = serv.accept();
				in = sock.getInputStream();
				out = sock.getOutputStream();

				DataInputStream it;
				// create read stream to read information
				it = new DataInputStream(in);

				DataOutputStream ot;
				// create write stream to send information
				ot = new DataOutputStream(out);

				char c = (char) in.read();
				switch (c) {
				case 'r':
					System.out.print("Server received " + c);
					// clientId = it.readInt();
					cId = it.readInt();
					clientId = cId + "";
					totals.put(clientId, 0);
					ot.writeInt(0);
					break;

				default:
					cId = it.readInt();
					clientId = cId + "";
					int x = it.readInt();
					System.out.print("Server received " + x + " from client " + clientId);
					Integer total = totals.get(clientId);
					if (total == null) {
						total = 0;
					}
					totals.put(clientId, total + x);
					ot.writeInt(totals.get(clientId));
					break;
				}
				System.out.println("\nGo");

				generateXMLFile(totals, fileName);

				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (sock != null)
					sock.close();
			}
		}
	}

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public static Map<String, Integer> loadXmlToMap(InputStream in) {

		Map<String, Integer> clientsMap = new HashMap<String, Integer>();

		if (in != null) {
			try {
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				builderFactory.setNamespaceAware(true);
				builderFactory.setValidating(false);
				builderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				// parse the input stream
				Document document = builder.parse(in);
				document.getDocumentElement().normalize();
				// JAXP
				Element rootXMLNode = document.getDocumentElement();

				NodeList nodeList = document.getElementsByTagName("client");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node currentNode = nodeList.item(i);
					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) currentNode;
						String tempId = eElement.getAttribute("id");
						int tempTotal = Integer.parseInt(eElement.getAttribute("total"));

						System.out.println("client Id: " + tempId);
						System.out.println("Total: " + tempTotal);
						clientsMap.put(tempId, tempTotal);
					}
				}
				in.close();

			} catch (FactoryConfigurationError e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return clientsMap;

	}

	public static void generateXMLFile(Map<String, Integer> clientMap, String filename) {
		if (filename.length() == 0) {
			filename = "ClientsRunningTotal.xml";
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element mainRootElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema-instance", "Clients");
			doc.appendChild(mainRootElement);

			// append child elements to root element
			Iterator<Map.Entry<String, Integer>> it = clientMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
				mainRootElement.appendChild(getClient(doc, pair.getKey(), pair.getValue()));
			}

			// output DOM XML to console
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult console = new StreamResult(System.out);
			transformer.transform(source, console);

			System.out.println("\nXML Created Successfully name: " + filename);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Node getClient(Document doc, String id, Integer total) {
		Element client = doc.createElement("client");
		client.setAttribute("id", id);
		client.setAttribute("total", total + "");
		return client;
	}
}