package httpserver.itf.impl;

import java.io.File;
import java.io.IOException;


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
		File f = new File("./FILES/" + getRessname());
		if(f.exists()) {
			resp.setReplyOk();
			resp.setContentLength((int) f.length());
			resp.setContentType(getContentType(getRessname()));
			resp.beginBody();
		} else {
			resp.setReplyError(404, "File not found");
		}
	}

}
