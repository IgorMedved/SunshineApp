package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.internal.widget.ActivityChooserView;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;

/**
 * Created by Admin User on 12/12/2015.
 */
// The default Share action provider class did not work properly, as the contrast for the share icon in the action bar did not have sufficient contrast
    // onCreatActionView() overrides the default method and sets an icon with proper contrast
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
