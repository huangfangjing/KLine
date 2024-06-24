package com.funny.klinelibrary.entity

import java.io.Serializable
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MinuteData(

    /**
     * 当天最高价
     */
    var day: String,
    /**
     * 当天最高价
     */
    var maxPrice: Float = 0f,

    /**
     * 当天最低价
     */
    var minPrice: Float = 0f,

    /**
     * 当天开盘价
     */
    var openPrice: Float = 0f,

    /**
     * 当天收盘价
     */
    var closePrice: Float = 0f,

    /**
     * 昨天收盘价
     */
    var preClosePrice: Float = 0f


) : Serializable {

    /**
     * 当天涨停价
     */
    var heighLimit: Float = preClosePrice * 1.1f

    /**
     * 当天涨停价
     */
    var lowLimit: Float = preClosePrice * 0.9f

    /**
     * 分时价格集合
     */
    var prices: MutableList<Float> = setRandomPrices()

    /**
     * 是否一字板
     */
    private var isOneLine: Boolean = false


    fun setRandomPrices(): MutableList<Float> {
        val pricesData: MutableList<Float> = mutableListOf()
        val random = Random(System.currentTimeMillis())
        isOneLine = (openPrice == closePrice && openPrice == maxPrice && openPrice == minPrice)
        when (isOneLine) {
            true -> {
                (0..119).forEach { i ->
                    pricesData.add(openPrice)
                }
            }

            else -> {
                val amplitude = preClosePrice //波动幅度 1%
                pricesData.add(0, openPrice)
                (1..117).forEach { i ->
                    val data = random.nextInt(-amplitude.toInt(), amplitude.toInt()) / 100f
                    pricesData.add(i, max(lowLimit, min(pricesData[i - 1] + data, heighLimit)))
                }
                pricesData.add(pricesData[117])
                pricesData.add(closePrice)
                pricesData.add(closePrice)

            }
        }

        return pricesData
    }
}