package org.irri.iric.portal.auth;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    request.getSession().invalidate();
//    sess.removeAttribute(SessionConstants.CONTENT_MANAGER);
//	sess.removeAttribute(SessionConstants.USER_CREDENTIAL);
    response.sendRedirect("/index.zul");
  }
}