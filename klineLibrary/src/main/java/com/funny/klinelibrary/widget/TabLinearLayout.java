package com.funny.klinelibrary.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.funny.klinelibrary.R;
import com.funny.klinelibrary.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hfj on 2018/7/10
 */
public class TabLinearLayout extends LinearLayout {

    private Context mContext;
    private String[] titles = new String[]{};
    public List<View> lines = new ArrayList<>();
    public List<TextView> textViews = new ArrayList<>();

    private int textSize = 14;//字体默认大小
    private int lineWidth = 6;//线条默认的厚度
    private int lineColor = 0xff3b90ff;//线条默认颜色
    private int textColor = 0x8a000000;//文字默认颜色
    private boolean isShowDivider = false;//是否显示分割线。
    private int selectedTextColor = 0xff3b90ff;//被选中文字默认颜色

    private OnTabClickListener onTabClickListener;

    private boolean isTitleEllipsis = false;//超过一定字数是否需要省略

    private boolean isNeedWeight = false;//是否需要平均宽度

    private int marginWidth = 20;
    private boolean isSelectedBold = true;//设置是否选中加粗

    private int currentIndex = 0;//当前选中的index

    public TabLinearLayout setSelectedBold(boolean selectedBold) {
        isSelectedBold = selectedBold;
        return this;
    }

    public TabLinearLayout setMarginWidth(int marginWidth) {
        this.marginWidth = marginWidth;
        return this;
    }

    public TabLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public TabLinearLayout setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public boolean isShowDivider() {
        return isShowDivider;
    }

    public TabLinearLayout setShowDivider(boolean showDivider) {
        isShowDivider = showDivider;
        return this;
    }

    public int getTextSize() {
        return textSize;
    }

    public TabLinearLayout setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public TabLinearLayout setEllipsis(boolean ellipsis) {
        this.isTitleEllipsis = ellipsis;
        return this;
    }

    public int getSelectedTextColor() {
        return selectedTextColor;
    }

