package httpserver.itf.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl implements HttpRicmletResponse {
	protected HttpServer m_hs;
	protected PrintStream m_ps;
	protected HttpRequest m_req;

	private HashMap<String, String> cookies = new HashMap<String, String>();

	protected HttpRicmletResponseImpl(HttpServer hs, HttpRequest req, PrintStream ps) {
		m_hs = hs;
		m_req = req;
		m_ps = ps;
	}

	@Override
	public void setReplyOk() throws IOException {
		m_ps.println("HTTP/1.0 200 OK");
		m_ps.println("Date: " + new Date());
		m_ps.println("Server: ricm-http 1.0");
		m_ps.println(sendCookies());
//		System.out.println(sendCookies() + "\n");
	}

	@Override
	public void setReplyError(int codeRet, String msg) throws IOException {
		m_ps.println("Date: " + new Date());
		m_ps.println("Server: ricm-http 1.0");
		m_ps.println("Content-type: text/html");
		m_ps.println();
		m_ps.println("<HTML><HEAD><TITLE>" + msg + "</TITLE></HEAD>");
		m_ps.println("<BODY><H4>HTTP Error " + codeRet + ": " + msg + "</H4></BODY></HTML>");
		m_ps.flush();
	}

	@Override
	public void setContentLength(int length) throws IOException {
		// We don't need to have the resource's length for this implementation of our
		// server.
		// Could be done if the project evolves.
	}

	@Override
	public void setContentType(String type) throws IOException {
		m_ps.println("Content-type: " + type);
	}

	@Override
	public PrintStream beginBody() throws IOException {
		m_ps.println();
		return m_ps;
	}

	@Override
	public void setCookie(String name, String value) {
		cookies.put(name, value);
	}

	private String sendCookies() {
		String cookie = null;
		if (!cookies.isEmpty()) {
			cookie = "Set-Cookie: ";
			Set<String> keys = cookies.keySet();
			Iterator<String> iter = keys.iterator();

			while (iter.hasNext()) {
				String key = iter.next();
				cookie += key;
				cookie += "=";
				cookie += cookies.get(key);
				cookie += ";";
			}			
		}
		String session_id = m_hs.getSessionId();
		if (session_id != null) {
			if (cookie == null)
				cookie = "Set-Cookie: ";
			cookie += "session_id";
			cookie += "=";
			cookie += session_id;
			cookie += ";";
		}
		return cookie;
	}

}
