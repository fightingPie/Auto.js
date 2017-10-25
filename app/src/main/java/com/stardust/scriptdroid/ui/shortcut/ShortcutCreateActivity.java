package com.stardust.scriptdroid.ui.shortcut;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.external.shortcut.Shortcut;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;
import com.stardust.scriptdroid.model.script.ScriptFile;
import com.stardust.scriptdroid.tool.BitmapTool;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.theme.internal.DrawableTool;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/25.
 */

public class ShortcutCreateActivity extends AppCompatActivity {

    public static final String EXTRA_FILE = "file";
    private static final String LOG_TAG = "ShortcutCreateActivity";
    private ScriptFile mScriptFile;
    private boolean mIsDefaultIcon = true;

    @BindView(R.id.name)
    TextView mName;

    @BindView(R.id.select_icon)
    ImageView mIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScriptFile = (ScriptFile) getIntent().getSerializableExtra(EXTRA_FILE);
        showDialog();
    }

    private void showDialog() {
        View view = View.inflate(this, R.layout.shortcut_create_dialog, null);
        ButterKnife.bind(this, view);
        mName.setText(mScriptFile.getSimplifiedName());
        new ThemeColorMaterialDialogBuilder(this)
                .customView(view, false)
                .title(R.string.text_send_shortcut)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> {
                    createShortcut();
                    finish();
                })
                .cancelListener(dialog -> finish())
                .show();
    }


    @OnClick(R.id.select_icon)
    void selectIcon() {
        ShortcutIconSelectActivity_.intent(this)
                .startForResult(21209);
    }


    private void createShortcut() {
        Shortcut shortcut = new Shortcut(this);
        if (mIsDefaultIcon) {
            shortcut.iconRes(R.drawable.ic_node_js_black);
        } else {
            Bitmap bitmap = BitmapTool.drawableToBitmap(mIcon.getDrawable());
            shortcut.icon(bitmap);
        }
        shortcut.name(mName.getText().toString())
                .targetClass(ShortcutActivity.class)
                .extras(new Intent().putExtra(CommonUtils.EXTRA_KEY_PATH, mScriptFile.getPath()))
                .send();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        String packageName = data.getStringExtra(ShortcutIconSelectActivity.EXTRA_PACKAGE_NAME);
        if (packageName != null) {
            try {
                mIcon.setImageDrawable(getPackageManager().getApplicationIcon(packageName));
                mIsDefaultIcon = false;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
        Observable.fromCallable(() -> BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData())))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((bitmap -> {
                    mIcon.setImageBitmap(bitmap);
                    mIsDefaultIcon = false;
                }), error -> {
                    Log.e(LOG_TAG, "decode stream", error);
                });

    }
}
