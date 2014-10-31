package com.januszhou.learn.circleavatar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class DemonActivity extends Activity {

  final static String img_url1 = "http://ww2.sinaimg.cn/mw690/6927e7a5jw1ebwe3wllq9j20b40b4t9s.jpg";
  final static String img_url2 = "http://ww3.sinaimg.cn/mw690/40d61044jw8eljwlszz96j20e80e8dg6.jpg";
  final static String img_url3 = "http://img1.touxiang.cn/uploads/20120904/04-005718_392.jpg";
  final static String img_url4 = "http://img1.touxiang.cn/uploads/20120821/21-005406_525.jpg";

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_demon );

    final Avatar2 avatarView = ( Avatar2 ) findViewById( R.id.img_avatar );
    avatarView.setRightCount( 1 );
    avatarView.setLeftInitial( "MM" );
    avatarView.setRightInitial( "II" );
    avatarView.setLeftUrl( img_url1 );
    avatarView.setRightUrl( img_url3 );
  }


  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.demon, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item ) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if ( id == R.id.action_settings ) {
      return true;
    }
    return super.onOptionsItemSelected( item );
  }
}
