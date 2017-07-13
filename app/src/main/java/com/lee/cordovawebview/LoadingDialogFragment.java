package com.lee.cordovawebview;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Auther: Lee Sie
 * CopyRight: CL
 *
 * @Description: TODO
 */

public class LoadingDialogFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = "LoadingFm";
    private ImageView ivLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_loading, container, false);

        if (rootView != null) {
            ivLoading = (ImageView) rootView.findViewById(R.id.loading_iv);
            ivLoading.setBackgroundResource(R.drawable.anim_drawable);
        }
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            AnimationDrawable loadingAnimation = (AnimationDrawable) ivLoading.getBackground();
            loadingAnimation.start();
        }
    }

    public void showLoadingDialog(FragmentManager manager) {
        LoadingDialogFragment fragment = (LoadingDialogFragment) manager.findFragmentByTag(LoadingDialogFragment.FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new LoadingDialogFragment();
            fragment.setCancelable(false);
            manager.beginTransaction()
                    .add(fragment, LoadingDialogFragment.FRAGMENT_TAG)
                    .commitAllowingStateLoss();

            // fragment.show(getSupportFragmentManager().beginTransaction(), LoadingDialogFragment.FRAGMENT_TAG);
        }
    }

    public void hideLoadingDialog(FragmentManager manager) {
        LoadingDialogFragment fragment = (LoadingDialogFragment) manager.findFragmentByTag(LoadingDialogFragment.FRAGMENT_TAG);
        if (fragment != null) {
            // fragment.dismissAllowingStateLoss();
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }
}