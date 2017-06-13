package com.pavlmir.pavlmirp2p.CustomAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pavlmir.pavlmirp2p.ChatActivity;
import com.pavlmir.pavlmirp2p.Entities.Message;
import com.pavlmir.pavlmirp2p.R;

import java.util.HashMap;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
	private List<Message> listMessage;
	private LayoutInflater inflater;
	public static Bitmap bitmap;
	private Context mContext;
	private HashMap<String,Bitmap> mapThumb;

	public ChatAdapter(Context context, List<Message> listMessage){
		this.listMessage = listMessage;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mapThumb = new HashMap<String, Bitmap>();
	}
	
	@Override
	public int getCount() {
		return listMessage.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		Message mes = listMessage.get(position);
		int type = mes.getmType();
		
		if(view == null){
			CacheView cache = new CacheView();            
            
			view = inflater.inflate(R.layout.chat_row, null);
			cache.chatName = (TextView) view.findViewById(R.id.chatName);
            cache.text = (TextView) view.findViewById(R.id.text);
            cache.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
	            
			view.setTag(cache);
		}
		
		//Retrive the items from cache
        CacheView cache = (CacheView) view.getTag();
        cache.chatName.setText(listMessage.get(position).getChatName());
        cache.chatName.setTag(cache);
        cache.chatName.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				CacheView cache = (CacheView) v.getTag();
				((ChatActivity)mContext).talkTo((String) cache.chatName.getText());
				return true;
			}
		});
        
        //Colourise differently own message
        if((Boolean) listMessage.get(position).isMine()){
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble_mine));
        }   
        else{
        	cache.relativeLayout.setBackground(view.getResources().getDrawable(R.drawable.chat_bubble));
        }
        
        //We disable all the views and enable certain views depending on the message's type
        disableAllMediaViews(cache);
        
        /***********************************************
          				Text Message
         ***********************************************/
        if(type == Message.TEXT_MESSAGE){           
        	enableTextView(cache, mes.getmText());
		}

		return view;
	}
	
	private void disableAllMediaViews(CacheView cache){
		cache.text.setVisibility(View.GONE);
		cache.fileSavedIcon.setVisibility(View.GONE);
	}
	
	private void enableTextView(CacheView cache, String text){
		if(!text.equals("")){
			cache.text.setVisibility(View.VISIBLE);
			cache.text.setText(text);
			Linkify.addLinks(cache.text, Linkify.PHONE_NUMBERS);
			Linkify.addLinks(cache.text, Patterns.WEB_URL, "myweburl:");
		}		
	}

	//Cache
	private static class CacheView{
		public TextView chatName;
		public TextView text;
		public RelativeLayout relativeLayout;
		public ImageView fileSavedIcon;
	}
}
