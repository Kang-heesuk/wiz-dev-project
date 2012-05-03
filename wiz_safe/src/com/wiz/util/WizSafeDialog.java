package com.wiz.util;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.wiz.Activity.R;

public class WizSafeDialog  {
	
	private static Dialog m_loadingDialog = null;

	public static void showLoading(Context context) {
		if (m_loadingDialog == null) {

			m_loadingDialog = new Dialog(context, R.style.TransDialog);
			ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleInverse);
			pb.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
			LayoutParams params = new LayoutParams(200, 200);
			m_loadingDialog.addContentView(pb, params);
			m_loadingDialog.setCancelable(false);

		}

		m_loadingDialog.show();

	}

	public static void hideLoading() {
		if (m_loadingDialog != null) { 

			m_loadingDialog.dismiss(); 

			m_loadingDialog = null; 
		}
	}
	
}
