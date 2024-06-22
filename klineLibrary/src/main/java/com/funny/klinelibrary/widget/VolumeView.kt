package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.helper.KLineSourceHelper
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.utils.PaintUtils
import kotlin.math.max
import kotlin.math.min

/**
 * 成交量附图
 *@author : hfj
 */
class VolumeView(context: Context?, attrs: AttributeSet?) : BaseChartView(context, attrs) {

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

        val item = getKLineDatas()[if (isLongPressState) mFocusIndex else getKLineDatas().size - 1]
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

    override fun drawCrossLine() {
        //绘制十字线
        if (isLongPressState) {
            mFocusIndex =
                (((mFocusPoint.x - mRectF.left) * KLineSourceHelper.K_D_COLUMNS / mRectF.width()).toInt())
            mFocusIndex = max(0, min(mFocusIndex, KLineSourceHelper.K_D_COLUMNS - 1))

            // 附图实际y轴位置
            val focusY: Float = mFocusPoint.y - y

            if (mViewRectF.contains(mFocusPoint.x, focusY)) {
                mCanvas.drawLine(
                    mRectF.left, focusY, mRectF.right, focusY,
                    PaintUtils.FOCUS_LINE_PAINT
                )
            }
            mCanvas.drawLine(
                mFocusPoint.x, 0f, mFocusPoint.x, mRectF.bottom,
                PaintUtils.FOCUS_LINE_PAINT
            )
            invalidate()
            return
        }
    }

}