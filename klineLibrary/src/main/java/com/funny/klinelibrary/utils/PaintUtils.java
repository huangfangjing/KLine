package com.funny.klinelibrary.utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * @author guannan
 * @date on 2020-04-11 14:18
 * @des 画笔工具类
 */
public class PaintUtils {

    private static int TEXT_SIZE_SP;

    public static Paint KLINE_PAINT_RED = new Paint();
    public final static int KLINE_PAINT_RED_COLOR = 0xfffd4331;

    public static Paint KLINE_PAINT_GREEN = new Paint();
    public final static int KLINE_PAINT_GREEN_COLOR = 0xff05aa3b;

    /**
     * 行情图外部分隔线
     */
    public static Paint GRID_DIVIDER = new Paint();
    public final static int COLOR_GRID_DIVIDER = 0xffe5e6f2;

    /**
     * 行情图内部分隔线
     */
    public static Paint GRID_INNER_DIVIDER = new Paint();

    /**
     * 表格外文字
     */
    public static Paint TEXT_PAINT = new Paint();
    public final static int C_FRAME_FONT = 0xff747985;

    /**
     * 选中十字线
     */
    public static Paint FOCUS_LINE_PAINT = new Paint();
    public final static int C_FOCUS_BG_BLACK = 0xff000000;

    /**
     * 长按弹框背景颜色
     */
    public static Paint POP_DIALOG_PAINT = new Paint();
    public final static int C_POP_DIALOG_BG = 0x99091622;

    /**
     * 表格外文字
     */
    public static Paint TEXT_POP_PAINT = new Paint();
    public final static int C_TEXT_POP = 0xffffffff;

    /**
     * 红
     */
    public static Paint TEXT_RED_PAINT = new Paint();
    public final static int C_TEXT_RED = 0xfffd4331;

    /**
     * 绿
     */
    public static Paint TEXT_GREEN_PAINT = new Paint();
    public final static int C_TEXT_GREEN = 0xff05aa3b;

    /**
     * 黄
     */
    public static Paint LINE_YELLOW_PAINT = new Paint();
    public static Paint TEXT_YELLOW_PAINT = new Paint();
    public final static int C_LINE_DEA = 0XFFFFA100;

    /**
     * 蓝
     */
    public static Paint LINE_BLUE_PAINT = new Paint();
    public static Paint TEXT_BLUE_PAINT = new Paint();
    public final static int C_LINE_DIF = 0XFF4E8BEE;

    /**
     * 紫
     */
    public static Paint LINE_PURPLE_PAINT = new Paint();
    public static Paint TEXT_PURPLE_PAINT = new Paint();
    public final static int C_LINE_MACD = 0XFFFF3FBE;


    /**
     * 黑色箭头
     */
    public static Paint ARROW_BLACK_PAINT = new Paint();
    public final static int COLOR_BLACK = 0xff000000;

    /**
     * 紫色矩形
     */
    public static Paint BLUE_RECTF_PAINT = new Paint();
    public final static int COLOR_BLUE_RECTF = 0xFF9b58b5;

    /**
     * 箭头文字黑笔
     */
    public static Paint BLACK_PAINT = new Paint();

    /**
     * 矩形框内白色笔
     */
    public static Paint WHITE_PAINT = new Paint();
    public final static int COLOR_WHITE = 0xFFFFFFFF;

