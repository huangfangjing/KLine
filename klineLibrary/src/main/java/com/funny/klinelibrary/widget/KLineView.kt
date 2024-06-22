package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.utils.DateUtils
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.utils.PaintUtils
import kotlin.math.max
import kotlin.math.min

/**
 * K线附图
 *@author : hfj
 */
class KLineView(context: Context?, attrs: AttributeSet?) : BaseChartView(context, attrs) {

    var mPath5: Path = Path()//5天均线
    var mPath10: Path = Path()//10天均线
    var mPath30: Path = Path()//30天均线
    var mPathHigh: Path = Path()//最高价价格箭头
    var mPathLow: Path = Path()//最低价价格箭头


    override fun drawData() {
        super.drawData()
        if (getKLineDatas().isNotEmpty()) {
            for (i in getKLineDatas().indices) {
                val drawItem: KLineDrawItem = getKLineDatas()[i]
                // 绘制K线和上下引线
                when (drawItem.isNegaline) {
                    true -> {
                        mCanvas.drawRect(drawItem.rect, PaintUtils.KLINE_PAINT_GREEN)
                        mCanvas.drawRect(drawItem.shadowRect, PaintUtils.KLINE_PAINT_GREEN)
                    }

                    else -> {
                        mCanvas.drawRect(drawItem.rect, PaintUtils.KLINE_PAINT_RED)
                        mCanvas.drawRect(drawItem.shadowRect, PaintUtils.KLINE_PAINT_RED)
                    }
                }
                //设置价格均线
                when (i) {
                    0 -> {
                        mPath5.moveTo(drawItem.point5.x, drawItem.point5.y)
                        mPath10.moveTo(drawItem.point10.x, drawItem.point10.y)
                        mPath30.moveTo(drawItem.point30.x, drawItem.point30.y)
                    }

                    else -> {
                        mPath5.lineTo(drawItem.point5.x, drawItem.point5.y)
                        mPath10.lineTo(drawItem.point10.x, drawItem.point10.y)
                        mPath30.lineTo(drawItem.point30.x, drawItem.point30.y)
                    }
                }
                drawArrow(i, drawItem)//绘制最高价最低价箭头
            }

            //绘制价格均线
            mCanvas.drawPath(mPath5, PaintUtils.LINE_BLUE_PAINT)
            mCanvas.drawPath(mPath10, PaintUtils.LINE_PURPLE_PAINT)
            mCanvas.drawPath(mPath30, PaintUtils.LINE_YELLOW_PAINT)

            //绘制均线值
            drawAverageValue(
                if (!isLongPressState) {
                    getLastDrawItem()
                } else {
                    getKLineDatas()[max(0, min(mFocusIndex, getKLineDatas().size - 1))]
                }
            )
            drawExtremeDate()//绘制两边K线日期
            drawCrossLine()//绘制十字线和悬浮框
            invalidate()//刷新
        }
    }

    /**
     * 绘制第一和最后一根K线的日期
     */
    private fun drawExtremeDate() {
        val firstItem = getFirstDrawItem()
        val firstDate = DateUtils.getYMD(firstItem.day)
        mCanvas.drawText(
            firstDate,
            mRectF.left,
            mRectF.bottom + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )

        val lastItem = getLastDrawItem()
        val lastDate = DateUtils.getYMD(lastItem.day)
        val textWidth = PaintUtils.TEXT_PAINT.measureText(lastDate)
        mCanvas.drawText(
            lastDate,
            mRectF.right - textWidth,
            mRectF.bottom + textPadding * 2,
            PaintUtils.TEXT_PAINT
        )
    }

