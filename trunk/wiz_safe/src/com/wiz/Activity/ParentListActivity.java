package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ParentListActivity extends Activity {

	ParentListAdapter parentListAdapter;
	//부모 리스트 정보를 담는 리스트
	final ArrayList<ParentDetail> parentList = new ArrayList<ParentDetail>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.parent_list);
         
        //top-navigation 값 정의
        TextView topTitle = (TextView)findViewById(R.id.textTitle);
        if(topTitle != null){
        	topTitle.setText(R.string.title_parent);
        }
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ParentListActivity.this.finish();
			}
		});
        
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(ParentListActivity.this, "부모 리스트 삭제 ", Toast.LENGTH_SHORT).show();
			}
		});
        
        
        //body 정의
        
        //서버와 통신하여 리스트 정보를 가져온다.
        //서버와 통신하여 리스트 정보를 가져온다.        
        //서버와 통신하여 리스트 정보를 가져온다.
        getParentList();
        
        //어댑터 정의
        parentListAdapter = new ParentListAdapter(this, R.layout.safe_list, parentList);
        ListView listView = (ListView)findViewById(R.id.list);
        //실행하면 화면에 listview를 보여준다.
        listView.setAdapter(parentListAdapter);
        
    } 
    
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	
		//body 정의
		//리스트를 초기화 하고 서버를 통하여 다시 가져온다.
		parentList.clear();
        //서버와 통신하여 리스트 정보를 가져온다.
        //서버와 통신하여 리스트 정보를 가져온다.        
        //서버와 통신하여 리스트 정보를 가져온다.
		getParentList();
        
        //어댑터 정의
        parentListAdapter = new ParentListAdapter(this, R.layout.safe_list, parentList);
        ListView listView = (ListView)findViewById(R.id.list);
        //실행하면 화면에 listview를 보여준다.
        listView.setAdapter(parentListAdapter);
	}

	//부모리스트를 가져오는 로직 - 실행하면 통신하여 리스트를 가져와서  arraylist에 담긴다.
	public void getParentList(){
		ParentDetail ne1 = new ParentDetail("Mia","010-1111-1111", R.drawable.noimg);
        parentList.add(ne1);
        ParentDetail ne2 = new ParentDetail("Char","010-2222-3333", R.drawable.noimg);
        parentList.add(ne2);
	}

/*    
    @Override
    proteted void onListItemClick (ListView l, View v, int position, long id){ 
    	super.onListItemClick(l, v, position, id);
    	//커스텀 리스트 뷰의 인덱스를 확인
    	//Toast.makeText(ParentListActivity.this, "==="+position+"===", Toast.LENGTH_SHORT).show();
    	
    	//커스텀 리스트뷰의 phone 정보를 가져온다.
    	ParentDetail data = parentAdapter.getItem(position);
    	
    	Intent intent = new Intent(ParentListActivity.this, ParentAcceptActivity.class);
		intent.putExtra("phonenum", data.getPhoneNo());
    	startActivity(intent);
    	
    }
 */  
    
	
	//custom list adapter 를 inner class 로 선언하여 사용
	class ParentListAdapter extends BaseAdapter {       
		
		Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ParentDetail> arSrc;
    	int layout;
		 
		public ParentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc) {
			maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout; 
		}
		
		public int getCount() {
			return arSrc.size();
		}

		public ParentDetail getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		} 
		
		@Override    
		public View getView(final int position, View convertView, ViewGroup parent) {
			final int pos = position;
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}       
			
			ParentDetail parentDetail = getItem(position);
			
			if (parentDetail != null) {             //하나의 이미지뷰와 2개의 텍스트뷰와 버튼 한개 정보를 받아온다.
				
				((TextView)convertView.findViewById(R.id.eName)).setText(parentDetail.getName());
	            
				((TextView)convertView.findViewById(R.id.info1)).setText(parentDetail.getPhoneNo());
	           
				if (arSrc.get(pos).getPhotoId() != -1) {
	            	((ImageView)convertView.findViewById(R.id.ePhoto)).setImageResource(parentDetail.getPhotoId());                
	            } else {
	            	((ImageView)convertView.findViewById(R.id.ePhoto)).setImageResource(R.drawable.noimg);  
	            }

				Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);
				btn_accept.setVisibility(View.VISIBLE);
				btn_accept.setText(R.string.btn_accept);
				
			}            
			
			//Button 기능 셋팅
			Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);
			btn_accept.setTag(position);
			//승낙하기 클릭 리스너
			btn_accept.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phonenum = (String)getItem(position).getPhoneNo();
					
					Intent intent = new Intent(ParentListActivity.this, ParentAcceptActivity.class);
					intent.putExtra("phonenum", phonenum);
					startActivity(intent);
					
				}

			});
			
			return convertView;     
			}

		}  
	
	
	//custom list data 를 inner class 로 선언
	class ParentDetail {       
		
		private String name;
	    private String phoneNo;
	    private int photo;
	    
	    public ParentDetail(String _name, String _pn, int _photo) {
	        this.name = _name;
	        this.phoneNo = _pn;
	        this.photo = _photo;
	    }
	    
	    public String getName() {
	        return name;
	    }

	    public String getPhoneNo() {
	        return phoneNo;
	    }

	    public int getPhotoId() {
	        return photo;
	    }

	}
	
	
	
	
	
}