    static {

        BLUE_RECTF_PAINT.setColor(COLOR_BLUE_RECTF);
        BLUE_RECTF_PAINT.setStyle(Paint.Style.FILL);
        BLUE_RECTF_PAINT.setAntiAlias(true);

        BLACK_PAINT.setColor(COLOR_BLACK);
        BLACK_PAINT.setAntiAlias(true);

        WHITE_PAINT.setColor(COLOR_WHITE);
        WHITE_PAINT.setAntiAlias(true);

        ARROW_BLACK_PAINT.setColor(COLOR_BLACK);
        ARROW_BLACK_PAINT.setStyle(Paint.Style.STROKE);
        ARROW_BLACK_PAINT.setStrokeWidth(2.5f);
        ARROW_BLACK_PAINT.setAntiAlias(true);

        KLINE_PAINT_RED.setColor(KLINE_PAINT_RED_COLOR);
        KLINE_PAINT_RED.setStyle(Paint.Style.FILL);
        KLINE_PAINT_RED.setAntiAlias(true);

        KLINE_PAINT_GREEN.setColor(KLINE_PAINT_GREEN_COLOR);
        KLINE_PAINT_GREEN.setStyle(Paint.Style.FILL);
        KLINE_PAINT_GREEN.setAntiAlias(true);

        GRID_DIVIDER.setColor(COLOR_GRID_DIVIDER);
        GRID_DIVIDER.setStyle(Paint.Style.STROKE);
        GRID_DIVIDER.setStrokeWidth(1.5f);
        GRID_DIVIDER.setAntiAlias(true);

        GRID_INNER_DIVIDER.setColor(COLOR_GRID_DIVIDER);
        GRID_INNER_DIVIDER.setStyle(Paint.Style.STROKE);
        GRID_INNER_DIVIDER.setStrokeWidth(1.5f);
        GRID_INNER_DIVIDER.setAntiAlias(true);

        TEXT_PAINT.setAntiAlias(true);
        TEXT_PAINT.setColor(C_FRAME_FONT);

        FOCUS_LINE_PAINT.setColor(C_FOCUS_BG_BLACK);
        FOCUS_LINE_PAINT.setStrokeWidth(2.5f);

        POP_DIALOG_PAINT.setColor(C_POP_DIALOG_BG);
        POP_DIALOG_PAINT.setAntiAlias(true);
        POP_DIALOG_PAINT.setStyle(Paint.Style.FILL);

        TEXT_POP_PAINT.setColor(C_TEXT_POP);
        TEXT_POP_PAINT.setAntiAlias(true);

        TEXT_RED_PAINT.setColor(C_TEXT_RED);
        TEXT_RED_PAINT.setAntiAlias(true);

        TEXT_GREEN_PAINT.setColor(C_TEXT_GREEN);
        TEXT_GREEN_PAINT.setAntiAlias(true);

        LINE_YELLOW_PAINT.setStyle(Paint.Style.STROKE);
        LINE_YELLOW_PAINT.setStrokeWidth(2.5f);
        LINE_YELLOW_PAINT.setColor(C_LINE_DEA);
        LINE_YELLOW_PAINT.setStrokeCap(Paint.Cap.ROUND);
        LINE_YELLOW_PAINT.setAntiAlias(true);

        TEXT_YELLOW_PAINT.setStyle(Paint.Style.FILL);
        TEXT_YELLOW_PAINT.setColor(C_LINE_DEA);
        TEXT_YELLOW_PAINT.setAntiAlias(true);

        LINE_BLUE_PAINT.setStyle(Paint.Style.STROKE);
        LINE_BLUE_PAINT.setStrokeWidth(2.5f);
        LINE_BLUE_PAINT.setColor(C_LINE_DIF);
        LINE_BLUE_PAINT.setStrokeCap(Paint.Cap.ROUND);
        LINE_BLUE_PAINT.setAntiAlias(true);

        TEXT_BLUE_PAINT.setStyle(Paint.Style.FILL);
        TEXT_BLUE_PAINT.setColor(C_LINE_DIF);
        TEXT_BLUE_PAINT.setAntiAlias(true);

        LINE_PURPLE_PAINT.setStyle(Paint.Style.STROKE);
        LINE_PURPLE_PAINT.setStrokeWidth(2.5f);
        LINE_PURPLE_PAINT.setColor(C_LINE_MACD);
        LINE_PURPLE_PAINT.setStrokeCap(Paint.Cap.ROUND);
        LINE_PURPLE_PAINT.setAntiAlias(true);

        TEXT_PURPLE_PAINT.setStyle(Paint.Style.FILL);
        TEXT_PURPLE_PAINT.setColor(C_LINE_MACD);
        TEXT_PURPLE_PAINT.setAntiAlias(true);
    }

    public static void init(Context context) {
        TEXT_SIZE_SP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8,
                context.getResources().getDisplayMetrics());
        TEXT_PAINT.setTextSize(TEXT_SIZE_SP);
        TEXT_YELLOW_PAINT.setTextSize(TEXT_SIZE_SP);
        TEXT_BLUE_PAINT.setTextSize(TEXT_SIZE_SP);
        TEXT_PURPLE_PAINT.setTextSize(TEXT_SIZE_SP);
        WHITE_PAINT.setTextSize(TEXT_SIZE_SP);

        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                context.getResources().getDisplayMetrics());
        TEXT_POP_PAINT.setTextSize(textSize);
        TEXT_RED_PAINT.setTextSize(textSize);
        TEXT_GREEN_PAINT.setTextSize(textSize);
        ARROW_BLACK_PAINT.setTextSize(textSize);

        float textSize10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                context.getResources().getDisplayMetrics());
        BLACK_PAINT.setTextSize(textSize10);
    }
}
