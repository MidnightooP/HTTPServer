package httpserver.itf.impl;

import java.util.Date;
import java.util.HashMap;

import httpserver.itf.HttpSession;

public class HttpSessionImpl implements HttpSession {
	
	private String id;
	private HashMap<String, Object> data = new HashMap<String, Object>();
	private Date last_access;
	private int MaxInactiveInterval;
	
	public HttpSessionImpl(String id) {
		this.id = id;
		this.last_access = new Date();
//		this.MaxInactiveInterval = 900000; // 15 minutes en milli
		this.MaxInactiveInterval = 3000; // 15 minutes en milli
	}

	@Override
	public String getId() {
		updateLastAccess();
		return id;
	}

	@Override
	public Object getValue(String key) {
		updateLastAccess();
		return data.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		updateLastAccess();
		data.put(key,value);
	}
	
	@Override
	public void setMaxInactiveInterval(int interval) {
		this.MaxInactiveInterval = interval;
	}
	
	@Override
	public int getMaxInactiveInterval() {
		return this.MaxInactiveInterval;
	}
	
	@Override
    public void updateLastAccess() {
        this.last_access = new Date();
    }
	
	@Override
    public Date getLastAccessTime() {
        return this.last_access;
    }

}
