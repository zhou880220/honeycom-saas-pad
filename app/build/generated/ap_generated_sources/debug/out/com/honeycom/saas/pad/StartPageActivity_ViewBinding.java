// Generated code from Butter Knife. Do not modify!
package com.honeycom.saas.pad;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class StartPageActivity_ViewBinding implements Unbinder {
  private StartPageActivity target;

  @UiThread
  public StartPageActivity_ViewBinding(StartPageActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public StartPageActivity_ViewBinding(StartPageActivity target, View source) {
    this.target = target;

    target.tvSecond = Utils.findRequiredViewAsType(source, R.id.tv_second, "field 'tvSecond'", TextView.class);
    target.layoutSkip = Utils.findRequiredViewAsType(source, R.id.layout_skip, "field 'layoutSkip'", LinearLayout.class);
    target.ivAdvertising = Utils.findRequiredViewAsType(source, R.id.iv_advertising, "field 'ivAdvertising'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    StartPageActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvSecond = null;
    target.layoutSkip = null;
    target.ivAdvertising = null;
  }
}
