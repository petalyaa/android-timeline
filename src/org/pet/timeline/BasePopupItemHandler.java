package org.pet.timeline;

import android.content.Context;

public class BasePopupItemHandler {
	
	private Context context;
	
	public BasePopupItemHandler(Context context) {
		this.setContext(context);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
