package com.wiz.util;

import com.wiz.Activity.R;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class MyProgressDialog extends Dialog {
	boolean flag = false;

	public static synchronized MyProgressDialog show(Context context,
			CharSequence title,
			CharSequence message) {
		return show(context, title, message, false);
	}

	public static synchronized MyProgressDialog show(Context context,
			CharSequence title,
			CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static synchronized MyProgressDialog show(Context context,
			CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static synchronized MyProgressDialog show(Context context,
			CharSequence title,
			CharSequence message, boolean indeterminate,
			boolean cancelable, OnCancelListener cancelListener) {
		MyProgressDialog dialog = new MyProgressDialog(context);
		dialog.setTitle(title);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		/* The next line will add the ProgressBar to the dialog. */
		ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleInverse);
		dialog.addContentView(pb, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.show();
		return dialog;

	}

	public static synchronized MyProgressDialog show(Context context,
			CharSequence title,
			CharSequence message, boolean indeterminate,
			boolean cancelable, OnCancelListener cancelListener, boolean flag) {
		MyProgressDialog dialog = new MyProgressDialog(context);
		dialog.setTitle(title);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		// dialog.setFeatureDrawable(featureId, drawable)

		/* The next line will add the ProgressBar to the dialog. */
		dialog.addContentView(new ProgressBar(context), new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.show();
		return dialog;
	}

	public MyProgressDialog(Context context) {
		super(context, R.style.NewDialog);
	}
}
