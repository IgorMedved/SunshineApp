package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.internal.widget.ActivityChooserView;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;

/**
 * Created by Admin User on 12/12/2015.
 */

public class CustomIconShareProvider  extends ShareActionProvider {

    private final Context mContext;

    public CustomIconShareProvider(Context context)
    {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        ActivityChooserView chooserView =
                (ActivityChooserView) super.onCreateActionView();

        // Set your drawable here
        Drawable icon =
                mContext.getResources().getDrawable(android.R.drawable.ic_menu_share);

        chooserView.setExpandActivityOverflowButtonDrawable(icon);

        return chooserView;
    }
}
