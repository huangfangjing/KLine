package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.Log
import android.widget.LinearLayout
import com.funny.klinelibrary.R
import com.funny.klinelibrary.entity.MinuteData
import com.funny.klinelibrary.utils.DisplayUtils
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.utils.PaintUtils
import kotlin.random.Random

/**
 * 分时图
 *@author : hfj
 */
class MinuteLineView(
    context: Context,
    private val minuteData: MinuteData,
) : BaseChartView(context) {

    private var neverDraw = true

    private val valuePath = Path()
    private val showdowPath = Path()
    private val averagePath = Path()

    private lateinit var mValueRectF: RectF
    private lateinit var mVolumeRectF: RectF


    override fun initView() {
        super.initView()
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            DisplayUtils.dip2px(context, 180f)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectF = RectF(
            DisplayUtils.dip2px(context, 10.0f).toFloat(),
            DisplayUtils.dip2px(context, 20.0f).toFloat(),
            (w - DisplayUtils.dip2px(context, 10.0f)).toFloat(),
            (h - DisplayUtils.dip2px(context, 0f)).toFloat()
        )
        mValueRectF = RectF(
            mRectF.left,
            mRectF.top,
            mRectF.right,
            mRectF.top + DisplayUtils.dip2px(context, 85f)
        )
        mVolumeRectF = RectF(
            mRectF.left,
            mRectF.top * 2 + mValueRectF.height(),
            mRectF.right,
            mRectF.bottom
        )
    }

    /**
     * 绘制边框
     */
    private fun drawOutLine() {

        mCanvas.drawRect(
            mValueRectF.left, mValueRectF.top, mValueRectF.right, mValueRectF.bottom,
            PaintUtils.GRID_DIVIDER
        )

        mCanvas.drawLine(
            mValueRectF.left,
            mValueRectF.centerY(),
            mValueRectF.right,
            mValueRectF.centerY(),
            PaintUtils.GRID_DIVIDER
        )

        mCanvas.drawLine(
            mValueRectF.centerX(),
            mValueRectF.top,
            mValueRectF.centerX(),
            mValueRectF.bottom,
            PaintUtils.GRID_DIVIDER
        )

        //绘制价格刻度
        val maxPriceText = NumFormatUtils.formatFloat(minuteData.heighLimit, 2).toString()
        mCanvas.drawText(
            maxPriceText,
            mValueRectF.left + calePadding,
            mValueRectF.top + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )

        val middlePrice =
            NumFormatUtils.formatFloat((minuteData.heighLimit + minuteData.lowLimit) / 2, 2)
                .toString()
        mCanvas.drawText(
            middlePrice,
            mValueRectF.left + calePadding,
            mValueRectF.centerY() - calePadding,
            PaintUtils.TEXT_PAINT
        )

        val minPriceText = NumFormatUtils.formatFloat(minuteData.lowLimit, 2).toString()
        mCanvas.drawText(
            minPriceText,
            mValueRectF.left + calePadding,
            mValueRectF.bottom - textPadding,
            PaintUtils.TEXT_PAINT
        )

        val dataText = "${minuteData.day}   分时图"
        mCanvas.drawText(
            dataText,
            mValueRectF.left,
            mValueRectF.top - textPadding,
            PaintUtils.TEXT_PAINT
        )

        val averageText = "均价:${minuteData.averagePrice.last()}"
        mCanvas.drawText(
            averageText,
            mValueRectF.left + PaintUtils.TEXT_PAINT.measureText(dataText) + textPadding * 2,
            mValueRectF.top - textPadding,
            PaintUtils.TEXT_YELLOW_PAINT
        )

        val closeText = "收盘:${minuteData.closePrice}"
        mCanvas.drawText(
            closeText,
            mValueRectF.left + PaintUtils.TEXT_PAINT.measureText(dataText) +
                    textPadding * 4 + PaintUtils.TEXT_PAINT.measureText(averageText),
            mValueRectF.top - textPadding,
            if (minuteData.isFall) PaintUtils.TEXT_GREEN_PAINT_8 else PaintUtils.TEXT_RED_PAINT_8
        )


        val measureText = PaintUtils.TEXT_PAINT.measureText("09:30")
        mCanvas.drawText(
            "09:30", mValueRectF.left,
            mValueRectF.bottom + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )
        mCanvas.drawText(
            "11:30", mValueRectF.centerX() - measureText / 2,
            mValueRectF.bottom + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )
        mCanvas.drawText(
            "15:00", mValueRectF.right - measureText,
            mValueRectF.bottom + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )

        mCanvas.drawRect(
            mVolumeRectF.left, mVolumeRectF.top, mVolumeRectF.right, mVolumeRectF.bottom,
            PaintUtils.GRID_DIVIDER
        )

        val evenWidth = mRectF.width() / minuteData.volumes.size
        minuteData.volumes.forEachIndexed { i, _ ->

            mCanvas.drawRect(
                mRectF.left + (0.125f + i) * evenWidth,
                mVolumeRectF.top + (1 - minuteData.volumes[i] / 100f) * mVolumeRectF.height(),//分时量最大假设是10
                mRectF.left + (0.875f + i) * evenWidth,
                mVolumeRectF.bottom,
                if (Random.nextBoolean()) PaintUtils.KLINE_PAINT_RED else PaintUtils.KLINE_PAINT_GREEN
            )
        }
    }

    /**
     * 绘制分时图
     */
    private fun drawMinnutePath() {

        val evenWidth = mValueRectF.width() / 120
        valuePath.moveTo(
            mValueRectF.left + evenWidth * 0.5f,
            mValueRectF.top + mValueRectF.height() * (minuteData.heighLimit - minuteData.prices[0]) /
                    (minuteData.heighLimit - minuteData.lowLimit)
        )

        showdowPath.moveTo(
            mValueRectF.left + evenWidth * 0.5f,
            mValueRectF.top + mValueRectF.height() * (minuteData.heighLimit - minuteData.prices[0]) /
                    (minuteData.heighLimit - minuteData.lowLimit)
        )

        averagePath.moveTo(
            mValueRectF.left + evenWidth * 0.5f,
            (mValueRectF.top + mValueRectF.height() * (minuteData.heighLimit - minuteData.averagePrice[0]) /
                    (minuteData.heighLimit - minuteData.lowLimit)).toFloat()
        )



        (1..119).forEach { i ->
            valuePath.lineTo(
                mValueRectF.left + evenWidth * (i + 0.5f),
                mValueRectF.top + mValueRectF.height() * (minuteData.heighLimit - minuteData.prices[i]) /
                        (minuteData.heighLimit - minuteData.lowLimit)
            )

            showdowPath.lineTo(
                mValueRectF.left + evenWidth * (i + 0.5f),
                mValueRectF.top + mValueRectF.height() * (minuteData.heighLimit - minuteData.prices[i]) /
                        (minuteData.heighLimit - minuteData.lowLimit)
            )

            averagePath.lineTo(
                mValueRectF.left + evenWidth * (i + 0.5f),
                (mValueRectF.top + mValueRectF.height() * (minuteData.heighLimit - minuteData.averagePrice[i]) /
                        (minuteData.heighLimit - minuteData.lowLimit))
            )
        }

        showdowPath.lineTo(mValueRectF.right, mValueRectF.bottom)
        showdowPath.lineTo(mValueRectF.left, mValueRectF.bottom)
        showdowPath.close()
        mCanvas.drawPath(valuePath, PaintUtils.LINE_BLUE_PAINT)
        mCanvas.drawPath(showdowPath, PaintUtils.SHADOW_BLUE_PAINT)
        if (!minuteData.isOneLine) {
            mCanvas.drawPath(averagePath, PaintUtils.LINE_YELLOW_PAINT)
        }
    }

    /**
     * 绘制关闭分时图按钮
     */
    private fun drawCloseRect() {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.close)
        mCanvas.drawBitmap(bitmap, mRectF.right - bitmap.width, 0f, null)
    }

    override fun drawData() {
        super.drawData()
        drawOutLine()//边框
        drawMinnutePath()//分时图
        drawCloseRect()//关闭按钮
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (neverDraw) {
            drawData()
            neverDraw = false
        }
    }
}