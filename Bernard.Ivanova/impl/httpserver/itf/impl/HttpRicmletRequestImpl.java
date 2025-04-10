package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {

	private HashMap<String, String> args = new HashMap<String, String>();
	private HashMap<String, String> cookies = new HashMap<String, String>();


	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		parseArgs();
		parseCookies(br);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCookie(String name) {
		return cookies.get(name);
	}
	
	private void parseCookies(BufferedReader br) throws IOException {
		
		String line;
		while ((line = br.readLine()) != null && !line.isEmpty()) {
			
			if (!line.startsWith("Cookie:"))
				continue;
			
			line = line.substring(8);
			
			String[] cookies_tab = line.split(";");
			
			for (String cookie_x : cookies_tab) {
				String[] elem = cookie_x.split("=");
				elem[0] = elem[0].trim();
				elem[1] = elem[1].trim();
				cookies.put(elem[0], elem[1]);
//				System.out.println(elem[0] + "=" + elem[1]);
			}
		}
	}
	
	@Override
	public String getArg(String name) {
		return args.get(name);
	}

	private void parseArgs() {
		String ressname = getRessname();

		if (!ressname.contains("?"))
			return;

		int args_ind = ressname.indexOf("?");

		String args_str = ressname.substring(args_ind + 1);
		String[] args_tab = args_str.split("&");

		for (String arg_x : args_tab) {
			String[] arg = arg_x.split("=");
			args.put(arg[0], arg[1]);
		}
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		String name = m_ressname;
		int index = name.indexOf("/", 1) + 1;
		if (name.contains("?")) {
			int args_ind = name.indexOf("?");
			name = name.substring(index, args_ind);
		} else {
			name = name.substring(index);
		}
		name = name.replaceAll("/", ".");
		HttpRicmlet instance = m_hs.getInstance(name);
		if (instance != null) {
			instance.doGet(this, (HttpRicmletResponse) resp);
		} else {
			resp.setReplyError(404, "Ricmlet not found");
		}
	}

}
