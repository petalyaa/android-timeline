package org.pet.timeline;

import android.content.Context;

public class CallPopupItemsHandler extends BasePopupItemHandler implements PopupItemHandler {


	public CallPopupItemsHandler(Context context) {
		super(context);
	}

	@Override
	public void performAction(int itemClick) {
		switch (itemClick) {
		case 0:
			viewDetail();
			break;
		case 1:
			break;
		case 2:
			break;
		default:
			break;
		}
	}
	
	private void viewDetail() {
		
	}

}
