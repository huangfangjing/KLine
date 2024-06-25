package com.funny.klinelibrary.entity

import com.funny.klinelibrary.utils.NumFormatUtils
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
    private var maxPrice: Float = 0f,

    /**
     * 当天最低价
     */
    private var minPrice: Float = 0f,

    /**
     * 当天开盘价
     */
    private var openPrice: Float = 0f,

    /**
     * 当天收盘价
     */
    var closePrice: Float = 0f,

    /**
     * 昨天收盘价
     */
    private var preClosePrice: Float = 0f


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
     * 分时量集合
     */
    var volumes: MutableList<Int> = setRandomVolumes()

    /**
     * 分时均价集合
     */
    var averagePrice: MutableList<Float> = setAveragePrices()

    /**
     * 分时涨跌
     */
    var randomBooleans: MutableList<Boolean> = setRandomBooleans()

    /**
     * 是否一字板
     */
    var isOneLine: Boolean = false

    /**
     * 当天是否是跌
     */
    var isFall: Boolean = (closePrice < preClosePrice)

    /**
     * 设置分时图随机数
     */
    fun setRandomPrices(): MutableList<Float> {
        val pricesData: MutableList<Float> = mutableListOf()

        val random = Random(System.currentTimeMillis())
        isOneLine = (openPrice == closePrice && openPrice == maxPrice && openPrice == minPrice)
        when (isOneLine) {
            true -> {
                (0..119).forEach { _ ->
                    pricesData.add(openPrice)

                }
            }

            else -> {
                val amplitude = preClosePrice //波动幅度 1%
                pricesData.add(0, closePrice)
                (1..118).forEach { _ ->
                    val data = random.nextInt(-amplitude.toInt(), amplitude.toInt()) / 100f
                    pricesData.add(0, max(minPrice, min(pricesData[0] + data, maxPrice)))
                }
                pricesData.add(0, openPrice)


            }
        }

        return pricesData
    }


    /**
     * 设置分时图均价
     */
    private fun setAveragePrices(): MutableList<Float> {

        val averagePrice: MutableList<Float> = mutableListOf()
        prices.forEachIndexed { index, _ ->
            averagePrice.add(
                NumFormatUtils.formatFloat(prices.take(index + 1).average(), 2).toFloat()
            )
        }
        return averagePrice
    }

    /**
     * 设置分时量，最大100
     */
    private fun setRandomVolumes(): MutableList<Int> {

        val random = Random(System.currentTimeMillis())
        val volumes: MutableList<Int> = mutableListOf()
        volumes.add(90)
        volumes.add(80)
        volumes.add(75)
        volumes.add(60)
        volumes.add(72)
        volumes.add(62)
        volumes.add(60)
        volumes.add(58)
        volumes.add(63)
        volumes.add(55)

        (0..108).forEach { _ ->
            volumes.add(random.nextInt(1, 50))
        }
        volumes.add(75)
        return volumes
    }


    /**
     * 设置分时随机涨跌
     */
    private fun setRandomBooleans(): MutableList<Boolean> {
        val booleans: MutableList<Boolean> = mutableListOf()
        (0..119).forEach { _ ->
            booleans.add(Random.nextBoolean())
        }
        return booleans
    }
}