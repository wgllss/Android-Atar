package com.atar.fragment;

/**
 *
 */

import android.annotation.SuppressLint;
import android.common.CommonHandler;
import android.fragment.CommonFragment;
import android.interfaces.NetWorkCallListener;
import android.os.Message;
import android.reflection.NetWorkMsg;
import android.skin.SkinUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.atar.activitys.R;
import com.atar.common.CommonLoading;
import com.atar.enums.EnumMsgWhat;
import com.atar.widgets.refresh.DataDispose;
import com.atar.widgets.refresh.OnHandlerDataListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * ****************************************************************************************************************************************************************************
 * 刷新Fragment
 *
 * @author :Atar
 * @createTime:2014-6-3上午11:08:30
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description: 一步解决实现各种控件刷新 同里带有操作数据handler
 * ****************************************************************************************************************************************************************************
 */
@SuppressLint("Recycle")
public class AtarRefreshFragment<T extends PullToRefreshBase<V>, V extends View> extends CommonFragment implements OnHandlerDataListener<T, V>, NetWorkCallListener, OnClickListener {
    private T t;
    private DataDispose<T, V> mDataDispose;
    private boolean isFirstLoad = true;
    private boolean isSwitchViewData;

    private TextView txtToast;

    @Override
    public void onHandlerData(Message msg) {
        CommonLoading.dissMissLoading();
    }

    @Override
    public void NetWorkCall(NetWorkMsg msg) {
        CommonLoading.dissMissLoading();
        onStopRefresh();
    }

    @Override
    public void sendEmptyMessage(int msgWhat) {
        if (mDataDispose != null) {
            Message msg = new Message();
            msg.what = msgWhat;
            mDataDispose.onHandlerData(msg);
        }
    }