    public TabLinearLayout setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
        return this;
    }

    public int getLineColor() {
        return lineColor;
    }

    public TabLinearLayout setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public TabLinearLayout setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TabLinearLayout setTitles(String[] titles) {
        this.titles = titles;
        return this;
    }

    public TabLinearLayout setIsNeedWeight(boolean isNeedWeight) {
        this.isNeedWeight = isNeedWeight;
        return this;
    }

    public void build() {
        createView();
    }

    private void createView() {
        switch (getOrientation()) {
            case LinearLayout.VERTICAL:
                removeAllViews();
                for (int i = 0; i < titles.length; i++) {
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
                    addView(linearLayout);
                    if (isShowDivider()) {
                        View view = new View(mContext);
                        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
                        view.setBackgroundColor(getResources().getColor(R.color.line));
                        addView(view);
                    }
                    View line = new View(mContext);
                    line.setLayoutParams(new LayoutParams(getLineWidth(), LayoutParams.MATCH_PARENT));
                    line.setBackgroundColor(getLineColor());
                    linearLayout.addView(line);
                    if (i != 0) line.setVisibility(View.INVISIBLE);
                    lines.add(line);
                    final TextView textView = new TextView(mContext);
                    textView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(titles[i]);
                    textView.setSingleLine();
                    textView.setTextSize(getTextSize());
                    if (i == 0) {
                        textView.setTextColor(getSelectedTextColor());
                    } else {
                        textView.setTextColor(getTextColor());
                    }
                    linearLayout.addView(textView);
                    textViews.add(textView);
                    final int p = i;
                    linearLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int j = 0; j < textViews.size(); j++) {
                                TextView tv = textViews.get(j);
                                if (j == p) {
                                    tv.setTextColor(getSelectedTextColor());
                                } else {
                                    tv.setTextColor(getTextColor());
                                }
                                View l = lines.get(j);
                                if (j == p) {
                                    l.setVisibility(View.VISIBLE);
                                } else {
                                    l.setVisibility(View.INVISIBLE);
                                }
                            }
                            if (onTabClickListener != null) {
                                onTabClickListener.onTabClick(p, titles[p]);
                            }
                        }
                    });
                }
                break;
            case LinearLayout.HORIZONTAL:
                removeAllViews();
                for (int i = 0; i < titles.length; i++) {
                    final LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    if (titles.length <= 4 && isNeedWeight) {
                        linearLayout.setLayoutParams(new LayoutParams(((DisplayUtils.getScreenWidth(getContext()) - marginWidth) / titles.length), LayoutParams.MATCH_PARENT));
                    } else {
                        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                    }
                    addView(linearLayout);
                    if (isShowDivider()) {
                        View view = new View(mContext);
                        LayoutParams layoutParams = new LayoutParams(3, LayoutParams.MATCH_PARENT);
                        layoutParams.setMargins(0, 25, 0, 25);
                        view.setLayoutParams(layoutParams);
                        view.setBackgroundColor(getResources().getColor(R.color.line));
                        addView(view);
                    }
                    final TextView textView = new TextView(mContext);
                    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1.0f);
                    if (!isNeedWeight) {
                        params.setMargins(DisplayUtils.dip2px(getContext(), marginWidth), 0, DisplayUtils.dip2px(getContext(), marginWidth), 0);
                    }
                    textView.setLayoutParams(params);
                    textView.setGravity(Gravity.CENTER);
                    String title = titles[i];
                    if (isTitleEllipsis) {
                        if (title.length() > 4) {
                            title = title.substring(0, 4) + "...";
                        }
                    }
                    textView.setText(title);
                    textView.setSingleLine();
                    textView.setTextSize(getTextSize());
                    if (i == 0) {
                        textView.setTextColor(getSelectedTextColor());
                        if (isSelectedBold) {
                            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        }
                    } else {
                        textView.setTextColor(getTextColor());
                    }
                    linearLayout.addView(textView);
                    textViews.add(textView);

                    final View line = new View(mContext);
                    linearLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            line.setLayoutParams(new LayoutParams(textView.getMeasuredWidth(), DisplayUtils.dip2px(getContext(), 2)));
                            line.setBackgroundColor(getLineColor());
                        }
                    });
                    linearLayout.addView(line);
                    if (i != 0) line.setVisibility(View.INVISIBLE);
                    lines.add(line);
                    final int p = i;
                    linearLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int j = 0; j < textViews.size(); j++) {
                                TextView tv = textViews.get(j);
                                if (j == p) {
                                    tv.setTextColor(getSelectedTextColor());
                                } else {
                                    tv.setTextColor(getTextColor());
                                }
                                if (isSelectedBold) {
                                    tv.setTypeface(Typeface.defaultFromStyle(j == p ? Typeface.BOLD : Typeface.NORMAL));
                                }
                                View l = lines.get(j);
                                if (j == p) {
                                    l.setVisibility(View.VISIBLE);
                                } else {
                                    l.setVisibility(View.INVISIBLE);
                                }
                            }
                            if (onTabClickListener != null) {
                                if (currentIndex != p) {
                                    onTabClickListener.onTabClick(p, titles[p]);
                                    currentIndex = p;
                                }
                            }
                        }
                    });
                }
                currentIndex = 0;
                break;
        }

    }

    public interface OnTabClickListener {
        void onTabClick(int position, String title);
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public void setSelect(int position) {
        currentIndex = position;
        setSelectView(position);
        if (onTabClickListener != null) {
            onTabClickListener.onTabClick(position, titles[position]);
        }
    }

    //用于初始化时选中状态，不调用onTabClick
    public void setSelectView(int position) {
        for (int i = 0; i < textViews.size(); i++) {
            TextView tv = textViews.get(i);
            if (i == position) {
                tv.setTextColor(getSelectedTextColor());
            } else {
                tv.setTextColor(getTextColor());
            }
            View l = lines.get(i);
            if (i == position) {
                l.setVisibility(View.VISIBLE);
            } else {
                l.setVisibility(View.INVISIBLE);
            }
            if (isSelectedBold) {
                tv.setTypeface(Typeface.defaultFromStyle(i == position ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
    }
}
