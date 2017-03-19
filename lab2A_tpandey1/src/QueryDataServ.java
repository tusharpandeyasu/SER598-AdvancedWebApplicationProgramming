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
public class QueryDataServ extends javax.servlet.http.HttpServlet {

	private String firstname = "", lastname = "", contact = "";
	private String[] languages;
	private String[] days;
	private int numRecords = 0;
	private boolean failure = false;
	private String xfirstname, xlastname, xcontact, xlang, xlangconcat = "", xdow, xdowconcat = "";
	// private String xlangconcat[];
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/html");
		//Make certain that the results of GETs are not cached by the browser
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		PrintWriter out = res.getWriter();

		String userAgent = req.getHeader("user-agent");
		String bg_color = "white";
		// Getting attribute values from URL
		int queryParamLength = -1;
		if(req.getQueryString() == null || req.getQueryString().length() == 0) {
			queryParamLength = 0;
		};
		Enumeration paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = req.getParameterValues(paramName);

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
					// languages = new String[1];
					// languages[0] = paramValue;
					languages = paramValue.split("\\+");
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

		// loop through all elements in XML
		try {
			ServletContext context = getServletContext();
			String path = context.getRealPath("/WEB-INF/");
			String filename = path + "Lab2.xml";
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			boolean[] hitResult = new boolean[5];

			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("person");

			System.out.println("----------------------------");

			Node n = null;
			Element eElement = null;
			if (userAgent.contains("Chrome")) {
				bg_color = "pink";
			}
			out.println("<html><body bgcolor=" + bg_color + "><h1>Results</h1>");
			NodeList tempList;
			String xmlString = null;

			for (int i = 0; i < nList.getLength(); i++) {
				System.out.println(nList.getLength());
				n = nList.item(i);
				System.out.println("\nCurrent Element of record: " + i + " - element: " + n.getNodeName());

				if (n.getNodeType() == Node.ELEMENT_NODE) {
					eElement = (Element) n.getChildNodes();
					tempList = eElement.getElementsByTagName("firstname");
					System.out.println("\nChecking firstname :" + i + " - element: " + n.getNodeName());
					if (tempList.getLength() > 0) {
						xfirstname = ((Element) tempList.item(0)).getAttribute("value");
						if (!firstname.isEmpty() && xfirstname.contains(firstname)) {
							hitResult[0] = true;
						}
					}

					tempList = eElement.getElementsByTagName("lastname");
					System.out.println("\nChecking lastname :" + i + " - element: " + n.getNodeName());
					if (tempList.getLength() > 0) {
						xlastname = ((Element) tempList.item(0)).getAttribute("value");
						if (!lastname.isEmpty() && xlastname.contains(lastname)) {
							hitResult[1] = true;
						}
					}

					// Logic for checking multivalue languages received from URL
					// with the xlang stored in XML
					tempList = eElement.getElementsByTagName("lang");
					System.out.println("\nChecking languages :" + i + " - element: " + n.getNodeName());
					if (tempList.getLength() > 0) {
						for (int j = 0; j < tempList.getLength(); j++) {
							xlang = ((Element) tempList.item(j)).getAttribute("value");
							xlangconcat = xlangconcat + ' ' + xlang;
						}
						for (int k = 0; queryParamLength != 0 && k < languages.length; k++) {
							if (xlangconcat.contains(languages[k])) {
								hitResult[2] = true;
								break;
							}
						}
					}

					// Logic for checking multivalue days of week received from
					// URL with the xdow stored in XML
					tempList = eElement.getElementsByTagName("dow");
					System.out.println("\nChecking days :" + i + " - element: " + n.getNodeName());
					if (tempList.getLength() > 0) {
						for (int j = 0; j < tempList.getLength(); j++) {
							xdow = ((Element) tempList.item(j))	.getAttribute("value");
							// xlangconcat = xlangconcat.trim() +' '+ xlang;
							xdowconcat = xdowconcat + ' ' + xdow;
						}
						for (int k = 0; queryParamLength != 0 && k < days.length; k++) {
							if (xdowconcat.contains(days[k])) {
								hitResult[3] = true;
								break;
							}
						}
					}

					tempList = eElement.getElementsByTagName("contact");
					System.out.println("\nChecking contact :" + n.getNodeName());
					if (tempList.getLength() > 0) {
						xcontact = ((Element) tempList.item(0)).getAttribute("value");
						if (!contact.isEmpty() && xcontact.contains(contact)) {
							hitResult[4] = true;
						}
					}

				}
				
				if(queryParamLength == 0) {
					//Empty query parameters - print all records from xml
					hitResult[0] = hitResult[1] = hitResult[2] = hitResult[3] = hitResult[4] = true;
				}
				
				if (hitResult[0] && hitResult[1] && hitResult[4] && hitResult[2] && hitResult[3]) {
					int k = i+1;
					xmlString = "Record No: "+k +" - "+ xfirstname + ", " + xlastname + ", " + xlangconcat + ", " + xdowconcat + ", "
							+ xcontact;
					out.println("<p>" + xmlString + "</p>");
				}
				hitResult[0] = hitResult[1] = hitResult[4] = hitResult[2] = hitResult[3] = false;
				xlangconcat = "";
				xdowconcat = "";
				n.getNextSibling();
			}
			out.println("</br><p>Done!</p></body></html>");

		} catch (

		Exception e)

		{
			e.printStackTrace();
			failure = true;
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Do nothing
	}
}