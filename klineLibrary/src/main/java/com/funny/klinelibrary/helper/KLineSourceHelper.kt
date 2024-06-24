package com.funny.klinelibrary.helper

import android.graphics.PointF
import android.graphics.RectF
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.entity.KLineItem
import com.funny.klinelibrary.inter.IChartDataCountListener
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.widget.KLineView
import com.funny.klinelibrary.widget.VolumeView
import kotlin.math.abs
import kotlin.math.max

class KLineSourceHelper(private var mReadyListener: IChartDataCountListener<MutableList<KLineDrawItem>>) {


    companion object {
        /**
         * 一屏默认展示的蜡烛线数量
         */
        const val COLUMNS_DEFAULT: Int = 50

        /**
         * 当前展示的蜡烛线数量
         */
        var K_D_COLUMNS: Int = COLUMNS_DEFAULT

        /**
         * 一屏最多展示的蜡烛线数量
         */
        const val MAX_COLUMNS: Int = 160

        /**
         * 一屏最少展示的蜡烛线数量
         */
        const val MIN_COLUMNS: Int = 20

        /**
         * 给极值增加一定放大倍数比例，防止绘制到最高最低点
         */
        const val EXTREME_SCALE: Float = 0.04f


    }


    /**
     * 行情图当前屏开始的位置
     */
    private var startIndex: Int = 0

    /**
     * 行情图当前屏结束位置
     */
    private var endIndex: Int = 0

    /**
     * k线的绘制数据
     */
    private lateinit var kLineItems: MutableList<KLineDrawItem>

    private lateinit var mKList: List<KLineItem>

    private lateinit var mKLineView: KLineView

    private lateinit var mVolumeView: VolumeView


    enum class SourceType {
        INIT,
        MOVE,
        SCALE
    }

    /**
     * 初始化行情图初始数据
     */
    fun initKDrawData(
        klineList: List<KLineItem>,
        kLineChartView: KLineView,
        volumeView: VolumeView
    ) {
        this.mKList = klineList
        this.mKLineView = kLineChartView
        this.mVolumeView = volumeView

        // K线首次当前屏初始位置
        startIndex = max(0, klineList.size - K_D_COLUMNS)
        // k线首次当前屏结束位置
        endIndex = klineList.size - 1
        // 计算技术指标
        initKLineDrawData(0f, SourceType.INIT)
    }


