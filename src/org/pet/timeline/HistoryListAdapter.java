package org.pet.timeline;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter {

	private ArrayList<HistoryData> data;
	
	private static final String TAG = HistoryListAdapter.class.getCanonicalName();
	
	private LayoutInflater inflater;
	
	public HistoryListAdapter(Context context, ArrayList<HistoryData> data){
		this.data = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getImageId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = inflater.inflate(R.layout.history_item, viewGroup, false);
		ImageView categoryImageView = (ImageView) view.findViewById(R.id.historyCategoryImage);
		TextView dataTextView = (TextView) view.findViewById(R.id.historyDataText);
		HistoryData data = this.data.get(position);
		categoryImageView.setImageResource(data.getImageId());
		if(data.getAppIcon() != null) {
			categoryImageView.setImageDrawable(data.getAppIcon());
		}
		dataTextView.setText(data.getText());
		return view;
	}
	
}
