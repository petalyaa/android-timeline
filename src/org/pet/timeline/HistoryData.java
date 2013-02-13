package org.pet.timeline;

import java.sql.Timestamp;

import android.graphics.drawable.Drawable;

public class HistoryData implements Comparable<HistoryData> {

	public enum DataType {
		CALL, SMS, PLAY
	};
	
	public enum CallType {
		INCOMING, OUTGOING, MISSED
	}

	private int imageId;
	
	private Drawable appIcon;

	private String text;

	private Timestamp timestamp;
	
	private DataType type;
	
	private CallType callType;

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public CallType getCallType() {
		return callType;
	}

	public void setCallType(CallType callType) {
		this.callType = callType;
	}

	@Override
	public int compareTo(HistoryData another) {
		int res = 0;
		Timestamp thisTime = getTimestamp();
		Timestamp anotherTime = another.getTimestamp();
		if(thisTime != null && anotherTime != null) {
			res = anotherTime.compareTo(thisTime);
		} else {
			res = -1;
		}
		return res;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

}
