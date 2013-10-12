package com.kukung.app.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class TOCItem {
	private int level = 1;
	private String title = "";
	private String fileId = "";
	private URL imagUrl = null;
	
	public boolean hasPageImage() {
		if (imagUrl == null) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString(){
		return level+":"+title+"("+fileId+")"+" "+imagUrl;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public URL getImagUrl() {
		return imagUrl;
	}
	
	public void setImagUrl(URL imagUrl) {
		this.imagUrl = imagUrl;
	}
	
	public void build(NodeList childNodeList) {
		Node childNode = null;
		for (int i = 0; i < childNodeList.getLength(); i++) {
			childNode = childNodeList.item(i);
			if (childNode.getNodeName().equals("fileId")) {
				this.setFileId(childNode.getTextContent());
			} else if (childNode.getNodeName().equals("level")) {
				this.setLevel(Integer.parseInt(childNode.getTextContent()));
			} else if (childNode.getNodeName().equals("title")) {
				this.setTitle(childNode.getTextContent());
			} else if (childNode.getNodeName().equals("imgUrl")) {
				try {
					this.setImagUrl(new URL(childNode.getTextContent()));
				} catch (Exception e) {
					Log.e("unja", "fail at loading url", e);
				}
			}
		}
	}
}
