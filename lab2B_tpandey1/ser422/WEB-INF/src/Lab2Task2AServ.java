package mypkg;

import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.io.File;
import java.util.Collections;

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
public class Lab2Task2AServ extends javax.servlet.http.HttpServlet {

	private String firstname = "", lastname = "", contact = "", uniq_fname = "", uniq_lname = "";
	private String[] uniq_lang;
	private String[] uniq_dow;
	private String[] oth_lang;
	private String[] oth_dow;
	private String[] languages;
	private String[] days;
	private int numRecords = 0;
	private boolean failure = false;
	private String xfirstname, xlastname, xcontact, xlang, xlangconcat = "", xdow, xdowconcat = "";
	private HashMap<Integer, Integer> resultsMap;
	// private String xlangconcat[];
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("In beg");
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title></title></head><body>");
		String s1 = null;

		Cookie cookie = null;
		Cookie[] cookies = null;
		cookies = request.getCookies();
//		System.out.println("Cookies length: "+ cookies.length);
		int count = 0;

		String id = null;

		try {

			if (cookies == null) {

				Cookie namecookie = new Cookie("cookie1", "abc");
				namecookie.setMaxAge(60 * 60 * 24);
				response.addCookie(namecookie);
				request.getRequestDispatcher("/lab2task2/mywebform.html").forward(request, response);

			} else {
				System.out.println("In else");
				String firstname = request.getParameter("firstname");
				String lastname = request.getParameter("lastname");

				if (firstname == null && lastname == null) {

					for (int i = 0; i < cookies.length; i++) {
						cookie = cookies[i];

						if ((cookie.getName()).compareTo("first_name") == 0) {
							firstname = cookie.getValue();
						}
						if ((cookie.getName()).compareTo("last_name") == 0) {
							lastname = cookie.getValue();
						}
					}
					out.print("Welcome Again!, " + firstname + " " + lastname + " <br/><br/><br/>");

					try {
						ServletContext context = getServletContext();
						String path = context.getRealPath("/WEB-INF/");
						String filename = path + "Lab2.xml";
						File fXmlFile = new File(filename);
						DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(fXmlFile);

						// For First name and Last name
						boolean[] hitResult = new boolean[2];

						doc.getDocumentElement().normalize();

						System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

						NodeList nList = doc.getElementsByTagName("person");

						System.out.println("----------------------------");

						Node n = null;
						Element eElement = null;

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
									System.out.println("xml: "+xfirstname+" req: "+firstname);
									if (xfirstname.contains(firstname)) {
										System.out.println("Found f");
										hitResult[0] = true;
										uniq_fname = xfirstname;
									}
								}

								tempList = eElement.getElementsByTagName("lastname");
								System.out.println("\nChecking lastname :" + i + " - element: " + n.getNodeName());
								if (tempList.getLength() > 0) {
									xlastname = ((Element) tempList.item(0)).getAttribute("value");
									System.out.println("xml: "+xlastname+" req: "+lastname);
									if (xlastname.contains(lastname)) {
										System.out.println("Found l");
										uniq_lname = xlastname;
										hitResult[1] = true;
									}
								}

								if (hitResult[0] && hitResult[1]) {

									// Logic for checking multivalue languages
									// received from URL
									// with the xlang stored in XML
									tempList = eElement.getElementsByTagName("lang");
									System.out.println("\nChecking languages :" + i + " - element: " + n.getNodeName());
									if (tempList.getLength() > 0) {
										uniq_lang = new String[tempList.getLength()];
										for (int j = 0; j < tempList.getLength(); j++) {
											uniq_lang[j] = ((Element) tempList.item(j)).getAttribute("value");
											// xlangconcat = xlangconcat + ' ' +
											// xlang;
										}

									}

									// Logic for checking multivalue days of
									// week received from
									// URL with the xdow stored in XML
									tempList = eElement.getElementsByTagName("dow");
									System.out.println("\nChecking days :" + i + " - element: " + n.getNodeName());
									if (tempList.getLength() > 0) {
										uniq_dow = new String[tempList.getLength()];
										for (int j = 0; j < tempList.getLength(); j++) {
											uniq_dow[j] = ((Element) tempList.item(j)).getAttribute("value");
											// xlangconcat = xlangconcat.trim()
											// +' '+ xlang;

										}

									}
									System.out.print("Found unique record");

									break;
								}
							}
						}

						int max1 = 0, max2 = 0, max3 = 0;
						int index1 = 0, index2 = 0, index3 = 0;
						int count1 = 0, count2 = 0, total = 0;
//						resultsMap = new HashMap<Integer, Integer>();

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

								// Eliminate uinique guy and continue for others
								if ((xfirstname == uniq_fname) && (xlastname == uniq_lname))
									continue;

								// Logic for checking multivalue languages
								// received from URL
								// with the xlang stored in XML
								tempList = eElement.getElementsByTagName("lang");
								System.out.println("\nChecking languages :" + i + " - element: " + n.getNodeName());
								if (tempList.getLength() > 0) {
									oth_lang = new String[tempList.getLength()];
									for (int j = 0; j < tempList.getLength(); j++) {
										oth_lang[j] = ((Element) tempList.item(j)).getAttribute("value");
									}
									count1 = findMatchCount(oth_lang, uniq_lang);

								}

								// Logic for checking multivalue days of week
								// received from
								// URL with the xdow stored in XML
								tempList = eElement.getElementsByTagName("dow");
								System.out.println("\nChecking days :" + i + " - element: " + n.getNodeName());
								if (tempList.getLength() > 0) {
									oth_dow = new String[tempList.getLength()];
									for (int j = 0; j < tempList.getLength(); j++) {
										oth_dow[j] = ((Element) tempList.item(j)).getAttribute("value");
										// xlangconcat = xlangconcat.trim() +'
										// '+ xlang;

									}
									count2 = findMatchCount(oth_dow, uniq_dow);

								}


							}
							total = count1 + count2;
							System.out.println("Count for index: "+i+" is: "+total);
							if (total > max1) {
								max3 = max2;
								index3 = index2;
								max2 = max1;
								index2 = index1;
								max1 = total;
								index1 = i;
							} else if (total > max2) {
								max3 = max2;
								index3 = index2;
								max2 = total;
								index2 = i;
							} else if (total > max3) {
								max3 = total;
								index3 = i;
							}

						}
						System.out.println("resultsCount");
						System.out.println("max1, index1-" + max1 +"-"+ index1);
						System.out.println("max1, index1-" + max2 +"-"+ index2);
						System.out.println("max1, index1-" + max3 +"-"+ index3);

						int currentIndex = 0;
						String recordString = "";
						for (int i= 0; i<3; i++) {
							if (i == 0)
								currentIndex = index1;
							else if (i == 1)
								currentIndex = index2;
							else 
								currentIndex = index3;
							
							n = nList.item(currentIndex);
							eElement = (Element) n.getChildNodes();
							tempList = eElement.getElementsByTagName("firstname");
							if (tempList.getLength() > 0) {
								recordString = recordString + ((Element) tempList.item(0)).getAttribute("value"); 
							}
							
							tempList = eElement.getElementsByTagName("lastname");
							if (tempList.getLength() > 0) {
								recordString = recordString + " " +((Element) tempList.item(0)).getAttribute("value");
							}
							
							tempList = eElement.getElementsByTagName("lang");
							if (tempList.getLength() > 0) {
								for (int j = 0; j < tempList.getLength(); j++) {
									xlang = ((Element) tempList.item(j)).getAttribute("value");
									xlangconcat = xlangconcat + ' ' + xlang;
								}
								recordString = recordString + ",   " + xlangconcat;
							}
							
							tempList = eElement.getElementsByTagName("dow");
							if (tempList.getLength() > 0) {
								for (int j = 0; j < tempList.getLength(); j++) {
									xdow = ((Element) tempList.item(j))	.getAttribute("value");
									xdowconcat = xdowconcat + ' ' + xdow;
								}
								recordString = recordString + ",   " + xlangconcat;
							}
							
							tempList = eElement.getElementsByTagName("contact");
							if (tempList.getLength() > 0) {
								recordString = recordString + ",  " + ((Element) tempList.item(0)).getAttribute("value") + "<br><br><br>";
								
							}
							
						}
						
						out.println(recordString);
						
						
						out.println("</br><p>Done!</p></body></html>");
					} catch (Exception e)

					{
						e.printStackTrace();
						failure = true;
					}
				} else {
					Cookie fname = new Cookie("first_name", request.getParameter("firstname"));
					Cookie lname = new Cookie("last_name", request.getParameter("lastname"));
					fname.setMaxAge(60 * 60 * 24);
					lname.setMaxAge(60 * 60 * 24);
					response.addCookie(fname);
					response.addCookie(lname);
					out.println("Hello there," + firstname + " " + lastname);
					out.println("Welcome for the first visit");

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		  res.getWriter().println("<html><body><p>Use "
					+ "GET instead!</p></body></html>");
	}
	
    public int findMatchCount(String [] a,String [] b){
        /*
            If a has more than one entries that matches to a string in b, it's also considered as accountable.
        */
            int matchCount = 0;

            for(String x_element : a){
                for(String y_element : b){
                    if(x_element.equals(y_element)) ++matchCount;                       
                }
            }
            return matchCount;
    }

	// private static HashMap sortByValues(HashMap map) {
	// List list = new LinkedList(map.entrySet());
	// // Defined Custom Comparator here
	// Collections.sort(list, new Comparator() {
	// public int compare(Object o1, Object o2) {
	// return ((Comparable) ((Map.Entry)
	// (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
	// }
	// });
	//
	// // Here I am copying the sorted list in HashMap
	// // using LinkedHashMap to preserve the insertion order
	// HashMap sortedHashMap = new LinkedHashMap();
	// for (Iterator it = list.iterator(); it.hasNext();) {
	// Map.Entry entry = (Map.Entry) it.next();
	// sortedHashMap.put(entry.getKey(), entry.getValue());
	// }
	// return sortedHashMap;
	// }
}