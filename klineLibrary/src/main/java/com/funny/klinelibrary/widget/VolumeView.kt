package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.utils.DisplayUtils
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.utils.PaintUtils

/**
 * 成交量附图
 *@author : hfj
 */
class VolumeView(context: Context?, attrs: AttributeSet?) : BaseChartView(context, attrs) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectF = RectF(
            DisplayUtils.dip2px(context, 10.0f).toFloat(),
            DisplayUtils.dip2px(context, 15.0f).toFloat(),
            (w - DisplayUtils.dip2px(context, 10.0f)).toFloat(),
            (h - DisplayUtils.dip2px(context, 0f)).toFloat()
        )
    }

    override fun drawOutLine(mExtremeValue: ExtremeValue) {
        val path = Path()
        path.moveTo(mRectF.left, mRectF.top)
        path.lineTo(mRectF.right, mRectF.top)
        path.lineTo(mRectF.right, mRectF.bottom)
        path.lineTo(mRectF.left, mRectF.bottom)
        path.close()
        mCanvas.drawPath(path, PaintUtils.GRID_DIVIDER)
        mCanvas.drawLine(
            mRectF.left,
            mRectF.centerY(),
            mRectF.right,
            mRectF.centerY(),
            PaintUtils.GRID_INNER_DIVIDER
        )

        // 绘制附图最大刻度
        val maxVolume = "最大量:${NumFormatUtils.formatBigFloatAll(mExtremeValue.maxVolume, 2)}"
        val rect = Rect()
        PaintUtils.TEXT_PAINT.getTextBounds(maxVolume, 0, maxVolume.length, rect)

        mCanvas.drawText(
            maxVolume, mRectF.left,
            mRectF.top - textPadding,
            PaintUtils.TEXT_PAINT
        )

        val item =
            getKLineDatas()[if (KLineViewGroup.CHART_STATE == KLineViewGroup.STATE_DEFAULT) getFocusIndex() else getKLineDatas().size - 1]
        val volumeDes = "当前量:" + NumFormatUtils.formatBigFloatAll(item.volume.toFloat(), 2)

        mCanvas.drawText(
            volumeDes, mRectF.left + textPadding * 2 + rect.width(), mRectF.top - textPadding,
            PaintUtils.TEXT_PAINT
        )

    }

    override fun drawData() {
        super.drawData()
        getKLineDatas().forEach { element ->
            mCanvas.drawRect(
                element.volumeRect, if (element.isNegaline) {
                    PaintUtils.KLINE_PAINT_GREEN
                } else {
                    PaintUtils.KLINE_PAINT_RED
                }
            )
        }

        drawCrossLine()
        invalidate()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!mBitmap.isRecycled) {
            mBitmap.recycle()
        }
    }
}