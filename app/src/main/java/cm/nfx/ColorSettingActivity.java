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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by paulck on 3/5/16.
 */
public class ColorSettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RadioGroup rgroup;
    private RadioGroup radioGroupIdentity;
    private NavigationView NavigationView;
    private Toolbar mToolbar;
    private Activity mActivity;
    private Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_color_setting);
        initToolbar();

        initDrawer();

        View header = NavigationView.getHeaderView(0);
        Utils.changeToTitleCOlor(mActivity,header);




        startBtn = (Button) findViewById(R.id.start);
        radioGroupIdentity = (RadioGroup) findViewById(R.id.identityGroup);

        btnGoHide();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if(Utils.identity==0) {
                    Utils.simpleAlertDialog( mActivity, "Please select poor man/ rich man base on the character card you choose.");

                }else{
                    Utils.BUTTON_STATE = 1;

                    startActivity(new Intent(mActivity, PlayTimeActivity.class));



                }


            }
        });
        Utils.changeBgCOlor(mActivity,startBtn);


        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(listener);



        radioGroupIdentity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub


                if(checkedId == R.id.poorMan)
                {
                    Utils.identity = 1;
                }
                if(checkedId == R.id.richMan)
                {
                    Utils.identity = 2;
                }


            }
        });

    }

    private void btnGoHide(){

        if(Utils.BUTTON_STATE==1){

            radioGroupIdentity.setVisibility(View.INVISIBLE);
            startBtn.setVisibility(View.INVISIBLE);
        }

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationView.setNavigationItemSelectedListener(this);


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub


            switch (checkedId) {
                case R.id.red_color:

                    Utils.changeToTheme(ColorSettingActivity.this, Utils.THEME_RED);
                    break;
                case R.id.blue_color:
                    Utils.changeToTheme(mActivity, Utils.THEME_BLUE);
                    break;
                case R.id.yellow_color:
                    Utils.changeToTheme(mActivity, Utils.THEME_YELLOW);
                    break;
                case R.id.pink_color:
                    Utils.changeToTheme(mActivity, Utils.THEME_PINK);
                    break;
                case R.id.green_color:
                    Utils.changeToTheme(mActivity, Utils.THEME_DEFAULT);
                    break;

            }

        }

    };


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_status) {
            if(Utils.BUTTON_STATE==0){
                Toast.makeText(this,"Please press start in Color Setting!",Toast.LENGTH_LONG).show();
            }else {
                startActivity(new Intent(mActivity, PlayTimeActivity.class));
            }
        } else if (id == R.id.nav_color_setting) {
            Toast.makeText(this,"You already in this page!",Toast.LENGTH_LONG).show();
        }  else if (id == R.id.nav_share) {
            Utils.popUpAlertDialogWithQR(mActivity);
        } else if (id == R.id.nav_about_us) {
            Utils.popUpAlertDialog(mActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
