package httpserver.itf.impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpSession;


/**
 * Basic HTTP Server Implementation 
 * 
 * Only manages static requests
 * The url for a static ressource is of the form: "http//host:port/<path>/<ressource name>"
 * For example, try accessing the following urls from your brower:
 *    http://localhost:<port>/
 *    http://localhost:<port>/voile.jpg
 *    ...
 */
public class HttpServer {

	private int m_port;
	private File m_folder;  // default folder for accessing static resources (files)
	private ServerSocket m_ssoc;
	
	private HashMap<String, HttpRicmlet> instances = new HashMap<String, HttpRicmlet>();
	private HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	private String session_id = null;

	protected HttpServer(int port, String folderName) {
		m_port = port;
		if (!folderName.endsWith(File.separator)) 
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc=new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e );
			System.exit(1);
		}
	}
	
	public File getFolder() {
		return m_folder;
	}
	
	public HttpRicmlet getInstance(String clsname)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		HttpRicmlet inst = null;
//		System.out.println(clsname);
		if (instances.containsKey(clsname)) {
			inst = instances.get(clsname);
		} else {
			Class<?> c = Class.forName(clsname);
			inst = (HttpRicmlet) c.getDeclaredConstructor().newInstance();
			instances.put(clsname, inst);
		}
		return inst;
	}

	public void setSessionId(String session_id) {
		this.session_id = session_id;
	}
	
	public String getSessionId() {
		return session_id;
	}

	
	private void controlSession() {
		if (sessions.isEmpty())
			return;
		Set<String> keys = sessions.keySet();
		Iterator<String> iter = keys.iterator();
		
		LinkedList<String> todelete = new LinkedList<String>();
		
		while (iter.hasNext()) {
			String currKey = iter.next();
			HttpSession sess = sessions.get(currKey);
			
			Date now = new Date();
			
			Date last_acces = sess.getLastAccessTime();
			
			long duree_sans_acces = now.getTime() - last_acces.getTime();
			
			if (duree_sans_acces >= sess.getMaxInactiveInterval()) {
				todelete.add(currKey);
			}
		}
		
		Iterator<String> iter2 = todelete.iterator();
		while (iter2.hasNext()) {
			sessions.remove(iter2.next());
		}
	}

	/*
	 * Reads a request on the given input stream and returns the corresponding HttpRequest object
	 */
	public HttpRequest getRequest(BufferedReader br) throws IOException {
		HttpRequest request = null;
		
		controlSession();
		
		String startline = br.readLine();
		StringTokenizer parseline = new StringTokenizer(startline);
		String method = parseline.nextToken().toUpperCase(); 
		String ressname = parseline.nextToken();
		if (method.equals("GET")) {
			if(!ressname.startsWith("/ricmlets")) {
				request = new HttpStaticRequest(this, method, ressname);
			} else {
				request = new HttpRicmletRequestImpl(this, method, ressname, br);
			}
		} else 
			request = new UnknownRequest(this, method, ressname);
		return request;
	}


	/*
	 * Returns an HttpResponse object associated to the given HttpRequest object
	 */
	public HttpResponse getResponse(HttpRequest req, PrintStream ps) {
		String ressname = req.getRessname();
		if(ressname.startsWith("/ricmlets")) {
			return new HttpRicmletResponseImpl(this, req, ps);			
		} else {
			return new HttpResponseImpl(this, req, ps);
		}
	}
	
	public HttpSession getSession(String session_id) {
		return sessions.get(session_id);
	}
	
	public void setSession(String session_id, HttpSession session) {
		sessions.put(session_id, session);
	}


	/*
	 * Server main loop
	 */
	protected void loop() {
		try {
			while (true) {
				Socket soc = m_ssoc.accept();
				(new HttpWorker(this, soc)).start();
			}
		} catch (IOException e) {
			System.out.println("HttpServer Exception, skipping request");
			e.printStackTrace();
		}
	}

	
	
	public static void main(String[] args) {
		int port = 0;
		if (args.length != 2) {
			System.out.println("Usage: java Server <port-number> <file folder>");
		} else {
			port = Integer.parseInt(args[0]);
			String foldername = args[1];
			HttpServer hs = new HttpServer(port, foldername);
			hs.loop();
		}
	}

}

