// Generated code from Butter Knife. Do not modify!
package com.honeycom.saas.pad.ui.activity;

import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.honeycom.saas.pad.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ExecuteActivity_ViewBinding implements Unbinder {
  private ExecuteActivity target;

  @UiThread
  public ExecuteActivity_ViewBinding(ExecuteActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ExecuteActivity_ViewBinding(ExecuteActivity target, View source) {
    this.target = target;

    target.mNewWebProgressbar = Utils.findRequiredViewAsType(source, R.id.NewWebProgressbar, "field 'mNewWebProgressbar'", ProgressBar.class);
    target.mNewWeb = Utils.findRequiredViewAsType(source, R.id.eq_Web, "field 'mNewWeb'", BridgeWebView.class);
    target.mWebError = Utils.findRequiredView(source, R.id.web_error, "field 'mWebError'");
    target.mLoadingPage = Utils.findRequiredView(source, R.id.glide_gif, "field 'mLoadingPage'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ExecuteActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mNewWebProgressbar = null;
    target.mNewWeb = null;
    target.mWebError = null;
    target.mLoadingPage = null;
  }
}
