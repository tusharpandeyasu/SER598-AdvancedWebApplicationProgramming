package mypkg;

import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;
import java.io.*;
import java.net.*;
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

//import com.sun.org.apache.bcel.internal.generic.FNEG;

import javax.xml.parsers.*;
import javax.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class InputDataServ extends javax.servlet.http.HttpServlet {

	private String firstname, lastname, contact;
	private String[] languages;
	private String[] days;
	private int numRecords = 0;
	private boolean failure = false;
	private String xfirstname, xlastname, xcontact, xlang, xlangconcat = "", xdow, xdowconcat = "";
	// private String xlangconcat[];
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Do nothing
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		Enumeration paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = req.getParameterValues(paramName);

			// Read single valued data
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				switch (paramName) {
				case "firstname":
					firstname = paramValue;
					System.out.println("fname: " + firstname);
					break;
				case "lastname":
					lastname = paramValue;
					System.out.println("lname " + lastname);
					break;
				case "contact":
					contact = paramValue;
					System.out.println("phone: " + contact);
					break;
				case "languages":
					languages = new String[1];
					languages[0] = paramValue;
//					languages = paramValue.split("+");
					System.out.println("Language: " + languages[0]);
					break;
				case "days":
					days = new String[1];
					days[0] = paramValue;
					System.out.println("Day: " + days[0]);
					break;
				default:
					System.out.print("No single value parameters found");
				}
			} else {

				// Read multiple valued data
				String tempParamName = null;
				switch (paramName) {
				case "languages":
					languages = paramValues.clone();
					System.out.println("langs: " + languages.length);
					break;
				case "days":
					days = paramValues.clone();
					System.out.println("days: " + days.length);
					break;
				default:
					System.out.print("No multi value parameters found");
				}
			}
		}

		ServletContext context = getServletContext();
		String path = context.getRealPath("/WEB-INF/");
		String filename = path + "Lab2.xml";
		File f = new File(filename);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (Exception e) {
			System.out.println("Exception " + e);
			failure = true;
		}

		if (f.exists() && !f.isDirectory()) {

			try {
				FileInputStream in = new FileInputStream(filename);

				builderFactory.setNamespaceAware(true);
				builderFactory.setValidating(false);
				builderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

				// Parse the input stream
				Document document = builder.parse(in);
				document.getDocumentElement().normalize();

				// JAXP
				Element rootXMLNode = document.getDocumentElement();

				NodeList nodeList = document.getElementsByTagName("Persons");
				if (nodeList.getLength() == 0)
					throw new ServletException();
				nodeList.item(0).appendChild(getPerson(document, firstname, lastname, languages, days, contact));

				// Write the xml source back to file
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(document);
				StreamResult console = new StreamResult(System.out);
				transformer.transform(source, console);
				StreamResult result = new StreamResult(new File(filename));
				transformer.transform(source, result);
				numRecords = (document.getElementsByTagName("person")).getLength();
				in.close();

			} catch (SAXException e) {
				e.printStackTrace();
				failure = true;

			} catch (Exception e) {
				e.printStackTrace();
				failure = true;
			}

		} else {
			try {
				builder = builderFactory.newDocumentBuilder();
				Document doc = builder.newDocument();
				Element mainRootElement = doc.createElementNS("http://www.w3.org/20	01/XMLSchema-instance", "Persons");
				doc.appendChild(mainRootElement);

				mainRootElement.appendChild(getPerson(doc, firstname, lastname, languages, days, contact));
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				StreamResult console = new StreamResult(System.out);
				transformer.transform(source, console);

				System.out.println("\nXML Created Successfully name: " + filename);
				StreamResult result = new StreamResult(new File(filename));
				transformer.transform(source, result);
				numRecords = (doc.getElementsByTagName("person")).getLength();

			} catch (Exception e) {
				System.out.println("Exception " + e);
			}
		}

		if (failure) {
			out.println("<html><body><h1>Failure!!</h2></br></body></html>");
		} else {
			out.println("<html><body><h1>Success!</h1></br><p>The Total Number of Records in file: " + numRecords
					+ " </p></body></html>");
		}

		// Hyperlink back to the web form page
		out.println("<p><a href='webform.html'>Back to Web Form </a></p>");

	}

	private Node getPerson(Document doc, String firstname, String lastname, String[] languages, String[] days,
			String contact) {
		Element tempElem = null;
		Element personElem = doc.createElement("person");

		// Add firstname element
		tempElem = doc.createElement("firstname");
		tempElem.setAttribute("value", firstname);
		personElem.appendChild(tempElem);

		// Add lastname element
		tempElem = doc.createElement("lastname");
		tempElem.setAttribute("value", lastname);
		personElem.appendChild(tempElem);

		// Create languages element with multi values
		Element langElem = doc.createElement("languages");
		for (int i = 0; i < languages.length; i++) {
			tempElem = doc.createElement("lang");
			tempElem.setAttribute("value", languages[i]);
			langElem.appendChild(tempElem);
		}

		// Append languages element to personElement
		personElem.appendChild(langElem);

		// Create Days element with multi values
		Element daysElem = doc.createElement("days");
		for (int i = 0; i < days.length; i++) {
			tempElem = doc.createElement("dow");
			tempElem.setAttribute("value", days[i]);
			daysElem.appendChild(tempElem);
		}

		// Append days element to personElement
		personElem.appendChild(daysElem);

		tempElem = doc.createElement("contact");
		tempElem.setAttribute("value", contact);
		personElem.appendChild(tempElem);

		return personElem;
	}
}