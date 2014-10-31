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
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created by janus on 14-10-11.
 */
public class Avatar2 extends ImageView {

  private Paint mPaint;
  private static final Xfermode MASK_XFERMODE;
  private Bitmap mLeftBitmap, mRightBitmap;
  private String mLeftUrl, mRightUrl;
  private String mLeftInitial,mRightInitial;
  private int mRightCount;

  private enum AVATAR_LEFT_STYLE { NOT_ASSIGNED, INITIAL, PHOTO };
  private enum AVATAR_RIGHT_STYLE { NOT_ASSIGNED, INITIAL, PHOTO, PLUS, NONE };

  private AVATAR_LEFT_STYLE mLeftStatus;
  private AVATAR_RIGHT_STYLE mRightStatus;
  private boolean mIsSingleAvatar;

  private ImageLoader mImageLoader;

  private int mBorderWidth;
  private int mBorderColor;

  static {
    PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
    MASK_XFERMODE = new PorterDuffXfermode( localMode );
  }

  public Avatar2( Context context ) {
    super( context );
    init();
  }

  public Avatar2( Context context, AttributeSet attributeSet ) {
    this( context, attributeSet, 0 );
  }

  public Avatar2( Context context, AttributeSet paramAttributeSet, int style ) {
    super( context, paramAttributeSet, style );
    init();
  }

  public void init() {
    mIsSingleAvatar = true;
    mRightCount = 0;
    mLeftStatus = AVATAR_LEFT_STYLE.NOT_ASSIGNED;
    mRightStatus = AVATAR_RIGHT_STYLE.NOT_ASSIGNED;

    if ( this.mPaint == null ) {
      final Paint paint = new Paint();
      paint.setFilterBitmap( false );
      paint.setAntiAlias( true );
      paint.setXfermode( MASK_XFERMODE );
      this.mPaint = paint;
    }
    mBorderWidth =  PixelUtil.getPXFromDIP( getContext(), 4 );
    mBorderColor = Color.parseColor( "#f8f8f8" );
    mImageLoader = ImageLoader.getInstance();
    ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault( getContext() );
    mImageLoader.init( config );

    getRemoteLeftBitmap();
    getRemoteRightBitmap();
  }

  private void getRemoteLeftBitmap() {
    Bitmap bitmap = ( ( BitmapDrawable ) getDrawable() ).getBitmap();
    setLeftBitmap( bitmap );
    mImageLoader.loadImage( mLeftUrl, new SimpleImageLoadingListener() {
      @Override
      public void onLoadingFailed( String s, View view, FailReason failReason ) {
        Bitmap bitmap = ( ( BitmapDrawable ) getDrawable() ).getBitmap();
        setLeftBitmap( bitmap );
      }

      @Override
      public void onLoadingComplete( String s, View view, Bitmap bitmap ) {
        setLeftBitmap( bitmap );
      }
    } );
  }

  private void getRemoteRightBitmap() {
    Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
    setRightBitmap( bitmap );
    mImageLoader.loadImage( mRightUrl, new SimpleImageLoadingListener() {
      @Override
      public void onLoadingFailed( String s, View view, FailReason failReason ) {
        Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        setRightBitmap( bitmap );
      }

      @Override
      public void onLoadingComplete( String s, View view, Bitmap bitmap ) {
        setRightBitmap( bitmap );
      }
    });
  }

  private void updateStatus() {
    if (mLeftUrl == null || mLeftUrl.isEmpty()) {
      if (mLeftInitial == null) mLeftStatus = AVATAR_LEFT_STYLE.NOT_ASSIGNED;
      else mLeftStatus = AVATAR_LEFT_STYLE.INITIAL;
    } else {
      mLeftStatus = AVATAR_LEFT_STYLE.PHOTO;
    }

    switch ( mRightCount ){
      case 0:
        mRightStatus = AVATAR_RIGHT_STYLE.NONE;
        break;
      case 1:
        mIsSingleAvatar = false;
        if (mRightUrl == null || mRightUrl.isEmpty()) {
          if (mRightInitial == null) mRightStatus = AVATAR_RIGHT_STYLE.NOT_ASSIGNED;
          else mRightStatus = AVATAR_RIGHT_STYLE.INITIAL;
        } else {
          mRightStatus = AVATAR_RIGHT_STYLE.PHOTO;
        }
        break;
      default:
        mIsSingleAvatar = false;
        mRightStatus = AVATAR_RIGHT_STYLE.PLUS;
    }

    if(mRightStatus == AVATAR_RIGHT_STYLE.NONE)
      mIsSingleAvatar = true;
    else
      mIsSingleAvatar = false;
  }