    @Override
    public void sendEmptyMessageDelayed(final int msgWhat, long delayMillis) {
        CommonHandler.getInstatnce().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendEmptyMessage(msgWhat);
            }
        }, delayMillis);
    }

    @Override
    public void sendMessage(int msgWhat, Object obj) {
        if (mDataDispose != null) {
            Message msg = new Message();
            msg.what = msgWhat;
            msg.obj = obj;
            mDataDispose.onHandlerData(msg);
        }
    }

    @Override
    public void sendMessage(int msgWhat, int msg1, int msg2) {
        if (mDataDispose != null) {
            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = msg1;
            msg.arg2 = msg2;
            mDataDispose.onHandlerData(msg);
        }
    }

    @Override
    public void sendMessage(int msgWhat, int msg1, int msg2, Object obj) {
        if (mDataDispose != null) {
            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = msg1;
            msg.arg2 = msg2;
            msg.obj = obj;
            mDataDispose.onHandlerData(msg);
        }
    }

    @Override
    public void setRefreshing() {
        if (t != null) {
            t.setRefreshing();
        }
    }

    @Override
    public void onStopRefresh() {
        if (mDataDispose != null) {
            mDataDispose.onStopRefresh();
        }
    }

    @Override
    public DataDispose<T, V> getDataDispose() {
        return mDataDispose;
    }

    @Override
    public boolean isPullDownRefresh() {
        return (t != null ? t.getCurrentMode() : Mode.PULL_FROM_START) == Mode.PULL_FROM_START;
    }

    @Override
    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    @Override
    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }

    @Override
    public T getPullView() {
        return t;
    }

    @Override
    public V getRefreshView() {
        return t != null ? t.getRefreshableView() : null;
    }

    @Override
    public void setRefreshMode(Mode mode) {
        if (t != null) {
            t.setMode(mode);
        }
    }

    @Override
    public boolean isSwitchData() {
        return isSwitchViewData;
    }

    @Override
    public void setIsSwitchViewData(boolean isSwitchViewData) {
        this.isSwitchViewData = isSwitchViewData;
    }

    /**
     * 设置toast提示内容
     *
     * @param strToastContent
     * @author :Atar
     * @createTime:2014-11-14下午4:21:23
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void setToastText(String strToastContent) {
        if (txtToast != null) {
            txtToast.setText(strToastContent);
            txtToast.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏toast
     *
     * @author :Atar
     * @createTime:2014-11-19下午9:01:47
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void setToastTextGone() {
        if (txtToast != null) {
            txtToast.setVisibility(View.GONE);
        }
    }

    /**
     * 设置toast提示
     *
     * @param txtToast
     * @author :Atar
     * @createTime:2015-6-3下午1:42:15
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void setTextView(TextView txtToast) {
        this.txtToast = txtToast;
    }

    /**
     * 设置刷新控件
     *
     * @param t
     * @author :Atar
     * @createTime:2015-4-3上午10:19:26
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    protected void setRefreshView(T t) {
        this.t = t;
        if (mDataDispose == null) {
            mDataDispose = new DataDispose<T, V>(t, this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        initUI(isVisibleToUser);
    }

    protected void initUI(final boolean isVisibleToUser) {
        CommonHandler.getInstatnce().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (t == null || getActivity() == null || !isVisibleToUser) {
                    initUI(isVisibleToUser);
                } else {
                    if (isFirstLoad()) {
                        sendEmptyMessage(EnumMsgWhat.LOAD_FROM_SQL);
                        setIsFirstLoad(false);
                    }
                }
            }
        }, 100);
    }

    @Override
    public void onClick(View v) {
    }

    public void onSwitchUI(int which, int which2, String changeID) {
    }

    public void onRefreshUI(int which, int which2, Object obj) {
    }

    @Override
    public void ChangeSkin(int skinType) {
        super.ChangeSkin(skinType);
        if (getPullView() != null && getActivity() != null) {
            // if (getPullView().getHeaderLayout() != null) {
            // getPullView().getHeaderLayout().setRefreshingLabel(getResources().getString(R.string.refreshing_waiting));
            // getPullView().getHeaderLayout().setPullLabel(getResources().getString(R.string.pull_to_start_refresh));
            // getPullView().getHeaderLayout().setReleaseLabel(getResources().getString(R.string.pull_to_start_reset));
            // ((DynamicLoadingLayout) getPullView().getHeaderLayout()).setRefreshingDrawable(com.handmark.pulltorefresh.library.R.drawable.default_ptr_rotate);
            // getPullView().getHeaderLayout().setRefreshingDrawable(GlobeSettings.refreshImg[skinType]);
            // }
            // if (getPullView().getFooterLayout() != null) {
            // getPullView().getFooterLayout().setRefreshingLabel(getResources().getString(R.string.refreshing_waiting));
            // getPullView().getFooterLayout().setPullLabel(getResources().getString(R.string.pull_to_up_load_more));
            // getPullView().getFooterLayout().setReleaseLabel(getResources().getString(R.string.pull_to_up_reset));
            // ((DynamicLoadingLayout) getPullView().getHeaderLayout()).setRefreshingDrawable(com.handmark.pulltorefresh.library.R.drawable.default_ptr_rotate);
            // getPullView().getFooterLayout().setRefreshingDrawable(GlobeSettings.refreshImg[skinType]);
            // }
            if (getPullView().getHeaderLayout() != null) {
                SkinUtils.setTextColor(getActivity(), R.string.refresh_header_text_color, skinType, getPullView().getHeaderLayout().getHeaderText());
            }
            if (getPullView().getFooterLayout() != null) {
                SkinUtils.setTextColor(getActivity(), R.string.refresh_header_text_color, skinType, getPullView().getFooterLayout().getHeaderText());
            }
            if (getPullView().getHeaderLayout() != null) {
                SkinUtils.setTextColor(getActivity(), R.string.refresh_header_sub_text_color, skinType, getPullView().getHeaderLayout().getSubHeaderText());
            }
            if (getPullView().getFooterLayout() != null) {
                SkinUtils.setTextColor(getActivity(), R.string.refresh_header_sub_text_color, skinType, getPullView().getFooterLayout().getSubHeaderText());
            }
            SkinUtils.setBackgroundColor(getActivity(), R.string.refresh_bg_color, skinType, getPullView().getHeaderLayout());
            SkinUtils.setBackgroundColor(getActivity(), R.string.refresh_bg_color, skinType, getPullView().getFooterLayout());
        }
        SkinUtils.setTextColor(getActivity(), R.string.txt_day_grey_night_greyblack_color, skinType, txtToast);
    }
}
