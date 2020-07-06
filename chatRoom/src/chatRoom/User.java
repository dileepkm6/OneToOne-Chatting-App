package chatRoom;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class User
 */
@WebServlet("/User")
public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public User() {
        super();
        // TODO Auto-generated constructor stub
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter printWriter= response.getWriter();
		HttpSession httpSession= request.getSession(true);
		String username=request.getParameter("username");
		httpSession.setAttribute("username", username);
		String to=request.getParameter("to");
		request.setAttribute("username", username);
		
		if(to!=null && to!="")
		{
			request.setAttribute("to", to);
			request.getRequestDispatcher("buyer.jsp").forward(request, response);
//			response.sendRedirect("test.jsp");
//			printWriter.println("<html>");
//			printWriter.println("<head><title>chat</title>");
//			printWriter.println("<script language=\"javascript\">");
//			RequestDispatcher requestDispatcher = request.getRequestDispatcher("WEB-INF/js/websocket.js");
//			requestDispatcher.include(request, response);
//			printWriter.println("</script>");
//			printWriter.println("<style>");     // start style
//			  // enclose style attributes withing the <style> </style> elements
//			printWriter.println("#to {");        // note leading brace
//			printWriter.println("display:inline");
////			printWriter.println("background-color:yellow;");
////			printWriter.println("border: 1px solid black;");
//			printWriter.println("}");  
//			printWriter.println("#status {");        // note leading brace
//			printWriter.println("display:inline");
////			printWriter.println("background-color:yellow;");
////			printWriter.println("border: 1px solid black;");
//			printWriter.println("}");  
//			// note trailing brace for h1 style
//			  // add styles for other elements here using similar structure
//			  // note that separate lines are used for clarity -
//			  // all of the above could be one println
//			printWriter.println("</style>");
//			printWriter.println("</head>");
//			printWriter.println("<body>");
//			printWriter.println("<p id=\"from\">"+username+"</p>");
//			printWriter.println("<p id=\"to\">"+to+"<p id=\"status\"></p></p>");
//			printWriter.println("<textarea id=\"messageTextArea\" readonly=\"readonly\" rows=\"10\" cols=\"45\"></textarea>");
//			printWriter.println("<br/>");
//			printWriter.println("<input type=\"text\" id=\"messageText\">");
//			printWriter.println("<input type=\"button\" value=\"send\" onclick=\"sendMessage()\">");
//			printWriter.println("<br/>");
//			printWriter.println("</body>");
//			printWriter.println("</html>");
		}
		else
		{
			request.getRequestDispatcher("seller.jsp").forward(request, response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
