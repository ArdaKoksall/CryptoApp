package com.example.project1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog); // Use your custom layout XML here
        setCancelable(false); // Prevent users from canceling the dialog
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
