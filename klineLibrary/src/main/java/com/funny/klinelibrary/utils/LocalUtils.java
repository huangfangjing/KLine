package com.funny.klinelibrary.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LocalUtils {

  /**
   * 从asset中读取js代码
   */
  public static String getFromAssets(Context context, String fileName) {
    try {
      InputStreamReader
          inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
      BufferedReader bufReader = new BufferedReader(inputReader);
      String line = "";
      String result = "";
      while ((line = bufReader.readLine()) != null) {
        result += line.trim();
      }
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
