package com.funny.klinelibrary.entity

import android.graphics.PointF
import android.graphics.RectF
import java.io.Serializable

class KLineDrawItem : Serializable {

    /**
     * 蜡烛线
     */
    lateinit var rect: RectF

    /**
     * 上影线、下影线
     */
    lateinit var shadowRect: RectF

    /**
     * 当天涨跌： true:跌   false：涨
     */
    var isFall: Boolean = false

    /**
     * 当天是否是阴线
     */
    var isNegaline: Boolean = false

    /**
     * 当天交易日日期
     */
    lateinit var day: String

    /**
     * 成交量
     */
    lateinit var volumeRect: RectF

    /**
     * 开盘价
     */
    var open: Float = 0f

    /**
     * 最高价
     */
    var high: Float = 0f

    /**
     * 最低价
     */
    var low: Float = 0f

    /**
     * 收盘价
     */
    var close: Float = 0f

    /**
     * 成交量
     */
    var volume: Long = 0

    /**
     * 昨日收盘价
     */
    var preClose: Float = 0f

    /**
     * 涨停价
     */
    var riseMaxPrice: Float = 0f

    /**
     * 跌停价
     */
    var fallMaxPrice: Float = 0f

    /**
     * 涨跌幅
     */
    lateinit var increaseRate: String

    /**
     * 涨跌额
     */
    lateinit var increaseExtra: String

    /**
     * 近5天平均价格
     */
    var average5: Float = 0f

    /**
     * 近10天平均价格
     */
    var average10: Float = 0f

    /**
     * 近30天平均价格
     */
    var average30: Float = 0f

    /**
     * 总市值
     */
    lateinit var marketCap: String

    /**
     * 换手率
     */
    lateinit var turnoverRate: String

    /**
     * 成交额
     */
    lateinit var volumeExtra: String

    /**
     * 总股本
     */
    var shareCapitaltotal = 300000000L //虚拟份额

    fun compare(price: Float): Boolean {
        return price < preClose
    }

    //5日，30日，120日价格均线点位置
    lateinit var point5: PointF
    lateinit var point10: PointF
    lateinit var point30: PointF
}