    /**
     * 根据移动偏移量计算行情图当前屏数据
     *
     * @param distance 手指横向移动距离
     */
    fun initKLineDrawData(distance: Float, sourceType: SourceType) {
        // 重置默认值

        resetDefaultValue()

        val contentRect = mKLineView.mRectF

        // 计算当前屏幕开始和结束的位置
        countStartEndPos(distance, sourceType)

        // 计算蜡烛线价格最大最小值，成交量最大值
        val extremeValue = countMaxMinValue()

        // 最大值最小值差值
        val diffPrice = extremeValue.mKMaxPrice - extremeValue.mKMinPrice

        // 计算当前屏幕每一个蜡烛线的位置和涨跌情况
        var i = startIndex
        var k = 0
        while (i < endIndex) {
            val kLineItem = mKList[i]
            // 开盘价
            val open = kLineItem.open
            // 最低价
            val close = kLineItem.close
            // 最高价
            val high = kLineItem.high
            // 最低价
            val low = kLineItem.low

            val drawItem = KLineDrawItem()
            drawItem.open = open
            drawItem.close = close
            drawItem.high = high
            drawItem.low = low
            drawItem.volume = kLineItem.volume
            drawItem.day = kLineItem.day

            // 计算蜡烛线
            val openYRate = (extremeValue.mKMaxPrice - open) / diffPrice
            val closeYRate = (extremeValue.mKMaxPrice - close) / diffPrice
            drawItem.rect = getRect(contentRect, k, openYRate, closeYRate)
            drawItem.volumeRect =
                getVolumeRect(mVolumeView.mRectF, k, drawItem.volume, extremeValue.mKMaxVolume)

            //计算近5,30,120天平均价格
            drawItem.average5 = getAveragePrice(i, 5)
            drawItem.average10 = getAveragePrice(i, 10)
            drawItem.average30 = getAveragePrice(i, 30)

            drawItem.point5 = getPointF(k, drawItem.average5, extremeValue.mKMaxPrice, diffPrice)
            drawItem.point10 = getPointF(k, drawItem.average10, extremeValue.mKMaxPrice, diffPrice)
            drawItem.point30 = getPointF(k, drawItem.average30, extremeValue.mKMaxPrice, diffPrice)

            // 计算上影线，下影线
            val highYRate = (extremeValue.mKMaxPrice - high) / diffPrice
            val lowYRate = (extremeValue.mKMaxPrice - low) / diffPrice
            val shadowRect = getLine(contentRect, k, highYRate, lowYRate)
            drawItem.shadowRect = shadowRect

            // 计算涨跌和阳阴线
            if (i - 1 >= 0) {
                val preItem = mKList[i - 1]
                drawItem.preClose = preItem.close
                drawItem.riseMaxPrice = NumFormatUtils.formatFloat(drawItem.preClose * 1.1f, 2)
                drawItem.fallMaxPrice = NumFormatUtils.formatFloat(drawItem.preClose * 0.9f, 2)
                drawItem.isFall = (drawItem.close < drawItem.preClose) //是否是下跌
                val rate = NumFormatUtils.formatFloat(
                    abs((drawItem.close - drawItem.preClose).toDouble()) * 100 / drawItem.preClose,
                    2
                )
                    .toFloat()
                drawItem.increaseRate = (if (drawItem.isFall) "-" else "+") + rate + "%"
                drawItem.increaseExtra =
                    NumFormatUtils.formatFloat(drawItem.close - drawItem.preClose, 2).toString()
            }
            drawItem.isNegaline =
                (drawItem.open > drawItem.close) || (drawItem.close == drawItem.fallMaxPrice) //是否阴线
            drawItem.marketCap =
                NumFormatUtils.formatBigFloatAll(drawItem.shareCapitaltotal * drawItem.close, 2)

            val v = NumFormatUtils.formatFloat(
                drawItem.volume * 100.00 / (drawItem.shareCapitaltotal * drawItem.close),
                2
            )
            drawItem.turnoverRate = String.format("%s", v.toFloat()) + "%"
            drawItem.volumeExtra = NumFormatUtils.formatBigFloatAll(drawItem.volume.toFloat(), 2)
            kLineItems.add(drawItem)
            i++
            k++
        }

        val resultList: MutableList<KLineDrawItem> = ArrayList()
        // 数据准备完毕
        resultList.addAll(kLineItems)
        mReadyListener.onReady(resultList, extremeValue)
    }

    private fun getVolumeRect(parent: RectF, k: Int, volume: Long, maxVolume: Float): RectF {
        val rect = RectF()
        rect.left = parent.left + (parent.width() * (k + 0.125) / K_D_COLUMNS).toInt()
        rect.top = parent.top + (maxVolume - volume) * parent.height() / maxVolume
        rect.right = parent.left + (parent.width() * (k + 0.875) / K_D_COLUMNS).toInt()
        rect.bottom = parent.bottom
        return rect
    }


    /**
     * @param k
     * @param price
     * @param diffPrice 计算价格某天的价格均线Point位置
     * @return
     */
    private fun getPointF(k: Int, price: Float, maxPrice: Float, diffPrice: Float): PointF {
        val x =
            (mKLineView.mRectF.width() * (k + 0.5) / K_D_COLUMNS).toFloat() + mKLineView.mRectF.left
        val y = (maxPrice - price) * mKLineView.mRectF.height() / diffPrice + mKLineView.mRectF.top
        return PointF(x, y)
    }


    /**
     * 计算价格均线
     *
     * @param index
     * @param day
     * @return
     */
    private fun getAveragePrice(index: Int, day: Int): Float {
        var priceSum = 0f
        if (index < day - 1) {
            for (i in 0..index) {
                priceSum += mKList[i].close
            }
        } else {
            for (i in index - day + 1..index) {
                priceSum += mKList[i].close
            }
        }
        return priceSum / day
    }


