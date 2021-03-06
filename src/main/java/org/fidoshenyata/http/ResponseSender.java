package org.fidoshenyata.http;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseSender {
    private final static String APP_JSON = "application/json";

    private final PrintWriter pw;

    public ResponseSender(OutputStream out){
        pw = new PrintWriter(out, true);
    }

    public void sendResponse(int code,String contentType,  String body){
        // Start sending our reply, using the HTTP 1.1 protocol
        pw.print("HTTP/1.1 "+code+" \r\n"); // Version & status code
        pw.print("Content-Type: "+contentType+"\r\n"); // The type of data
        pw.print("Access-Control-Allow-Origin: *\r\n");
        pw.print("Connection: close\r\n"); // Will close stream
        pw.print("\r\n"); // End of headers
        pw.println(body);
    }

    public void sendJsonResponse(int code, String body) {
        this.sendResponse(code, APP_JSON, body);
    }

    public void sendOptionsResponse(){
        pw.print("HTTP/1.1 204 No Content\r\n");
        pw.print("Access-Control-Allow-Methods: POST, GET, OPTIONS, PUT, DELETE\r\n");
        pw.print("Access-Control-Allow-Headers: content-type, x-auth\r\n");
        pw.print("Connection: Keep-Alive\r\n");
    }
}
