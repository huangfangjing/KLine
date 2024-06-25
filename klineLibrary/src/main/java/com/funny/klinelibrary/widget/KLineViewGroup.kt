package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.LinearLayout
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.entity.MinuteData
import com.funny.klinelibrary.helper.KLineDataHelper
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

    //手势识别
    private lateinit var mGestureDetector: GestureDetector

    //动作监听回调
    private var mKLineActionListener: KlineGestureListener? = null

    //移动
    private var scrollX = 0f //x方向移动的距离

    //缩放
    private var mStartXDist = 0f //两指按下时x方向的距离

    //长按
    var mFocusPoint: PointF = PointF() //长按时十字线的交叉点

    //选中的index
    var mFocusIndex: Int = 0 //长按时十字线的交叉点

    //数据源
    lateinit var mKLineDatas: MutableList<KLineDrawItem>
    lateinit var mExtremeValue: ExtremeValue

    //延时任务
    private val mHandler: Handler = Handler(Looper.getMainLooper())

    private val mRunnable = Runnable {

        if (CHART_STATE == STATE_SINGLE_CLICK) {
            CHART_STATE = STATE_DEFAULT
            dispatchDrawData()
            mKLineActionListener?.onFocusData(getLastDrawItem())
        }

    }

    companion object {

        /**
         * 默认状态
         */
        const val STATE_DEFAULT = 0

        /**
         * 点击状态
         */
        const val STATE_SINGLE_CLICK = 1


        /**
         * 长按状态
         */
        const val STATE_LONG_PRESSED = 2

        /**
         * 显示分时图状态
         */
        const val STATE_SHOW_MINTUNE = 3

        /**
         * 当前状态
         */
        var CHART_STATE = STATE_DEFAULT
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
        return mKLineDatas.last()
    }

    private fun getKLineView(): KLineView {
        return getChildAt(0) as KLineView
    }

    private fun getMinuteLineView(): MinuteLineView? {

        (0 until childCount).map { getChildAt(it) }.forEach {
            if (it is MinuteLineView) {
                return it
            }
        }
        return null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {

                when (CHART_STATE) {

                    STATE_LONG_PRESSED, STATE_SHOW_MINTUNE -> {

                        val mostNearX = getMostNearX(event.x)
                        mFocusPoint = PointF(mostNearX, event.y)
                        mFocusIndex = getFocusIndex(mostNearX)
                        dispatchDrawData(false)
                    }
                }

                if (event.pointerCount == 2) {
                    val xDist = abs((event.getX(0) - event.getX(1)))
                    val scaleX = xDist / mStartXDist

                    KLineDataHelper.K_D_COLUMNS =
                        max(
                            KLineDataHelper.MIN_COLUMNS,
                            min(
                                KLineDataHelper.MAX_COLUMNS,
                                (KLineDataHelper.K_D_COLUMNS / scaleX).toInt()
                            )
                        )
                    mKLineActionListener?.onChartScale()
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> if (event.pointerCount == 2) {
                mStartXDist = abs((event.getX(0) - event.getX(1)))
            }

            MotionEvent.ACTION_UP -> {

                when (CHART_STATE) {
                    STATE_LONG_PRESSED -> {
                        CHART_STATE = STATE_DEFAULT
                        dispatchDrawData()
                    }

                    STATE_SHOW_MINTUNE -> {

                        val klineView = getKLineView()
                        val focusDrawItem = klineView.getFocusDrawItem()
                        openMintuneValue(focusDrawItem)
                    }
                }
            }
        }
        return true
    }

    inner class SimpleGestureListener : SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {

            val klineView = getKLineView()

            when (CHART_STATE) {

                //单击状态时点击
                STATE_SINGLE_CLICK -> {

                    //判断点击打开分时图
                    if (e.x > klineView.mDateRectF.left && e.x < klineView.mDateRectF.right &&
                        abs(e.y - klineView.mDateRectF.centerY()) < 60 && CHART_STATE != STATE_SHOW_MINTUNE
                    ) {

                        //开启分时图
                        CHART_STATE = STATE_SHOW_MINTUNE
                        dispatchDrawData(false)
                        mHandler.removeCallbacks(mRunnable)
                        return super.onSingleTapUp(e)
                    }

                    resetFocus(e.x, e.y)

                    //关闭所有状态
                    CHART_STATE = STATE_DEFAULT
                    dispatchDrawData()
                    mHandler.removeCallbacks(mRunnable)
                }

                //分时图开启状态时点击
                STATE_SHOW_MINTUNE -> {

                    resetFocus(e.x, e.y)

                    val minuteLineView = getMinuteLineView()

                    if (minuteLineView != null && RectF(
                            minuteLineView.mViewRectF.right - DisplayUtils.dip2px(context, 40.0f),
                            minuteLineView.top.toFloat(),
                            minuteLineView.mViewRectF.right,
                            (minuteLineView.top + DisplayUtils.dip2px(context, 40.0f)).toFloat()
                        )
                            .contains(mFocusPoint.x, mFocusPoint.y)
                    ) {

                        closeMintuneValue()
                        CHART_STATE = STATE_DEFAULT
                        dispatchDrawData()
                        return super.onSingleTapUp(e)
                    }

                    dispatchDrawData(false)
                }

                else -> {
                    //默认状态
                    resetFocus(e.x, e.y)
                    CHART_STATE = STATE_SINGLE_CLICK
                    dispatchDrawData(false)
                    mHandler.postDelayed(mRunnable, 5000)//5秒无操作后自动关闭选中状态
                }
            }
            return super.onSingleTapUp(e)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)

            val mostNearX = getMostNearX(e.x)
            mFocusPoint = PointF(mostNearX, e.y)
            mFocusIndex = getFocusIndex(mostNearX)

            when (CHART_STATE) {
                STATE_SHOW_MINTUNE -> {

                    CHART_STATE = STATE_SHOW_MINTUNE
                    dispatchDrawData(false)
                }

                else -> {

                    CHART_STATE = STATE_LONG_PRESSED
                    dispatchDrawData(false)
                }
            }
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val scoreRate = max(
                1.0f,
                KLineDataHelper.COLUMNS_DEFAULT * 1.0f / KLineDataHelper.K_D_COLUMNS
            )
            scrollX -= (distanceX * scoreRate)
            mKLineActionListener?.onChartTranslate(scrollX)
            scrollX = 0f
            return true
        }

    }

    /**
     * 更新选中的位置
     */
    private fun resetFocus(x: Float, y: Float) {
        val mostNearX = getMostNearX(x)
        mFocusPoint = PointF(mostNearX, y)
        mFocusIndex = getFocusIndex(mostNearX)
    }


    /**
     * 开启分时图
     */
    private fun openMintuneValue(focusDrawItem: KLineDrawItem) {

        closeMintuneValue()
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
    }

    private fun closeMintuneValue() {
        (0 until childCount).map { getChildAt(it) }
            .filterIsInstance<MinuteLineView>().forEach {
                removeView(it)
            }
    }

    fun dispatchDrawData(focusLast: Boolean = true) {
        (0 until childCount).map { getChildAt(it) }
            .filterIsInstance<BaseChartView>().forEach { child ->
                child.drawData()
            }

        mKLineActionListener?.onFocusData(
            if (focusLast) getLastDrawItem() else mKLineDatas[max(
                0,
                min(mFocusIndex, mKLineDatas.size - 1)
            )]
        )
    }

    /**
     * 获取最靠近的那根k线的中线x坐标
     */
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

    /**
     * 根据x坐标获取focusIndex
     */
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