    /**
     * 绘制最大值最小值箭头
     */
    private fun drawArrow(index: Int, drawItem: KLineDrawItem) {
        if (!isDrawHighArrow && drawItem.high == getExtremeValue().maxPrice) {
            isDrawHighArrow = true
            if (index > getKLineDatas().size / 2) {
                //绘制在左边
                mPathLow.moveTo(drawItem.rect.centerX() - 15, drawItem.shadowRect.top - 25)
                mPathLow.lineTo(drawItem.rect.centerX(), drawItem.shadowRect.top - 15)
                mPathLow.lineTo(drawItem.rect.centerX() - 15, drawItem.shadowRect.top - 5)
                mCanvas.drawPath(mPathLow, PaintUtils.ARROW_BLACK_PAINT)
                mCanvas.drawLine(
                    drawItem.rect.centerX() - 45,
                    drawItem.shadowRect.top - 15,
                    drawItem.rect.centerX(),
                    drawItem.shadowRect.top - 15, PaintUtils.ARROW_BLACK_PAINT
                )

                val price = drawItem.high.toString()
                val textWidth = PaintUtils.BLACK_PAINT.measureText(price)
                mCanvas.drawText(
                    price, drawItem.rect.centerX() - 50 - textWidth,
                    drawItem.shadowRect.top - 5, PaintUtils.BLACK_PAINT
                )

            } else {
                //绘制在右边
                mPathLow.moveTo(drawItem.rect.centerX() + 15, drawItem.shadowRect.top - 25)
                mPathLow.lineTo(drawItem.rect.centerX(), drawItem.shadowRect.top - 15)
                mPathLow.lineTo(drawItem.rect.centerX() + 15, drawItem.shadowRect.top - 5)
                mCanvas.drawPath(mPathLow, PaintUtils.ARROW_BLACK_PAINT)
                mCanvas.drawLine(
                    drawItem.rect.centerX(),
                    drawItem.shadowRect.top - 15,
                    drawItem.rect.centerX() + 45,
                    drawItem.shadowRect.top - 15, PaintUtils.ARROW_BLACK_PAINT
                )

                val price = drawItem.high.toString()
                mCanvas.drawText(
                    price, drawItem.rect.centerX() + 50,
                    drawItem.shadowRect.top - 5, PaintUtils.BLACK_PAINT
                )
            }
        }
        if (!isDrawLowArrow && drawItem.low == getExtremeValue().minPrice) {
            isDrawLowArrow = true
            if (index > getKLineDatas().size / 2) {
                //绘制在左边
                mPathLow.moveTo(drawItem.rect.centerX() - 15, drawItem.shadowRect.bottom + 25)
                mPathLow.lineTo(drawItem.rect.centerX(), drawItem.shadowRect.bottom + 15)
                mPathLow.lineTo(drawItem.rect.centerX() - 15, drawItem.shadowRect.bottom + 5)
                mCanvas.drawPath(mPathLow, PaintUtils.ARROW_BLACK_PAINT)
                mCanvas.drawLine(
                    drawItem.rect.centerX() - 45,
                    drawItem.shadowRect.bottom + 15,
                    drawItem.rect.centerX(),
                    drawItem.shadowRect.bottom + 15, PaintUtils.ARROW_BLACK_PAINT
                )

                val price = drawItem.low.toString()
                val textWidth = PaintUtils.BLACK_PAINT.measureText(price)
                mCanvas.drawText(
                    price, drawItem.rect.centerX() - 50 - textWidth,
                    drawItem.shadowRect.bottom + 25, PaintUtils.BLACK_PAINT
                )

            } else {
                //绘制在右边
                mPathLow.moveTo(drawItem.rect.centerX() + 15, drawItem.shadowRect.bottom + 25)
                mPathLow.lineTo(drawItem.rect.centerX(), drawItem.shadowRect.bottom + 15)
                mPathLow.lineTo(drawItem.rect.centerX() + 15, drawItem.shadowRect.bottom + 5)
                mCanvas.drawPath(mPathLow, PaintUtils.ARROW_BLACK_PAINT)
                mCanvas.drawLine(
                    drawItem.rect.centerX(),
                    drawItem.shadowRect.bottom + 15,
                    drawItem.rect.centerX() + 45,
                    drawItem.shadowRect.bottom + 15, PaintUtils.ARROW_BLACK_PAINT
                )

                val price = drawItem.low.toString()
                mCanvas.drawText(
                    price, drawItem.rect.centerX() + 50,
                    drawItem.shadowRect.bottom + 25, PaintUtils.BLACK_PAINT
                )
            }
        }
    }


