package cm.nfx;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import cm.nfx.R;

public class Utils
{
    public static boolean timerFinished=false;
    private static int sTheme=4;
    public  static int BUTTON_STATE = 0; // 0 - show, 1 - hide
    public final static int THEME_RED = 0;
    public final static int THEME_BLUE = 1;
    public final static int THEME_YELLOW = 2;
    public final static int THEME_PINK = 3;
    public final static int THEME_DEFAULT = 4;
    private static int currentColor;
    private static int currentBgColor;
    private static int[] colors = new int[5];
    private static int[] bgcolors = new int[5];
    private final static int sdk = android.os.Build.VERSION.SDK_INT;



    public static void changeBgCOlor(Activity mActivity,View v){

        bgcolors[0] = R.color.startColorRed;
        bgcolors[1] = R.color.startColorBlue;
        bgcolors[2] = R.color.startColorYellow;
        bgcolors[3] = R.color.startColorPink;
        bgcolors[4] = R.color.colorPrimary;

        currentBgColor  = bgcolors[sTheme];

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable( mActivity.getResources().getDrawable(currentBgColor) );
        } else {
            v.setBackground( mActivity.getResources().getDrawable(currentBgColor));
        }

    }


    public static void changeToTitleCOlor(Activity mActivity,View v){

        colors[0] = R.drawable.color_red;
        colors[1] = R.drawable.color_blue;
        colors[2] = R.drawable.color_yellow;
        colors[3] = R.drawable.color_pink;
        colors[4] = R.drawable.color_default;

        currentColor  = colors[sTheme];

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable( mActivity.getResources().getDrawable(currentColor) );
        } else {
            v.setBackground( mActivity.getResources().getDrawable(currentColor));
        }

    }


    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            case THEME_RED:
                activity.setTheme(R.style.AppThemeRed);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppThemeBlue);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppThemeYellow);
                break;
            case THEME_PINK:
                activity.setTheme(R.style.AppThemePink);
                break;
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }



    public static void simpleAlertDialog(Activity activity,String msg){

        new AlertDialog.Builder(activity)
                .setTitle("Notification")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with go back
                    }
                })
                .setIcon(android.R.drawable.ic_menu_info_details)
                .show();

    }


    public static void popUpAlertDialog(Activity activity){

        new AlertDialog.Builder(activity)
                .setTitle("About us")
                .setMessage("This application made by Chu Chung Kit (Paul), and Oscar. This program is for the final project of SM3601 Game Prototyping & Design")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with go back
                    }
                })
                .setIcon(android.R.drawable.ic_menu_info_details)
                .show();

    }
    public static void popUpAlertDialogWithQR(Activity activity){

        AlertDialog.Builder alertadd = new AlertDialog.Builder(activity);
        LayoutInflater factory = LayoutInflater.from(activity);
        final View view = factory.inflate(R.layout.qr_code_image, null);
        alertadd.setView(view);
        alertadd.setTitle("Share this application via QR code!");
        alertadd.setIcon(android.R.drawable.ic_menu_info_details);
        alertadd.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with go back
            }
        });

        alertadd.show();

    }




}