package com.januszhou.learn.circleavatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by janus on 14-10-11.
 */
public class Avatar extends ImageView {

  private static final Xfermode MASK_XFERMODE;
  private Bitmap mMask;
  private Paint mPaint;
  private int mBorderWidth =  PixelUtil.getPXFromDIP( getContext(), 6 );
  private int mBorderColor = Color.parseColor( "#f8f8f8" );

  static {
    PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
    MASK_XFERMODE = new PorterDuffXfermode( localMode );
  }

  public Avatar( Context paramContext ) {
    super( paramContext );
  }

  public Avatar( Context paramContext, AttributeSet paramAttributeSet ) {
    this( paramContext, paramAttributeSet, 0 );
  }

  public Avatar( Context paramContext, AttributeSet paramAttributeSet, int paramInt ) {
    super( paramContext, paramAttributeSet, paramInt );

//    TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CircleImageView);
//    mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, mBorderColor);
//    mBorderWidth = a.getDimensionPixelOffset(R.styleable.CircleImageView_border_width, mBorderWidth);
//    //transfer width from dp to px
//    mBorderWidth = PixelUtil.getPXFromDIP( getContext(), mBorderWidth );
//    a.recycle();
  }

  private boolean useDefaultStyle = false;

  public void setUseDefaultStyle( boolean useDefaultStyle ) {
    this.useDefaultStyle = useDefaultStyle;
  }

  @Override
  protected void onDraw( Canvas paramCanvas ) {
    if ( useDefaultStyle ) {
      super.onDraw( paramCanvas );
      return;
    }
    final Drawable localDrawable = getDrawable();
    if ( localDrawable == null )
      return;
    if ( localDrawable instanceof NinePatchDrawable ) {
      return;
    }
    if ( this.mPaint == null ) {
      final Paint localPaint = new Paint();
      localPaint.setFilterBitmap( false );
      localPaint.setAntiAlias( true );
      localPaint.setXfermode( MASK_XFERMODE );
      this.mPaint = localPaint;
    }
    final int width = getWidth();
    final int height = getHeight();
    // save layer
    int layer = paramCanvas.saveLayer( 0.0F, 0.0F, width, height, null, Canvas.ALL_SAVE_FLAG );
    // set drawable
    localDrawable.setBounds( 0, 0, width, height );
    // bind the drawable to bitmap(this.mask)
    localDrawable.draw( paramCanvas );
    if ( ( this.mMask == null ) || ( this.mMask.isRecycled() ) ) {
      this.mMask = createOvalBitmap( width, height );
    }
    // draw bitmap on canvas
    paramCanvas.drawBitmap( this.mMask, 0.0F, 0.0F, this.mPaint );
    // copy canvas to layer
    paramCanvas.restoreToCount( layer );
    drawBorder( paramCanvas, width, height );
  }

  //draw the outside boarder
  private void drawBorder( Canvas canvas, final int width, final int height ) {
    if ( mBorderWidth == 0 ) {
      return;
    }
    final Paint mBorderPaint = new Paint();
    mBorderPaint.setStyle( Paint.Style.STROKE );
    mBorderPaint.setAntiAlias( true );
    mBorderPaint.setColor( mBorderColor );
    mBorderPaint.setStrokeWidth( mBorderWidth );

    canvas.drawCircle( width >> 1, height >> 1, ( width - mBorderWidth ) >> 1, mBorderPaint );
    canvas = null;
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if ( this.mMask != null )
      this.mMask.recycle();
  }

  public Bitmap createOvalBitmap( final int width, final int height ) {
    Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
    Bitmap localBitmap = Bitmap.createBitmap( width, height, localConfig );
    Canvas localCanvas = new Canvas( localBitmap );
    Paint localPaint = new Paint();
    //set padding for better result
    final int padding = ( mBorderWidth - 3 ) > 0 ? mBorderWidth - 3 : 1;
    RectF localRectF = new RectF( padding, padding, width - padding, height - padding );
    localCanvas.drawOval( localRectF, localPaint );
    return localBitmap;
  }
}

