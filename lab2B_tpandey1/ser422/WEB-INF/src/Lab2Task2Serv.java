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
import java.util.Arrays;

//import com.sun.org.apache.xpath.internal.operations.String;

//import com.sun.org.apache.bcel.internal.generic.FNEG;

import javax.xml.parsers.*;
import javax.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class Lab2Task2Serv extends javax.servlet.http.HttpServlet {

	private String firstname, lastname, contact;
	private String mutex = "";
	private String[] languages;// = new String[0];
	private String[] days;// = new String[0];
	private int numRecords = 0;
	private boolean failure = false;
	private String xfirstname, xlastname, xcontact, xlang, xlangconcat = "", xdow, xdowconcat = "";
	private String htmlHeaderString = "<!DOCTYPE html><html><head><title>Web Form</title></head><body>";
	private String htmlEndString = "</body></html>";
	// private String xlangconcat[];
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		String currentFormUrl = req.getRequestURL().toString();
		System.out.println("Get Req URL " + currentFormUrl);

		HttpSession session = req.getSession(false);
		if (session != null) {
			firstname = (String) session.getAttribute("firstname");
			lastname = (String) session.getAttribute("lastname");
			String tempLanguages = (String) session.getAttribute("languages");
			System.out.println("languages from session: " + tempLanguages);
			languages = (tempLanguages != null ? tempLanguages.split(",") : new String[0]);
			String tempDays = (String) session.getAttribute("days");
			System.out.println("days from session: " + tempDays);
			days = (tempDays != null ? tempDays.split(",") : new String[0]);
			contact = (String) session.getAttribute("contact");
			if (contact == null)
				contact = "";
		} else {
			firstname = lastname = contact = "";
		}

		if (languages == null) {
			System.out.println("nulling languages: ");
			languages = new String[1];
			languages[0] = "";
		}
		if (days == null) {
			System.out.println("nulling days: ");
			days = new String[1];
			days[0] = "";
		}

		if (currentFormUrl.contains("webform1")) {
			out.println(htmlHeaderString
					+ getHtmlString("webform1", Arrays.toString(languages), Arrays.toString(days), "GET")
					+ htmlEndString);
		} else if (currentFormUrl.contains("webform2")) {
			out.println(htmlHeaderString
					+ getHtmlString("webform2", Arrays.toString(languages), Arrays.toString(days), "GET")
					+ htmlEndString);
		} else if (currentFormUrl.contains("webform3")) {
			out.println(htmlHeaderString
					+ getHtmlString("webform3", Arrays.toString(languages), Arrays.toString(days), "GET")
					+ htmlEndString);
		} else if (currentFormUrl.contains("webform4")) {
			out.println(htmlHeaderString
					+ getHtmlString("webform4", Arrays.toString(languages), Arrays.toString(days), "GET")
					+ htmlEndString);
		} else {
			out.println(htmlHeaderString + "<p>Good bye</p>" + htmlEndString);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String currentFormUrl = req.getRequestURL().toString();
		System.out.println("Post Req URL " + currentFormUrl);
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String action = req.getParameter("action");
		HttpSession session = req.getSession(true);

		if ("Cancel".equals(action)) {
			firstname = "";
			lastname = "";
			languages = new String[0];
			days = new String[0];
			contact = "";
			session.invalidate();
			out.println(getHtmlString("webform1", "", "", "GET"));
		} else {

			// Create a session object if it is already not created.

			String htmlFormName = req.getParameter("type");

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
						if (!firstname.isEmpty())
							session.setAttribute("firstname", firstname);
						break;
					case "lastname":
						lastname = paramValue;
						System.out.println("lname " + lastname);
						if (!lastname.isEmpty())
							session.setAttribute("lastname", lastname);
						break;
					case "contact":
						contact = paramValue;
						System.out.println("phone: " + contact);
						if (!contact.isEmpty())
							session.setAttribute("contact", contact);
						break;
					case "languages":
						languages = new String[1];
						languages[0] = paramValue;
						System.out.println("Language: " + languages[0]);
						if (languages != null && languages.length != 0)
							session.setAttribute("languages", languages.toString());
						break;
					case "days":
						days = new String[1];
						days[0] = paramValue;
						System.out.println("Day: " + days[0]);
						if (days != null && days.length != 0)
							session.setAttribute("Day", days.toString());
						break;
					default:
						System.out.println("No single value parameters found");
					}
				} else {
					//
					// // Read multiple valued data
					String tempParamName = null;
					switch (paramName) {
					case "languages":
						languages = paramValues.clone();
						System.out.println("languages from form: " + paramValues.length);
						System.out.println("langs: " + languages.length);
						if (languages != null && languages.length != 0)
							session.setAttribute("languages", Arrays.toString(languages));
						break;
					case "days":
						days = paramValues.clone();
						System.out.println("days: " + days.length);
						if (days != null && days.length != 0)
							session.setAttribute("days", Arrays.toString(days));
						break;
					default:
						System.out.println("No multi value parameters found");
					}
				}
			}

			// if (session.isNew()){
			// session.setAttribute("firstname",firstname);
			// session.setAttribute("lastname",lastname);
			// }
			System.out.println("fn: " + session.getAttribute("firstname"));
			System.out.println("ln: " + session.getAttribute("lastname"));
			System.out.println("langs: " + session.getAttribute("languages"));
			System.out.println("days: " + session.getAttribute("days"));
			System.out.println("contact: " + session.getAttribute("contact"));

			if (htmlFormName.equals("webform1"))
				out.println(getHtmlString("webform1", Arrays.toString(languages), Arrays.toString(days), "POST"));
			else if (htmlFormName.equals("webform2"))
				out.println(getHtmlString("webform2", Arrays.toString(languages), Arrays.toString(days), "POST"));
			else if (htmlFormName.equals("webform3"))
				out.println(getHtmlString("webform3", Arrays.toString(languages), Arrays.toString(days), "POST"));
			else if (htmlFormName.equals("webform4"))
				out.println(getHtmlString("webform4", Arrays.toString(languages), Arrays.toString(days), "POST"));
			else if (htmlFormName.equals("webform5")) {

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

						// parse the input stream
						Document document = builder.parse(in);
						document.getDocumentElement().normalize();

						// JAXP
						Element rootXMLNode = document.getDocumentElement();

						NodeList nodeList = document.getElementsByTagName("Persons");
						if (nodeList.getLength() == 0)
							throw new ServletException();
						nodeList.item(0)
								.appendChild(getPerson(document, firstname, lastname, languages, days, contact));

						// Write the xml source back to file
						// handle a) concurrency between Tomcat instances, b) a
						// shared resource (the XML file) between Tomcat
						// instances, and c) ensure you
						// enable “stickiness” so a conversation happens with
						// the proper Tomcat instance.
						synchronized (mutex) {
							Transformer transformer = TransformerFactory.newInstance().newTransformer();
							transformer.setOutputProperty(OutputKeys.INDENT, "yes");
							DOMSource source = new DOMSource(document);
							StreamResult console = new StreamResult(System.out);
							transformer.transform(source, console);
							StreamResult result = new StreamResult(new File(filename));
							transformer.transform(source, result);
							numRecords = (document.getElementsByTagName("person")).getLength();

							in.close();
						}

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
						Element mainRootElement = doc.createElementNS("http://www.w3.org/20	01/XMLSchema-instance",
								"Persons");
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
						// result.getOutputStream().close();
						numRecords = (doc.getElementsByTagName("person")).getLength();

					} catch (Exception e) {
						System.out.println("Exception " + e);
					}
				}

				if (failure) {
					out.println("<html><body><h1>Failure!!</h2></br></body></html>");
				} else {
					firstname = "";
					lastname = "";
					languages = new String[0];
					days = new String[0];
					contact = "";
					session.invalidate();
					out.println(getHtmlString("webform1", "", "", "GET"));
				}

			}

			else
				out.println("Bad request");

		}

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

	private String getChecked(String mainStr, String testStr) {
		if (mainStr.contains(testStr)) {
			return "checked";
		} else {
			return "";
		}
	}

	private String getHtmlString(String formName, String xlanguages, String xdays, String reqType) {
		String returnStr = "";
		String webForm1 = "<form action=\"webform1\" method=\"POST\"> First name: "
				+ "<input type=\"text\" name=\"firstname\" value=" + firstname + "><br><br>"
				+ "Last name: <input type=\"text\" name=\"lastname\" value=" + lastname + "><br><br><br><br>"
				+ "<input type=\"hidden\" name=\"type\" value=\"webform1\" />"
				+ "<input type=\"submit\" value=\"Next\"></form>";
		String webForm2A = "<form action=\"webform2\" method=\"POST\">" + "Programming Languages Known:<br><br>"
				+ "<input type=\"checkbox\" name=\"languages\" value=\"C\"";// +
		// getChecked(languages.toString(),
		// "C")
		// + " > C<br>" + "<input type=\"checkbox\" name=\"languages\"
		// value=\"Ruby\""
		// + getChecked(languages.toString(), "Ruby") + "> Ruby<br>"
		// + "<input type=\"checkbox\" name=\"languages\" value=\"Ada\"" +
		// getChecked(languages.toString(), "Ada")
		// + " > Ada<br>" + "<input type=\"checkbox\" name=\"languages\"
		// value=\"Java\""
		// + getChecked(languages.toString(), "Java") + " > Java<br>"
		// + "<input type=\"checkbox\" name=\"languages\" value=\"Scala\""
		// + getChecked(languages.toString(), "Scala") + " > Scala<br>"
		// + "<input type=\"checkbox\" name=\"languages\" value=\"Python\""
		// + getChecked(languages.toString(), "Python") + " > Python<br>"
		// + "<input type=\"hidden\" name=\"type\" value=\"webform2\" />"
		// + "<a href=\"/ser422/lab2task2/webform1\">Prev</a>" +
		// "&nbsp&nbsp&nbsp" + "<input type=\"submit\" value=\"Next\"></form>";
		String webForm2B = " > C<br>" + "<input type=\"checkbox\" name=\"languages\" value=\"Ruby\"";
		String webForm2C = "> Ruby<br>" + "<input type=\"checkbox\" name=\"languages\" value=\"Ada\"";
		String webForm2D = " > Ada<br>" + "<input type=\"checkbox\" name=\"languages\" value=\"Java\"";
		String webForm2E = " > Java<br>" + "<input type=\"checkbox\" name=\"languages\" value=\"Scala\"";
		String webForm2F = "> Scala<br><input type=\"checkbox\" name=\"languages\" value=\"Python\"";
		String webForm2G = " > Python<br><input type=\"hidden\" name=\"type\" value=\"webform2\" />" + "<br><br><br>"
				+ "<a href=\"/ser422/lab2task2/webform1\">Prev</a>" + "&nbsp&nbsp&nbsp"
				+ "<input type=\"submit\" value=\"Next\"></form>";

		String webForm3 = "<form name=\"webform3\" action=\"webform3\" method=\"POST\">"
				+ "Available on Day(s) of the week:<br><br>" + "<input type=\"checkbox\" name=\"days\" value=\"Monday\""
				+ getChecked(Arrays.toString(days), "Monday") + ">Monday<br>"
				+ "<input type=\"checkbox\" name=\"days\" value=\"Tuesday\""
				+ getChecked(Arrays.toString(days), "Tuesday") + ">Tuesday<br>"
				+ "<input type=\"checkbox\" name=\"days\" value=\"Wednesday\""
				+ getChecked(Arrays.toString(days), "Wednesday") + ">Wednesday<br>"
				+ "<input type=\"checkbox\" name=\"days\" value=\"Thursday\""
				+ getChecked(Arrays.toString(days), "Thursday") + ">Thursday<br>"
				+ "<input type=\"checkbox\" name=\"days\" value=\"Friday\""
				+ getChecked(Arrays.toString(days), "Friday") + ">Friday<br>"
				+ "<input type=\"checkbox\" name=\"days\" value=\"Saturday\""
				+ getChecked(Arrays.toString(days), "Saturday") + ">Saturday<br>"
				+ "<input type=\"checkbox\" name=\"days\" value=\"Sunday\""
				+ getChecked(Arrays.toString(days), "Sunday") + ">Sunday<br><br><br>"
				+ "<input type=\"hidden\" name=\"type\" value=\"webform3\" />"
				+ "<a href=\"/ser422/lab2task2/webform2\">Prev</a>" + "&nbsp&nbsp"
				+ "<input type=\"submit\" value=\"Next\"></form>";
		String webForm4 = "<form action=\"webform4\" method=\"POST\"> Contact Number: "
				+ "<input type=\"text\" name=\"contact\" value=" + contact + "><br><br>"
				+ "<input type=\"hidden\" name=\"type\" value=\"webform4\" />"
				+ "<a href=\"/ser422/lab2task2/webform3\">Prev</a>" + "&nbsp&nbsp"
				+ "<input type=\"submit\" value=\"Next\"></form>";
		String webForm5 = "<form action=\"webform5\" method=\"POST\"> <table> <tr><td>First Name</td><td>" + firstname
				+ "</td>\n<td>" + "<input type=\"hidden\" name=\"type\" value=\"webform5\" />"
				+ "	<tr><td>Last Name</td><td>" + lastname + "</td>\n<td>" + "	<tr><td>Programming languages</td><td>"
				+ Arrays.toString(languages).replace("[", "").replace("]", "") + "</td>\n<td>"
				+ "	<tr><td>Days Availability</td><td>" + Arrays.toString(days).replace("[", "").replace("]", "")
				+ "</td>\n<td>" + "	<tr><td>Contact</td><td>" + contact + "</td>\n<td>"
				+ "</table><a href=\"/ser422/lab2task2/webform4\">Prev</a>" + "&nbsp&nbsp&nbsp"
				+ "<input type=\"submit\" value=\"Submit\">" + "&nbsp&nbsp"
				+ "<input type=\"submit\" value=\"Cancel\"> </form>";

		switch (formName) {
		case "webform1":
			if (reqType.equals("GET")) {
				returnStr = webForm1;
			} else {
				System.out.println("getting languages checked: " + getChecked(Arrays.toString(languages), "C"));
				returnStr = webForm2A + getChecked(Arrays.toString(languages), "C") + webForm2B
						+ getChecked(Arrays.toString(languages), "Ruby") + webForm2C
						+ getChecked(Arrays.toString(languages), "Ada") + webForm2D
						+ getChecked(Arrays.toString(languages), "Java") + webForm2E
						+ getChecked(Arrays.toString(languages), "Scala") + webForm2F
						+ getChecked(Arrays.toString(languages), "Python") + webForm2G;
				;
			}
			break;
		case "webform2":
			if (reqType.equals("GET")) {
				System.out.println("getting languages checked: " + getChecked(Arrays.toString(languages), "C"));
				returnStr = webForm2A + getChecked(Arrays.toString(languages), "C") + webForm2B
						+ getChecked(Arrays.toString(languages), "Ruby") + webForm2C
						+ getChecked(Arrays.toString(languages), "Ada") + webForm2D
						+ getChecked(Arrays.toString(languages), "Java") + webForm2E
						+ getChecked(Arrays.toString(languages), "Scala") + webForm2F
						+ getChecked(Arrays.toString(languages), "Python") + webForm2G;
			} else {
				returnStr = webForm3;
			}
			break;
		case "webform3":
			if (reqType.equals("GET")) {
				returnStr = webForm3;
			} else {
				returnStr = webForm4;
			}
			break;
		case "webform4":
			if (reqType.equals("GET")) {
				returnStr = webForm4;
			} else {
				returnStr = webForm5;
			}
			break;

		default:
			System.out.println("Not found html string");
		}
		if (returnStr.contains("checked")) {
			System.out.println("GOt checked");
		}
		return returnStr;
	}
}