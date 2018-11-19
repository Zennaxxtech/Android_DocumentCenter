package com.documentcenterapp.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.documentcenterapp.R;

public class CustomAlertDialogue {

    public static void showAlert(Context mContext,
                                 String title,
                                 String message1,
                                 String message2,
                                 String firstBtnTitle,
                                 String secondBtnTitle,
                                 OnClickListener btnFirstClickListner,
                                 OnClickListener btnSecondClickListner) {
        showAlert(mContext, title, message1, message2, firstBtnTitle, secondBtnTitle, btnFirstClickListner, btnSecondClickListner, true);
    }

    public static void showAlert(Context mContext,
                                 String title,
                                 String message1,
                                 String message2,
                                 String firstBtnTitle,
                                 String secondBtnTitle,
                                 OnClickListener btnFirstClickListner,
                                 OnClickListener btnSecondClickListner,
                                 boolean showCancelButton) {

        final Dialog dialog = new Dialog(mContext);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_alert_dialog);
		
		/*ImageView imgView =(ImageView) dialog.findViewById(R.id.cancel);
		imgView.getLayoutParams().height=30;
		imgView.getLayoutParams().width=30;*/

        TextView lblTitle = (TextView) dialog.findViewById(R.id.title);
        TextView lblMessage1 = (TextView) dialog.findViewById(R.id.message1);
        TextView lblMessage2 = (TextView) dialog.findViewById(R.id.message2);
        lblMessage1.setTypeface(null, Typeface.BOLD);
        lblMessage2.setTypeface(null, Typeface.ITALIC);

        TextView btnDial1 = (TextView) dialog.findViewById(R.id.btn1);
        TextView btnDial2 = (TextView) dialog.findViewById(R.id.btn2);

        dialog.setCancelable(false);
		
		/*if(imgView != null && !("").equals(imgView) && showCancelButton){
			imgView.setVisibility(View.VISIBLE);
			imgView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();

				}
			});
		}		*/

        if (null == title || "".equalsIgnoreCase(title)) {
            lblTitle.setVisibility(View.GONE);
        } else {
            lblTitle.setText(title);
        }

        if (null == message1 || "".equalsIgnoreCase(message1)) {
            lblMessage1.setVisibility(View.GONE);
        } else {
            lblMessage1.setText(message1);
        }

        if (null == message2 || "".equalsIgnoreCase(message2)) {
            lblMessage2.setVisibility(View.GONE);
        } else {
            lblMessage2.setText(message2);
        }

        if (null == firstBtnTitle || "".equalsIgnoreCase(firstBtnTitle)) {
            btnDial1.setVisibility(View.GONE);
        } else {
            btnDial1.setText(firstBtnTitle);
            if (btnFirstClickListner != null) {
                btnDial1.setOnClickListener(btnFirstClickListner);
                btnDial1.setTag(dialog);

            } else {
                btnDial1.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
            }
        }

        if (null == secondBtnTitle || "".equalsIgnoreCase(secondBtnTitle)) {
            btnDial2.setVisibility(View.GONE);
        } else {
            btnDial2.setText(secondBtnTitle);
            if (btnSecondClickListner != null) {
                //dialog.findViewById(R.id.viewDividerDialog).setVisibility(View.VISIBLE);
                btnDial2.setOnClickListener(btnSecondClickListner);
                btnDial2.setTag(dialog);
            } else {
                btnDial2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }

        dialog.show();
    }
}
