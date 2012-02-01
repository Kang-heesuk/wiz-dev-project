package com.wiz.Activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ParentListActivity extends ListActivity {

	ArrayAdapter<CustomListData> parentAdapter;
	final ArrayList<CustomListData> parentList = new ArrayList<CustomListData>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.parent_list);
         
        //top-navigation �� ����
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
				Toast.makeText(ParentListActivity.this, "�θ� ����Ʈ ���� ", Toast.LENGTH_SHORT).show();
			}
		});
        
        
        //body ����
        
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        //������ ����Ͽ� ����Ʈ ������ �����´�.        
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        
        //����Ʈ ������ ����
        CustomListData ne1 = new CustomListData("Mia","010-1111-1111", R.drawable.noimg);
        parentList.add(ne1);
        CustomListData ne2 = new CustomListData("Char","010-2222-3333", R.drawable.noimg);
        parentList.add(ne2);
        //����� ����
        parentAdapter = new CustomListAdapter(this, R.layout.custom_listview, parentList);
        //�����ϸ� ȭ�鿡 listview�� �����ش�.
        setListAdapter(parentAdapter);
        
    } 
    
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	
		//body ����
		//����Ʈ�� �ʱ�ȭ �ϰ� ������ ���Ͽ� �ٽ� �����´�.
		parentList.clear();
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        //������ ����Ͽ� ����Ʈ ������ �����´�.        
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        
        //����Ʈ ������ ����
        CustomListData ne1 = new CustomListData("Mia","010-3333-4444", R.drawable.noimg);
        parentList.add(ne1);
        CustomListData ne2 = new CustomListData("Char","010-5555-6666", R.drawable.noimg);
        parentList.add(ne2);
        
     
        
        //����� ����
        parentAdapter = new CustomListAdapter(this, R.layout.custom_listview, parentList);
        //�����ϸ� ȭ�鿡 listview�� �����ش�.
        setListAdapter(parentAdapter);
	}


/*    
    @Override
    proteted void onListItemClick (ListView l, View v, int position, long id){ 
    	super.onListItemClick(l, v, position, id);
    	//Ŀ���� ����Ʈ ���� �ε����� Ȯ��
    	//Toast.makeText(ParentListActivity.this, "==="+position+"===", Toast.LENGTH_SHORT).show();
    	
    	//Ŀ���� ����Ʈ���� phone ������ �����´�.
    	CustomListData data = parentAdapter.getItem(position);
    	
    	Intent intent = new Intent(ParentListActivity.this, ParentAcceptActivity.class);
		intent.putExtra("phonenum", data.getPhoneNo());
    	startActivity(intent);
    	
    }
 */  
    
	
	//custom list adapter �� inner class �� �����Ͽ� ���
	class CustomListAdapter extends ArrayAdapter<CustomListData> {       
		
		private ArrayList<CustomListData> items;       
		 
		public CustomListAdapter(Context context, int textViewResourceId, ArrayList<CustomListData> items) {
			super(context, textViewResourceId, items);         
			this.items = items; 
		}
		
		@Override    
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.custom_listview, null);
			}         
			
			CustomListData custom_list_data = items.get(position);
			
			if (custom_list_data != null) {             //�ϳ��� �̹������ 2���� �ؽ�Ʈ��� ��ư �Ѱ� ������ �޾ƿ´�.
				
				((TextView)v.findViewById(R.id.eName)).setText(custom_list_data.getName());
	            
				((TextView)v.findViewById(R.id.ePhoneNo)).setText(custom_list_data.getPhoneNo());
	           
				if (custom_list_data.getPhotoId() != -1) {
	            	((ImageView)v.findViewById(R.id.ePhoto)).setImageResource(custom_list_data.getPhotoId());                
	            } else {
	            	((ImageView)v.findViewById(R.id.ePhoto)).setImageResource(R.drawable.noimg);  
	            }

				Button btn_accept = (Button)v.findViewById(R.id.btn_accept);
				btn_accept.setText(R.string.btn_accept);
				
			}            
			
			//Button ��� ����
			Button btn_accept = (Button)v.findViewById(R.id.btn_accept);
			btn_accept.setTag(position);
			//�³��ϱ� Ŭ�� ������
			btn_accept.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phonenum = (String)parentAdapter.getItem(position).getPhoneNo();
					
					Intent intent = new Intent(ParentListActivity.this, ParentAcceptActivity.class);
					intent.putExtra("phonenum", phonenum);
					startActivity(intent);
					
				}

			});
			
			return v;     
			}

		}  
	
	
	//custom list data �� inner class �� ����
	class CustomListData {       
		
		private String name;
	    private String phoneNo;
	    private int photo;
	    
	    public CustomListData(String _name, String _pn, int _photo) {
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