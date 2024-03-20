package com.capacitor.inappbrowser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONException;

@CapacitorPlugin(name = "InAppBrowser")
public class InAppBrowserPlugin extends Plugin {

    private InAppBrowser implementation = new InAppBrowser();
    private BridgeWebView webView;

    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            } else {
                closeBrowser();
            }
        }
    };

    private void closeBrowser() {
        final ViewGroup root = (ViewGroup) ((ViewGroup) getActivity()
                .findViewById(android.R.id.content)).getChildAt(0);
        try {
            ViewGroup browserLayout = (ViewGroup) root.findViewById(R.id.browser_layout);
            PlayAnim(browserLayout, getActivity(), R.anim.slide_right_out, 0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onBackPressedCallback.setEnabled(false);
        webView = null;
    }

    @Override
    public void load() {
        super.load();
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), onBackPressedCallback);
    }

    @PluginMethod
    public void open(PluginCall call) {
        String url = call.getString("url");
        String title = call.getString("title", null);

        getActivity().runOnUiThread(() -> {
            final ViewGroup root = (ViewGroup) ((ViewGroup) getActivity()
                    .findViewById(android.R.id.content)).getChildAt(0);
            ConstraintLayout browserLayout = (ConstraintLayout) LayoutInflater.from(getActivity()).inflate(R.layout.browser_layout, root, false);
            CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            browserLayout.setLayoutParams(layoutParams);
            root.addView(browserLayout);
            PlayAnim(browserLayout, getActivity(), R.anim.slide_right_in, 0, false);

            AppBarLayout appBarLayout = browserLayout.findViewById(R.id.appbar);

            Toolbar toolbar = browserLayout.findViewById(R.id.toolbar);
            if (!TextUtils.isEmpty(title)) {
                toolbar.setTitle(title);
            }
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    closeBrowser();
                }
            });

            toolbar.inflateMenu(R.menu.toolbar_menu);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.close) {
                    closeBrowser();
                }
                return true;
            });

            webView = browserLayout.findViewById(R.id.inapp_webview);
            webView.registerHandler("postMessage", (data, function) -> {
                Log.i("InAppBrowserPlugin", "handler = postMessage, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data from Java");
                try {
                    notifyListeners("message", new JSObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            webView.registerHandler("close", (data, function) -> {
                closeBrowser();
                function.onCallBack("close success");
            });

            webView.loadUrl(url);
        });
        onBackPressedCallback.setEnabled(true);


        JSObject ret = new JSObject();
        ret.put("url", implementation.echo(url));
        call.resolve(ret);
    }

    @PluginMethod
    public void close(PluginCall call) {
        closeBrowser();
        call.resolve();
    }

    @PluginMethod
    public void postMessage(PluginCall call) {
        if (webView == null) {
            call.reject("No webview found");
            return;
        }
        getActivity().runOnUiThread(() -> webView.callHandler("message", call.getData().toString(), data -> {
            if (TextUtils.isEmpty(data)) {
                call.resolve();
            } else {
                try {
                    call.resolve(new JSObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }));
    }

    public Animation PlayAnim(View v, Context Con, int animationid, int StartOffset, boolean removeOnEnd) {

        if (v != null) {
            Animation animation = AnimationUtils.loadAnimation(Con, animationid);
            animation.setStartOffset(StartOffset);
            v.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (removeOnEnd) {
                        ((ViewGroup) v.getParent()).removeView(v);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return animation;
        }
        return null;
    }
}
