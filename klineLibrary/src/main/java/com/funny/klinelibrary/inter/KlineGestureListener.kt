package com.funny.klinelibrary.inter

import android.view.MotionEvent
import com.funny.klinelibrary.entity.KLineDrawItem


/**
 * 根据自己需要添加
 * @author : hfj
 */
interface KlineGestureListener {

    /**
     * 当在图标执行move，drag的时候调用
     *
     * @param dX x轴移动距离
     */
    fun onChartTranslate(me: MotionEvent?, dX: Float)

    /**
     * 当在图表上执行缩放的时候调用
     *
     * @param scaleX x轴缩放系数
     * @param scaleY y轴缩放系数
     */
    fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float)

    /**
     * 当在图表上选中的时候调用
     * @param drawItem 选中的位置
     */
    fun onFocusData(drawItem: KLineDrawItem)
}