package com.januszhou.learn.circleavatar;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by janus on 14-10-31.
 */

public class PixelUtil
{
  public static float getPXFromDIP( Context context, float dip )
  {
    Resources r = context.getResources();
    return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics() );
  }

  public static int getPXFromDIP( Context context, int dip )
  {
    Resources r = context.getResources();
    return (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics() );
  }

  public static float getDIPFromPX( Context context, float pixels )
  {
    // convert pixels to dip
    final float scale = context.getResources().getDisplayMetrics().density;
    return pixels / scale;
  }

  public static int getDIPFromPX( Context context, int pixels )
  {
    // convert pixels to dip
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pixels / scale);
  }

  public static boolean isMdpi( Context context )
  {
    final float scale = context.getResources().getDisplayMetrics().density;
    return scale == 1;
  }
}