    /**
     * 重置默认的最大值、最小值
     */
    private fun resetDefaultValue() {
        kLineItems = mutableListOf()
        mKLineView.mPath5.reset()
        mKLineView.mPath10.reset()
        mKLineView.mPath30.reset()
        mKLineView.mPathHigh.reset()
        mKLineView.mPathLow.reset()
    }

    /**
     * 计算当前屏幕开始和结束的位置
     */
    private fun countStartEndPos(distance: Float, sourceType: SourceType) {
        if (sourceType == SourceType.SCALE) {
            startIndex = endIndex - K_D_COLUMNS
        } else {
            // 根据偏移距离计算偏移几天
            val offCount =
                ((distance * K_D_COLUMNS) / mKLineView.mRectF.width()).toInt()
            // 计算移动后的开始和结束位置
            startIndex -= offCount
            endIndex = startIndex + K_D_COLUMNS
        }
        if (endIndex > mKList.size) {
            startIndex = mKList.size - K_D_COLUMNS
            endIndex = startIndex + K_D_COLUMNS
        }
        if (startIndex < 0) {
            startIndex = 0
            endIndex = K_D_COLUMNS
        }
    }

    /**
     * 计算蜡烛线价格最大最小值，成交量最大值
     */
    private fun countMaxMinValue(): ExtremeValue {
        var maxPrice = Int.MIN_VALUE.toFloat()
        var minPrice = Int.MAX_VALUE.toFloat()
        var maxVolume = Int.MIN_VALUE.toFloat()

        for (i in startIndex until endIndex) {
            val kLineItem = mKList[i]
            if (kLineItem.high > maxPrice) {
                maxPrice = kLineItem.high
            }
            if (kLineItem.low < minPrice) {
                minPrice = kLineItem.low
            }
            if (kLineItem.volume > maxVolume) {
                maxVolume = kLineItem.volume.toFloat()
            }
        }

        val extremeValue = ExtremeValue()
        extremeValue.mKMaxPrice = maxPrice * (1 + EXTREME_SCALE)
        extremeValue.mKMinPrice = minPrice * (1 - EXTREME_SCALE)
        extremeValue.mKMaxVolume = maxVolume * (1 + EXTREME_SCALE)
        extremeValue.maxPrice = maxPrice
        extremeValue.minPrice = minPrice
        extremeValue.maxVolume = maxVolume
        return extremeValue
    }

    /**
     * 蜡烛图的区域，间隙0.125，蜡烛图宽度占0.75.
     */
    private fun getRect(
        parent: RectF, col: Int, scaleTop: Float,
        scaleBottom: Float
    ): RectF {
        val rect = RectF()
        rect.left = parent.left + (parent.width() * (col + 0.125) / K_D_COLUMNS).toInt()
        rect.right =
            parent.left + (parent.width() * (col + 0.875) / K_D_COLUMNS).toInt()
        if (rect.right == rect.left) {
            rect.right += 1f
        }
        rect.top = parent.top + (parent.height() * scaleTop).toInt()
        rect.bottom = parent.top + (parent.height() * scaleBottom).toInt()
        if (rect.top == rect.bottom) {
            rect.bottom += 3f //加粗一字板形态
        }
        return rect
    }

    /**
     * 蜡烛线上影线，和下影线的宽度和高度
     */
    private fun getLine(
        parent: RectF, col: Int, scaleTop: Float,
        scaleBottom: Float
    ): RectF {
        val rect = RectF()
        rect.left = (parent.left + (parent.width() * (col + 0.5)
                / K_D_COLUMNS).toInt() - 1.5).toInt().toFloat()
        rect.right = (parent.left + (parent.width() * (col + 0.5)
                / K_D_COLUMNS).toInt() + 1.5).toInt().toFloat()

        rect.top = parent.top + (parent.height() * scaleTop).toInt()
        rect.bottom = parent.top + (parent.height() * scaleBottom).toInt()
        return rect
    }

}