  @Override
  protected void onDraw( Canvas canvas ) {
    final int width = getWidth();
    final int height = getHeight();

    if (mIsSingleAvatar)
      drawCenteredAvatar( canvas, width, height );
    else {

      drawLeftAvatar( canvas, width, height );
      drawRightAvatar( canvas, width, height );

    }
    drawBorder( canvas, width, height );
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
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (this.mLeftBitmap != null)
      this.mLeftBitmap.recycle();
    if ( this.mRightBitmap != null )
      this.mRightBitmap.recycle();
  }

  private void drawCenteredAvatar(Canvas canvas, final int width, final int height) {
    Bitmap ovalBitmap = createOvalBitmap( width, height );
    Paint paint = new Paint();
    paint.setTextSize( 120 );
    switch ( mLeftStatus ) {
      case NOT_ASSIGNED:
        canvas.drawBitmap( ovalBitmap, 0, 0, paint);
        paint.setColor( Color.WHITE );
        canvas.drawText( "?", (width - paint.measureText("?"))/2 , (height+paint.getTextSize()/2)/2, paint);
        break;
      case INITIAL:
        canvas.drawBitmap( ovalBitmap, 0, 0, paint );
        paint.setColor( Color.WHITE );
        canvas.drawText( mLeftInitial, (width - paint.measureText(mLeftInitial))/2, (height + paint.getTextSize()/2)/2, paint );
        break;
      case PHOTO:
        // save layer
        int layer = canvas.saveLayer( 0.0F, 0.0F, width, height, null, Canvas.ALL_SAVE_FLAG );
        mLeftBitmap = Bitmap.createScaledBitmap( mLeftBitmap, width, height, true );
        canvas.drawBitmap( mLeftBitmap, 0, 0, paint);
        canvas.drawBitmap( ovalBitmap, 0, 0, this.mPaint );
        canvas.restoreToCount(layer);
        break;
    }
  }

  private void drawLeftAvatar(Canvas canvas, final int width, final int height) {
    Bitmap bitmap;
    Paint paint = new Paint();
    switch ( mLeftStatus ) {
      case INITIAL:
        bitmap = createLeftInitial( mLeftInitial, width, height );
        break;
      case PHOTO:
        bitmap = createLeftBitmap( width, height );
        break;
      default:
        bitmap = createLeftInitial( "?", width, height );
    }
    canvas.drawBitmap( bitmap, 0.0F, 0.0F, paint);
  }

  private void drawRightAvatar(Canvas canvas, final int width, final int height) {
    Bitmap bitmap;
    Paint paint = new Paint();
    switch ( mRightStatus ) {
      case INITIAL:
        bitmap = createRightInitial( mRightInitial, width, height );
        canvas.drawBitmap( bitmap, 0.0F, 0.0F, paint);
        break;
      case PHOTO:
        bitmap = createRightBitmap( width, height );
        canvas.drawBitmap( bitmap, 0.0F, 0.0F, paint);
        break;
      case PLUS:
        bitmap = createRightInitial( "+"+mRightCount, width, height );
        canvas.drawBitmap( bitmap, 0.0F, 0.0F, paint);
        break;
      case NOT_ASSIGNED:
        bitmap = createRightInitial( "?", width, height );
        canvas.drawBitmap( bitmap, 0.0F, 0.0F, paint);
        break;
      default:
        //TODO: handle error for test
        bitmap = createRightInitial( "!", width, height );
        canvas.drawBitmap( bitmap, 0.0F, 0.0F, paint);
    }

  }