    override fun drawOutLine(mExtremeValue: ExtremeValue) {
        val contentRect = mRectF
        val path = Path()
        path.moveTo(contentRect.left, contentRect.top)
        path.lineTo(contentRect.right, contentRect.top)
        path.lineTo(contentRect.right, contentRect.bottom)
        path.lineTo(contentRect.left, contentRect.bottom)
        path.close()
        mCanvas.drawPath(path, PaintUtils.GRID_DIVIDER)

        // 绘制价格刻度和价格分隔线
        val maxPrice: Float = NumFormatUtils.formatFloat(mExtremeValue.mKMaxPrice, 2)
        val minPrice: Float = NumFormatUtils.formatFloat(mExtremeValue.mKMinPrice, 2)
        val rect = Rect()
        PaintUtils.TEXT_PAINT.getTextBounds(
            maxPrice.toString(),
            0,
            maxPrice.toString().length,
            rect
        )
        mCanvas.drawText(
            maxPrice.toString(),
            contentRect.left + calePadding,
            contentRect.top + rect.height() + calePadding,
            PaintUtils.TEXT_PAINT
        )
        val perHeight = contentRect.height() / 4
        val perPrice: Float = NumFormatUtils.formatFloat((maxPrice - minPrice) / 4, 2)

        for (i in 1..3) {
            mCanvas.drawLine(
                contentRect.left, contentRect.top + perHeight * i, contentRect.right,
                contentRect.top + perHeight * i, PaintUtils.GRID_INNER_DIVIDER
            )
            val value: Float = NumFormatUtils.formatFloat(maxPrice - perPrice * i, 2)
            mCanvas.drawText(
                value.toString(), contentRect.left + calePadding,
                contentRect.top + perHeight * i - calePadding, PaintUtils.TEXT_PAINT
            )
        }
        mCanvas.drawText(
            minPrice.toString() + "",
            contentRect.left + calePadding,
            contentRect.bottom - calePadding,
            PaintUtils.TEXT_PAINT
        )
    }

    /**
     * 绘制均线值
     */
    private fun drawAverageValue(targetItem: KLineDrawItem) {
        val average5 = NumFormatUtils.formatFloat(targetItem.average5, 2)
        val text5 = "M5 :$average5"
        val rectMid = Rect()
        //这里长度写死，避免位置抖动
        PaintUtils.TEXT_YELLOW_PAINT.getTextBounds(
            "M5 :12.12",
            0,
            "M5 :12.12".length,
            rectMid
        )
        mCanvas.drawText(
            text5, mRectF.left, mRectF.top - textPadding,
            PaintUtils.TEXT_BLUE_PAINT
        )
        val average30 = NumFormatUtils.formatFloat(targetItem.average10, 2)
        val text30 = "M10：$average30"
        val rectUpper = Rect()
        PaintUtils.TEXT_BLUE_PAINT.getTextBounds("M10 :12.12", 0, "M10 :12.12".length, rectUpper)
        mCanvas.drawText(
            text30,
            mRectF.left + rectMid.width() + textPadding * 2,
            mRectF.top - textPadding, PaintUtils.TEXT_PURPLE_PAINT
        )

        val average120 = NumFormatUtils.formatFloat(targetItem.average30, 2)
        val text120 = "M30：$average120"
        mCanvas.drawText(
            text120,
            mRectF.left + rectMid.width() + rectUpper.width() + textPadding * 5,
            mRectF.top - textPadding, PaintUtils.TEXT_YELLOW_PAINT
        )
    }
}