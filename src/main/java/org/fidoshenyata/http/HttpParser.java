package org.fidoshenyata.http;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.text.*;
import java.net.URLDecoder;

public class HttpParser {
    private BufferedReader reader;
    private String method, url, body;

    private List<String> urlParts;
    private Hashtable<String, String> headers, params;
    private int[] ver;

    public HttpParser(InputStream is) {
        reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        headers = new Hashtable<String, String>();
        params = new Hashtable<String, String>();
        ver = new int[2];
        urlParts = new ArrayList<>();
        try {
            this.parseRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  int parseRequest() throws IOException {
        String initial;
        String[] cmd;
        String[] temp;
        int ret;

        ret = 200; // default is OK now
        initial = reader.readLine();
        if (initial == null || initial.length() == 0) return 0;
        if (Character.isWhitespace(initial.charAt(0))) {
            // starting whitespace, return bad request
            return 400;
        }

        cmd = initial.split("\\s");
        if (cmd.length != 3) return 400;

        if (cmd[2].indexOf("HTTP/") == 0 && cmd[2].indexOf('.') > 5) {
            temp = cmd[2].substring(5).split("\\.");
            try {
                ver[0] = Integer.parseInt(temp[0]);
                ver[1] = Integer.parseInt(temp[1]);
            } catch (NumberFormatException nfe) {
                ret = 400;
            }
        } else ret = 400;


        if(cmd[0].equals("GET") || cmd[0].equals("DELETE") ||
                cmd[0].equals("POST") || cmd[0].equals("PUT")){
            method = cmd[0];
            parseQueryParams(cmd[1]);
            parseHeaders();
            if (headers.isEmpty()) ret = 400;
        }

        if (cmd[0].equals("POST") || cmd[0].equals("PUT")) {
            parseBody();
        }
        else if (ver[0] == 1 && ver[1] >= 1) {
            if (cmd[0].equals("OPTIONS") ||
                    cmd[0].equals("TRACE") ||
                    cmd[0].equals("CONNECT")) {
                ret = 501; // not implemented
            }
        }
        else {
            // meh not understand, bad request
            ret = 400;
        }

        if (ver[0] == 1 && ver[1] >= 1 && getHeader("Host") == null) {
            ret = 400;
        }

        return ret;
    }

    private void parseQueryParams(String urlNQuery){
        String[] params;
        int idx = urlNQuery.indexOf('?');
        if (idx < 0) url = urlNQuery;
        else {
            url = URLDecoder.decode(urlNQuery.substring(0, idx), StandardCharsets.ISO_8859_1);
            params = urlNQuery.substring(idx+1).split("&");

            String[] temp;
            for (String param : params) {
                temp = param.split("=");
                if (temp.length == 2) {
                    this.params.put(URLDecoder.decode(temp[0], StandardCharsets.ISO_8859_1),
                            URLDecoder.decode(temp[1], StandardCharsets.ISO_8859_1));
                } else if (temp.length == 1 && param.indexOf('=') == param.length() - 1) {
                    // handle empty string separately
                    this.params.put(URLDecoder.decode(temp[0], StandardCharsets.ISO_8859_1), "");
                }
            }
        }
        parseUrlParts();
    }

    private void parseUrlParts(){
        String[] parts = url.split("/");
        for(int i = 1; i < parts.length; ++i){
            urlParts.add(parts[i]);
        }
    }

    private void parseHeaders() throws IOException {
        String line;
        int idx;

        line = reader.readLine();
        while (!line.equals("")) {
            idx = line.indexOf(':');
            if (idx < 0) {
                headers = null;
                break;
            }
            else {
                headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx+1).trim());
            }
            line = reader.readLine();

        }
    }

    private void parseBody() throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        if(reader.ready()){
            int y = reader.read();
            while(((char) y )!= '}' ){
                stringBuilder.append((char) y);
                y = reader.read();
            }
            stringBuilder.append("}");
            body = stringBuilder.toString();
        }
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return method;
    }

    public List<String> getUrlParts(){
        return urlParts;
    }

    public int getUrlPartsLength(){
        return urlParts.size();
    }

    public boolean urlContains(String item){
        return urlParts.contains(item);
    }

    public String getHeader(String key) {
        if (headers != null)
            return headers.get(key.toLowerCase());
        else return null;
    }

    public Hashtable<String, String> getHeaders() {
        return headers;
    }

    public String getRequestURL() {
        return url;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public Hashtable<String, String> getParams() {
        return params;
    }

    public String getVersion() {
        return ver[0] + "." + ver[1];
    }

    public int compareVersion(int major, int minor) {
        if (major < ver[0]) return -1;
        else if (major > ver[0]) return 1;
        else if (minor < ver[1]) return -1;
        else if (minor > ver[1]) return 1;
        else return 0;
    }

    public static String getDateHeader() {
        SimpleDateFormat format;
        String ret;

        format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        ret = "Date: " + format.format(new Date()) + " GMT";

        return ret;
    }
}

   
    
    
    
  