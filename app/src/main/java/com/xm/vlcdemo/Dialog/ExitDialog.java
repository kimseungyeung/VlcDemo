package com.xm.vlcdemo.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xm.vlcdemo.R;

public class ExitDialog extends Dialog {
    TextView tv_text;
    Button btn_ok,btn_cancle;
    public ExitDialog(Context context, View.OnClickListener listener,String text) {
        super(context);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setCancelable(false);
        setContentView(R.layout.exit_dialog);
        init(listener,text);
    }

    public void init(View.OnClickListener listener,String text){
        tv_text=findViewById(R.id.tv_text);
        btn_ok=findViewById(R.id.btn_ok);
        btn_cancle=findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(listener);
        btn_cancle.setOnClickListener(listener);
        tv_text.setText(text);
    }
}
