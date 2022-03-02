package com.akame.videoplayer

import android.app.Activity
import android.view.View
import com.gyf.immersionbar.ImmersionBar

object StatusBarUtil {
    fun init(
        activity: Activity,
        isLight: Boolean,
        isKeyboard: Boolean,
        titleBarView: View?,
        keyboardListener: (Boolean, keyboardHeight: Int) -> Unit
    ) {
        ImmersionBar.with(activity)
            .transparentStatusBar()
            .navigationBarColor(R.color.white)
            .statusBarDarkFont(!isLight)   //状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(!isLight)
            .titleBar(titleBarView)
            .setOnKeyboardListener { isPopup, keyboardHeight ->
                keyboardListener.invoke(isPopup, keyboardHeight)
            }
            .keyboardEnable(isKeyboard)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//            .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            .init()
    }
}