package com.kukung.app.pk200;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kukung.app.model.TOC;
import com.kukung.app.model.TOCItem;
import com.kukung.app.model.TocAdapter;
import com.kukung.app.pk200.R;

public class ViewTocActivity extends Activity implements OnItemClickListener, OnClickListener {
	private static Context mainContext;
	private static List<TOCItem> tocItems;
	private static int backpress = 0;
	private ProgressDialog progressDialog;
	private String notice;
	private Button orderingButton;

	private Handler handler = new Handler() { 
		public void handleMessage(Message msg) { 
			progressDialog.dismiss();
			if(msg != null && msg.getData() != null) {
				String fileId = msg.getData().getString("fileId");
				if (fileId != null) {
					showNotePages(fileId);
				} else {
					Toast.makeText(ViewTocActivity.mainContext, "네트웍이 불안정 합니다. 차후 다시 시도해 주세요.",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(ViewTocActivity.mainContext, "페이지 이미지 로딩에 실패했습니다.", 
						Toast.LENGTH_LONG).show();
			}
		} 
	};
	
	@Override
	public void onBackPressed(){
		backpress = (backpress + 1);
		if (backpress == 1) {
			Toast.makeText(getApplicationContext(), "한 번 더 누르면 프로그램 닫습니다.", Toast.LENGTH_SHORT).show();
		} else {
			if (backpress > 1) {         
				this.finish();     
			}
			backpress = 0;
		}
	} 
	
	private void showNotePages(String fileId) {
		Intent wannaViewContents = new Intent(this, PageImageViewActivity.class);
		wannaViewContents.putExtra(PageImageViewActivity.EXTRA_KEY_PAGE_ID, fileId);
		startActivity(wannaViewContents);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        if (tocItems == null) {
        	tocItems = TOC.buildTOC(this);
        }
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.orderingButton) {
			String currentText = (String)orderingButton.getText();
			ListView tocListView = (ListView)this.findViewById(R.id.tocListView);
			TocAdapter currerntTocAdapter = ((TocAdapter)tocListView.getAdapter());
			
			if (currentText.equals("알파벳순 보기")) {
				orderingButton.setText("원래 목차 보기");
				currerntTocAdapter.setTocItemList(TOC.getItemListSortedByAlphabet());
			} else {
				orderingButton.setText("알파벳순 보기");
				currerntTocAdapter.setTocItemList(tocItems);
			}
			
			((ListView)this.findViewById(R.id.tocListView)).setAdapter(currerntTocAdapter);
		}
	}
    
    private String getNoticeFromServer() {
    	StringBuffer noticeBuffer = new StringBuffer();
    	BufferedReader in = null;
    	try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://unja66.woobi.co.kr/pk200/notice.txt"));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				if (line.trim().equals("") == false) {
					noticeBuffer.append(line);
				}
			}
			in.close();
		} catch (Exception e) {
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					return "";
				}
			}
		}
    	
    	return noticeBuffer.toString();
	}

	private void updateNoticeReadHistory() {
		SharedPreferences pref = getSharedPreferences("noticePref", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editor = pref.edit();
    	editor.putString("notice", notice);
    	editor.commit();
	}
	
	private String getLastNotice() {
		SharedPreferences prefs = getSharedPreferences("noticePref", Activity.MODE_PRIVATE);
		String notice = prefs.getString("notice", "");
		return notice;
	}

	private void setupTOCListView() {
    	TocAdapter listItemAdapter = new TocAdapter(this, R.layout.toc_item, tocItems);
    	ListView tocListView = (ListView)this.findViewById(R.id.tocListView);
    	tocListView.setAdapter(listItemAdapter);
    	tocListView.setOnItemClickListener(this);
	}

	@Override
    public void onResume() {
    	super.onResume();
    	backpress = 0;
    	setContentView(R.layout.toc);
    	
    	notice = getNoticeFromServer();
        if (notice.equals(getLastNotice())) {
        	notice = "";
        }
    	
    	orderingButton = (Button)findViewById(R.id.orderingButton);
        orderingButton.setOnClickListener(this);
        
        setupTOCListView();
        showNotice();
    }

	private void showNotice() {
		if (notice == null || notice.trim().equals("")) {
			return;
		}
		
		new AlertDialog.Builder(this)
			.setTitle("알려드립니다")
			.setMessage(notice)
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.dismiss();
		        }
		     }).show();
		updateNoticeReadHistory();
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, final View view, int position, long id) {
		final TOCItem clickedItem = tocItems.get(position); 
		
		if (clickedItem.hasPageImage()) {
			((TextView)view.findViewById(R.id.tocTitle)).setBackgroundColor(Color.MAGENTA);
			progressDialog = ProgressDialog.show(this, "", "페이지 로딩 중", true);
			Thread thread = new Thread(new Runnable() { 
	            public void run() {
	            	String fileId = clickedItem.getFileId();
	            	URL imageUrl = clickedItem.getImagUrl();
	            	
	            	if (TOC.loadImage(fileId, imageUrl)) {
	            		Message message = new Message();
	            		Bundle bundle = new Bundle();
	            		bundle.putString("fileId", fileId);
	            		message.setData(bundle);
	            		handler.sendMessage(message); 
	            	} else {
	            		handler.sendEmptyMessage(0);
	            	}
	            } 
	        }); 
	        thread.start(); 
		} 
	}

	
	
}