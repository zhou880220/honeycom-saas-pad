// Generated code from Butter Knife. Do not modify!
package com.honeycom.saas.pad.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.honeycom.saas.pad.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.mNewWebProgressbar = Utils.findRequiredViewAsType(source, R.id.NewWebProgressbar, "field 'mNewWebProgressbar'", ProgressBar.class);
    target.mNewWeb = Utils.findRequiredViewAsType(source, R.id.new_Web, "field 'mNewWeb'", BridgeWebView.class);
    target.mWebError = Utils.findRequiredView(source, R.id.web_error, "field 'mWebError'");
    target.mCloseLoginPage = Utils.findRequiredViewAsType(source, R.id.closeLoginPage, "field 'mCloseLoginPage'", ImageView.class);
    target.mTextPolicyReminder = Utils.findRequiredViewAsType(source, R.id.text_policy_reminder, "field 'mTextPolicyReminder'", TextView.class);
    target.mTextPolicyReminderBack = Utils.findRequiredViewAsType(source, R.id.text_policy_reminder_back, "field 'mTextPolicyReminderBack'", RelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mNewWebProgressbar = null;
    target.mNewWeb = null;
    target.mWebError = null;
    target.mCloseLoginPage = null;
    target.mTextPolicyReminder = null;
    target.mTextPolicyReminderBack = null;
  }
}
