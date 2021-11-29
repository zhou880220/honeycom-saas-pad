// Generated code from Butter Knife. Do not modify!
package com.honeycom.saas.pad.ui.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.honeycom.saas.pad.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReminderActivity_ViewBinding implements Unbinder {
  private ReminderActivity target;

  @UiThread
  public ReminderActivity_ViewBinding(ReminderActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ReminderActivity_ViewBinding(ReminderActivity target, View source) {
    this.target = target;

    target.mWebView = Utils.findRequiredViewAsType(source, R.id.Reminder_web, "field 'mWebView'", WebView.class);
    target.mBackImage = Utils.findRequiredViewAsType(source, R.id.back_image, "field 'mBackImage'", ImageView.class);
    target.mTitleText = Utils.findRequiredViewAsType(source, R.id.title_text, "field 'mTitleText'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ReminderActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mWebView = null;
    target.mBackImage = null;
    target.mTitleText = null;
  }
}
