package cm.nfx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cm.nfx.util.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    String LOG_TAG_ACTIVITY = "MainActivity";
    String GLOBAL_TRACK_LOG = "oska";
    private Activity mActivity;
    private Toolbar mToolbar;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        Log.i(GLOBAL_TRACK_LOG ,LOG_TAG_ACTIVITY+" onCreate");
        initToolbar();
        initDrawer();

        View header = navigationView.getHeaderView(0);
        Utils.changeToTitleCOlor(mActivity,header);

    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


//startActivity(new Intent(MainActivity.this, BroadcastService.class))
        if (id == R.id.nav_status) {
            Toast.makeText(this,"Please go to Color Setting first!",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_color_setting) {
            startActivity(new Intent(MainActivity.this, ColorSettingActivity.class));
        } else if (id == R.id.nav_immune) {
            Toast.makeText(this,"Please go to Color Setting first!",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_share) {
            Utils.popUpAlertDialogWithQR(mActivity);
        } else if (id == R.id.nav_about_us) {
            Utils.popUpAlertDialog(mActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





}