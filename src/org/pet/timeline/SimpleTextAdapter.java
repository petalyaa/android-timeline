package org.pet.timeline;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleTextAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private String[] array;
	
	private HistoryData data;
	
	public SimpleTextAdapter(Context context, String[] array, HistoryData data) {
		this.array = array;
		this.data = data;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return array.length;
	}

	@Override
	public Object getItem(int position) {
		return array[position];
	}

	@Override
	public long getItemId(int position) {
		return array[position].hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = inflater.inflate(R.layout.simple_text_view, viewGroup, false);
		TextView textView = (TextView) view.findViewById(R.id.simple_text_view);
		
		String text = array[position];
		Map<String, String> replacement = new HashMap<String, String>();
		replacement.put("name", data.getName());
		text = StringUtil.replaceString(text, replacement);
		
		textView.setText(text);
		return view;
	}

}