  public Bitmap createOvalBitmap( final int width, final int height ) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();
    //set padding for better result
    final int padding = ( mBorderWidth - 3 ) > 0 ? mBorderWidth - 3 : 1;
    RectF rectF = new RectF( padding, padding, width - padding, height - padding );
    canvas.drawOval( rectF, paint );
    return bitmap;
  }

  public Bitmap createLeftMask( final int width, final int height) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();
    //set padding for better result
    final int padding = ( mBorderWidth - 3 ) > 0 ? mBorderWidth - 3 : 1;
    RectF rectF = new RectF( padding, padding, (width - padding)/2, height - padding );
    canvas.drawRect( rectF, paint );

    Bitmap ovalMask = createOvalBitmap( width, height );
    canvas.drawBitmap( ovalMask, 0.0F, 0.0F, this.mPaint  );

    return bitmap;
  }

  public Bitmap createRightMask( final int width, final int height) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();
    //set padding for better result
    final int padding = ( mBorderWidth - 3 ) > 0 ? mBorderWidth - 3 : 1;
    RectF rectF = new RectF( (width + padding)/2, padding, (width - padding), height - padding );
    canvas.drawRect( rectF, paint );

    Bitmap ovalMask = createOvalBitmap( width, height );
    canvas.drawBitmap( ovalMask, 0.0F, 0.0F, this.mPaint );

    return bitmap;
  }

  private Bitmap createLeftBitmap(final int width, final int height) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();

    //draw left bitmap
    //Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
    Bitmap mask = createLeftMask( width, height );
    if (mLeftBitmap == null) {
      canvas.drawBitmap( mask, 0.0F, 0.0F, this.mPaint );
    }
    else {
      mLeftBitmap = Bitmap.createScaledBitmap( mLeftBitmap, width, height, true );
      canvas.drawBitmap( mLeftBitmap, width / ( -4 ), 0.0F, paint );
      canvas.drawBitmap( mask, 0.0F, 0.0F, this.mPaint );
    }

    return bitmap;
  }

  private Bitmap createRightBitmap(final int width, final int height) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();
    //draw right bitmap
    //Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
    Bitmap mask = createRightMask( width, height );
    if (mRightBitmap == null) {
      canvas.drawBitmap( mask, 0.0F, 0.0F, this.mPaint );
    }
    else {
      mRightBitmap = Bitmap.createScaledBitmap( mRightBitmap, width, height, true );
      canvas.drawBitmap( mRightBitmap, width / 4, 0.0F, paint );
      canvas.drawBitmap( mask, 0.0F, 0.0F, this.mPaint );
    }

    return bitmap;
  }

  private Bitmap createLeftInitial(String str, final int width, final int height) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();
    //draw right background
    Bitmap mask = createLeftMask( width, height );
    canvas.drawBitmap( mask, 0.0F, 0.0F, paint );
    paint.setColor( Color.WHITE );
    paint.setTextSize( 80 );
    canvas.drawText( str, ((width-2*paint.measureText(str))/4) , (height+paint.getTextSize()/2)/2, paint );

    return bitmap;
  }

  private Bitmap createRightInitial(String str, final int width, final int height) {
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    Bitmap bitmap = Bitmap.createBitmap( width, height, config );
    Canvas canvas = new Canvas( bitmap );
    Paint paint = new Paint();
    //draw right background
    Bitmap mask = createRightMask( width, height );
    canvas.drawBitmap( mask, 0.0F, 0.0F, paint );
    paint.setColor( Color.WHITE );
    paint.setTextSize( 80 );
    canvas.drawText( str, (3*(width-paint.measureText(str))/4) , (height+paint.getTextSize()/2)/2, paint );

    return bitmap;
  }

  @Override
  public void invalidate() {
    updateStatus();
    super.invalidate();
  }

  public void setBorderWidth( int mBorderWidth ) {
    this.mBorderWidth = mBorderWidth;
    invalidate();
  }

  public void setBorderColor( int mBorderColor ) {
    this.mBorderColor = mBorderColor;
    invalidate();
  }

  public String getLeftUrl() {
    return mLeftUrl;
  }

  public void setLeftUrl( String leftUrl ) {
    mLeftUrl = leftUrl;
    getRemoteLeftBitmap();
    invalidate();
  }

  public String getRightUrl() {
    return mRightUrl;
  }

  public void setRightUrl( String rightUrl ) {
    mRightUrl = rightUrl;
    getRemoteRightBitmap();
    invalidate();
  }

  public String getLeftInitial() {
    return mLeftInitial;
  }

  public void setLeftInitial( String leftInitial ) {
    mLeftInitial = leftInitial;
    invalidate();
  }

  public String getRightInitial() {
    return mRightInitial;
  }

  public void setRightInitial( String rightInitial ) {
    mRightInitial = rightInitial;
    invalidate();
  }

  public int getRightCount() {
    return mRightCount;
  }

  public void setRightCount( int rightCount ) {
    mRightCount = rightCount;
    invalidate();
  }

  public Bitmap getRightBitmap() {
    return mRightBitmap;
  }

  private void setRightBitmap( Bitmap rightBitmap ) {
    mRightBitmap = rightBitmap;
    invalidate();
  }

  public Bitmap getLeftBitmap() {
    return mLeftBitmap;
  }

  private void setLeftBitmap( Bitmap leftBitmap ) {
    mLeftBitmap = leftBitmap;
    invalidate();
  }

}
