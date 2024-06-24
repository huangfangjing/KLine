package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.LinearLayout
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.helper.KLineSourceHelper
import com.funny.klinelibrary.entity.MinuteData
import com.funny.klinelibrary.inter.KlineGestureListener
import com.funny.klinelibrary.utils.DateUtils
import com.funny.klinelibrary.utils.DisplayUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 *@author : hfj
 */
class KLineViewGroup(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private lateinit var mGestureDetector: GestureDetector //手势识别
    private var mKLineActionListener: KlineGestureListener? = null//动作监听回调

    //移动
    private var scrollX = 0f //x方向移动的距离

    //缩放
    private var mStartXDist = 0f //两指按下时x方向的距离

    //长按
    private var isPressState = false //记录点击状态
    private var isLongPressState = false //记录长按状态
    private var mFocusPoint: PointF = PointF() //长按时十字线的交叉点

    //数据源
    lateinit var mKLineDatas: MutableList<KLineDrawItem>
    lateinit var mExtremeValue: ExtremeValue

    //延时任务
    private val mHandler: Handler = Handler(Looper.getMainLooper())

    private val mRunnable = Runnable {
        if (isPressState) {
            isPressState = false
            isLongPressState = false
            dispatchDrawData()
            mKLineActionListener?.onLongPress(getLastDrawItem())
        }
    }

    init {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        mGestureDetector = GestureDetector(context, SimpleGestureListener())
    }

    fun setData(mToDrawList: MutableList<KLineDrawItem>, extremeValue: ExtremeValue) {
        this.mKLineDatas = mToDrawList
        this.mExtremeValue = extremeValue
    }

    fun setKLineActionListener(listener: KlineGestureListener) {
        this.mKLineActionListener = listener
    }

    private fun getLastDrawItem(): KLineDrawItem {
        return mKLineDatas[mKLineDatas.size - 1]
    }

    private fun getKLineView(): KLineView {
        return getChildAt(0) as KLineView
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                if (isLongPressState) {
                    val mostNearX = getMostNearX(event.x)
                    mFocusPoint = PointF(mostNearX, event.y)
                    dispatchLongPress(mFocusPoint, getFocusIndex(mostNearX))
                }
                if (event.pointerCount == 2) {
                    val xDist = abs((event.getX(0) - event.getX(1)))
                    val scaleX = xDist / mStartXDist
                    mKLineActionListener?.onChartScale(event, scaleX, 1f)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> if (event.pointerCount == 2) {
                mStartXDist = abs((event.getX(0) - event.getX(1)))
            }

            MotionEvent.ACTION_UP -> {
                if (isLongPressState) {
                    isLongPressState = false
                    dispatchDrawData()
                }
                if (isPressState) {
                    mHandler.postDelayed(mRunnable, 4000)
                } else {
                    mHandler.removeCallbacks(mRunnable)
                    mKLineActionListener?.onLongPress(getLastDrawItem())
                }
            }
        }
        return true
    }

    inner class SimpleGestureListener : SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {

            val mostNearX = getMostNearX(e.x)
            mFocusPoint = PointF(mostNearX, e.y)
            val focusIndex = getFocusIndex(mostNearX)

            when (isPressState) {
                true -> {

                    val klineView = getKLineView()
                    val focusDrawItem = klineView.getFocusDrawItem()

                    if (mostNearX > klineView.mDateRectF.left && mostNearX < klineView.mDateRectF.right &&
                        abs(e.y - klineView.mDateRectF.centerY()) < 60
                    ) {
                        (0 until childCount).map { getChildAt(it) }
                            .filterIsInstance<MinuteLineView>().forEach {
                                removeView(it)
                            }
                        addView(
                            MinuteLineView(
                                context, MinuteData(
                                    DateUtils.getYMD(focusDrawItem.day),
                                    focusDrawItem.high,
                                    focusDrawItem.low,
                                    focusDrawItem.open,
                                    focusDrawItem.close,
                                    focusDrawItem.preClose
                                ).apply {
                                    setRandomPrices()
                                }
                            )
                        )
                        return super.onSingleTapUp(e)
                    }

                    isPressState = false
                    dispatchDrawData()
                }

                else -> {
                    isPressState = true
                    dispatchLongPress(mFocusPoint, focusIndex)
                }
            }
            return super.onSingleTapUp(e)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            isPressState = false
            isLongPressState = true
            val mostNearX = getMostNearX(e.x)
            mFocusPoint = PointF(mostNearX, e.y)
            dispatchLongPress(mFocusPoint, getFocusIndex(mostNearX))
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val scoreRate = max(
                1.0f,
                KLineSourceHelper.COLUMNS_DEFAULT * 1.0f / KLineSourceHelper.K_D_COLUMNS
            )
            scrollX -= (distanceX * scoreRate)
            mKLineActionListener?.onChartTranslate(e2, scrollX)
            scrollX = 0f
            return true
        }
    }

    fun dispatchLongPress(focusPoint: PointF, focusIndex: Int) {
        (0 until childCount).map { getChildAt(it) }
            .filterIsInstance<BaseChartView>().forEach { child ->
                child.isLongPressState = true
                child.mFocusPoint = focusPoint
                child.mFocusIndex = focusIndex
                child.drawData()
            }
        mKLineActionListener?.onLongPress(
            mKLineDatas[max(
                0,
                min(focusIndex, mKLineDatas.size - 1)
            )]
        )
    }

    fun dispatchDrawData() {
        (0 until childCount).map { getChildAt(it) }
            .filterIsInstance<BaseChartView>().forEach { child ->
                child.isLongPressState = false
                child.drawData()
            }
    }

    //获取最靠近的那根k线的中线x坐标
    private fun getMostNearX(x: Float): Float {
        var resultX = -1.0f
        for (i in mKLineDatas.indices) {
            val rect: RectF = mKLineDatas[i].rect
            val middleX = rect.centerX()
            if (resultX < 0) {
                resultX = middleX
            } else {
                if (abs(middleX - x) < abs(resultX - x)) {
                    resultX = middleX
                }
            }
        }
        return resultX
    }

    //获取最靠近的那根k线的中线x坐标
    private fun getFocusIndex(x: Float): Int {
        for (i in mKLineDatas.indices) {
            if (mKLineDatas[i].rect.centerX() == x) {
                return i
            }
        }
        return 0
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
    }
}