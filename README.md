这是一款使用kotlin语言开发的股票K线走势行情图，效果如下：



<img src="https://github.com/huangfangjing/KLine/blob/master/show1.jpg" width="400px">      <img src="https://github.com/huangfangjing/KLine/blob/master/show2.jpg" width="400px">    
<img src="https://github.com/huangfangjing/KLine/blob/master/show3.jpg" width="400px">      <img src="https://github.com/huangfangjing/KLine/blob/master/kline.gif" width="390px">





项目中使用到的相关技术



1.自定义View（这里的方式是使用canvas绘图的方式，包含drawPath，drawRectF,drawText.drawBitmap等等）

2.使用GestureDetector实现用户手势检测,项目使用到的手势分别为点击(onSingleTapUp),长按（onLongPress），滑动（onScroll），长按后滑动（MotionEvent.ACTION_MASK）
  缩放（MotionEvent.ACTION_MASK 判断event.pointerCount == 2）
  
3.onTouch事件传递，本项目包含三个自定义的View，KLineViewGroup（父布局），KLineView（子View，K线图），VolumeView（子View，成交量图），MinuteLineView(子View，分时图)
  项目使用了最简单的方式，集中在父View监听onTouch事件，然后根据需要传递到子View并交给子类具体实现。
  
4.K线数据的指标计算和绘制位置计算（这个不是项目的技术关键，虽然有些算起来头大）

5.使用handler+Runnable进行事件分发和取消（postDelayed，removeCallbacks）

6.DataBinding数据绑定

7.用户交互，所有的交互方式均与同花顺APP一样

  交互如下
  
  v1：用户点击K线后，绘制十字线焦点。若五秒后无其他操作，自动释放焦点，若期间再次点击，则立即取消焦点

  v2：用户滑动K线，实时刷新K线数据

  v3：用户长按或长按后滑动K线，实时绘制选中K线涨跌情况

  v4：用户点击选中K线后，点击分时图区域，弹出股票分时走势图（包含价格走势和成交量走势）

  v5：分时图模式下，选中当前分时图对应的K仙线（绘制虚线表示）

  v6：分时图模式下滑动，手抬起时绘制抬起时对应的K线走势图

  v7：关闭分时图，K线重置


部分实现代码

//使用模拟数据（解析asset数据），数据准备完毕后回调

private val onReadyListener: IChartDataCountListener<MutableList<KLineDrawItem>> =

        IChartDataCountListener{ data, extremeValue ->
        
            mBinding.klineGroup.setData(data, extremeValue) //设置数据源
            
            mBinding.klineGroup.dispatchDrawData() //分发子类分别绘制
            
            mBinding.kLineData = data[data.size - 1] //DataBinding数据绑定
        }


  //手势滑动
  
    override fun onChartTranslate(me: MotionEvent?, dX: Float) {

        mHelper.initKLineDrawData(dX, KLineSourceHelper.SourceType.MOVE)
        
        }

 //手势缩放   

 
override fun onChartTranslate(dX: Float) {

        mHelper.initKLineDrawData(dX, KLineDataHelper.SourceType.MOVE)
        
    }


//长按


override fun onLongPress(drawItem: KLineDrawItem) {

        mBinding.kLineData = drawItem

    }
