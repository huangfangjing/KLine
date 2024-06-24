package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.widget.LinearLayout
import com.funny.klinelibrary.entity.MinuteData
import com.funny.klinelibrary.utils.DisplayUtils
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.utils.PaintUtils

/**
 * 分时图
 *@author : hfj
 */
class MinuteLineView(
    context: Context,
    private val minuteData: MinuteData,
    private var height: Int = 150
) : View(context) {

    lateinit var mRectF: RectF//绘图区域
    lateinit var mViewRectF: RectF//view全部区域

    var textPadding = 0 //绘制文字间距
    var calePadding = 0 //绘制刻度间距

    init {
        initView()
    }

    private fun initView() {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            DisplayUtils.dip2px(context, height.toFloat())
        )
        textPadding = DisplayUtils.dip2px(context, 5.0f)
        calePadding = DisplayUtils.dip2px(context, 3.0f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mRectF = RectF(
            DisplayUtils.dip2px(context, 10.0f).toFloat(),
            DisplayUtils.dip2px(context, 20.0f).toFloat(),
            (w - DisplayUtils.dip2px(context, 10.0f)).toFloat(),
            (h - DisplayUtils.dip2px(context, 0.0f)).toFloat()
        )
        mViewRectF = RectF(0F, 0F, w.toFloat(), h.toFloat())
    }

    /**
     * 绘制边框
     */
    private fun drawOutLine(canvas: Canvas) {
        canvas.drawRect(
            mViewRectF.left, mViewRectF.top, mViewRectF.right, mViewRectF.bottom,
            PaintUtils.WHITE_PAINT
        )

        canvas.drawRect(
            mRectF.left, mRectF.top, mRectF.right, mRectF.bottom,
            PaintUtils.GRID_DIVIDER
        )

        canvas.drawLine(
            mRectF.left,
            mRectF.centerY(),
            mRectF.right,
            mRectF.centerY(),
            PaintUtils.GRID_DIVIDER
        )

        //绘制价格刻度
        val maxPriceText = NumFormatUtils.formatFloat(minuteData.heighLimit, 2).toString()
        canvas.drawText(
            maxPriceText,
            mRectF.left + calePadding,
            mRectF.top + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )

        val middlePrice =
            NumFormatUtils.formatFloat((minuteData.heighLimit + minuteData.lowLimit) / 2, 2)
                .toString()
        canvas.drawText(
            middlePrice,
            mRectF.left + calePadding,
            mRectF.centerY() - calePadding,
            PaintUtils.TEXT_PAINT
        )

        val minPriceText = NumFormatUtils.formatFloat(minuteData.lowLimit, 2).toString()
        canvas.drawText(
            minPriceText,
            mRectF.left + calePadding,
            mRectF.bottom - textPadding,
            PaintUtils.TEXT_PAINT
        )

        val dataText = "${minuteData.day}   分时图"
        canvas.drawText(
            dataText, mRectF.left,
            mRectF.top - textPadding,
            PaintUtils.TEXT_PAINT
        )
    }

    /**
     * 绘制分时图
     */
    private fun drawMinnutePath(canvas: Canvas) {
        val valuePath = Path()
        val showdowPath = Path()
        val evenWidth = mRectF.width() / 120
        valuePath.moveTo(
            mRectF.left + evenWidth * 0.5f,
            mRectF.top + mRectF.height() * (minuteData.heighLimit - minuteData.prices[0]) /
                    (minuteData.heighLimit - minuteData.lowLimit)
        )
        showdowPath.moveTo(
            mRectF.left + evenWidth * 0.5f,
            mRectF.top + mRectF.height() * (minuteData.heighLimit - minuteData.prices[0]) /
                    (minuteData.heighLimit - minuteData.lowLimit)
        )
        (1..119).forEach { i ->
            valuePath.lineTo(
                mRectF.left + evenWidth * (i + 0.5f),
                mRectF.top + mRectF.height() * (minuteData.heighLimit - minuteData.prices[i]) /
                        (minuteData.heighLimit - minuteData.lowLimit)
            )

            showdowPath.lineTo(
                mRectF.left + evenWidth * (i + 0.5f),
                mRectF.top + mRectF.height() * (minuteData.heighLimit - minuteData.prices[i]) /
                        (minuteData.heighLimit - minuteData.lowLimit)
            )
        }

        showdowPath.lineTo(mRectF.right, mRectF.bottom)
        showdowPath.lineTo(mRectF.left, mRectF.bottom)
        showdowPath.close()
        canvas.drawPath(valuePath, PaintUtils.LINE_BLUE_PAINT)
        canvas.drawPath(showdowPath, PaintUtils.SHADOW_BLUE_PAINT)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOutLine(canvas)
        drawMinnutePath(canvas)
    }


}