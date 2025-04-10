package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArg(String name) {
		String ressname = getRessname();
		
		if (!ressname.contains("?"))
			return null;
		
		int args_ind = ressname.indexOf("?");
		
		String args = ressname.substring(args_ind);
		String[] args_tab = args.split("&");
		
		for (String arg : args_tab) {
			String[] arg_tab = arg.split("=");
			if (name.equals(arg_tab[0]))
				return arg_tab[1];
		}
		return null;
	}

	@Override
	public String getCookie(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void process(HttpResponse resp) throws Exception {
		String name = m_ressname;
		int index = name.indexOf("/", 1) + 1;
		String args = "";
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
