package com.yun.testprinter;

import com.yun.printer.BocaPrinter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button bt;
	private BocaPrinter bp;
	private TickInfo t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bt = (Button)findViewById(R.id.button1);
		
		//打印机设置
		bp = new BocaPrinter(this);
		t = new TickInfo();
		t.setProductName("baoluawdad");
		t.setArea("保利剧院测试");
		t.setTicketNo("tugayihudjjnahbygbhnujm");
		t.setOrderId(213211231);
		t.setOrderTicketId(2132131);
		t.setPrice(1321);
		t.setRemark("asdad");
		t.setSeat("asdasda");
		t.setShowStartTime(213232131);
		t.setVenueName("fgyuhyjukl");
		
		
		
		
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bp.write(new CustomTicketPhotoView(MainActivity.this, t).getPhotoBytes());
			}
		});
		
		
	}
}
