package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";
	
	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}
	
	public void process(HttpResponse resp) throws Exception {
		String resname = getRessname();
		if(resname.equals("/")) {
			resname = "/" + DEFAULT_FILE;
		}
		
		File f = new File(m_hs.getFolder() + resname);
		if(f.exists()) {
			if (f.isDirectory()) {
				resp.setReplyError(400, resname + " is a directory");
				return;
			}	
			resp.setReplyOk();
			resp.setContentLength((int) f.length());
			resp.setContentType(getContentType(resname));
			PrintStream ps = resp.beginBody();
			
			/* get file content */
			FileInputStream fis = new FileInputStream(f);
			byte[] fileContent = new byte[(int) f.length()];
			fileContent = fis.readAllBytes();
			ps.write(fileContent);
		} else {
			resp.setReplyError(404, "File not found");
		}
	}

}
