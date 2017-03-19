<%@page import="java.util.Iterator"%>
<%@page import="edu.asupoly.ser422.model.Author"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List"%>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Booktown</title>
</head>
<body><H2>Authors:</H2><br>
<%

int buildView = (int)request.getAttribute("buildView");
int authid = (int)request.getAttribute("authid");
switch(buildView) {
case 1:
    out.print("Author deleted: " + authid + "<br/><br/>\n");
    break;
case 2:
    out.print("Could not delete author with id " + authid + "<br/><br/>\n");
    break;
case 3:
    out.print("Created new author<br/><br/>\n");
    break;
case 4:
    out.print("ERROR: Unable to create an author!<br/><br/>\n");
    break;
case 5:
    out.print("Cannot create an author with an empty first or last name<br/><br/>\n");
    break;
}

List<Author> auth = (List<Author>)request.getAttribute("authorsList");

if (auth == null || auth.size() == 0) {
    out.print("No authors found!!!<br/>\n");
}
else {
    
    out.print("\n<table>\n");
    
    Author nextAuthor = null;
    Iterator<Author> authorIter = auth.iterator();
    while (authorIter.hasNext()) {
        nextAuthor = (Author)authorIter.next();
        out.print("\n<tr><td>");
        out.print(nextAuthor.getLastName());
        out.print("</td><td>");
        out.print(nextAuthor.getFirstName());
        out.print("</td><td><a href=\"./booktown?action=delete&authorid="+nextAuthor.getAuthorID()+"\">delete</a></td></tr>\n");
        
    }
    out.print("</table>\n<br/>\n");
}
%>
<form action="./booktown" method="get">
<input type="hidden" name="action" value="create"/>
Last name: <input type="text" size="12" name="lastname"/><br>
First name: <input type="text" size="12" name="firstname"/><br><br>  
<button type="submit">Create author</button><br/></form><br/>
</body>
</html>