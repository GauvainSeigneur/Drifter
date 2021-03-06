package io.drifterapp.drifter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import io.drifterapp.drifter.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Resources res;
    private boolean mShiftingMode=true;
    private RelativeLayout bottomNavParentlayout;
    private BottomNavigationView mBottomNavigationView;
    private FrameLayout bottomNavBackgroundContainer;
    private View constantBackground;
    private View revealBackground;
    private View revealFront;
    private boolean iscolorRevealBackground;
    private static final long ACTIVE_ANIMATION_DURATION_MS = 115L;
    private int totalNavItems;
    private int activeWidth;
    private int inactiveWidth;
    private int targetWidth;
    private int[] colorNumberarray;
    //for reveal color with shift mode
    private int currentItemSelected;
    private int previousItemSelected;
    private int revealItemPosition;
    private int revealFinalPosition;
    //choose type of colorRevealMode
    //Others Views
    private CoordinatorLayout parentLayout;
    //BottomSheet
    FrameLayout bottomPlayer;
    BottomSheetBehavior bottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        res = getResources();
        initViews();
        initBottomNavigationview(false,true,R.color.bottomNavColor_1);
        //redefinne height of bottom nav to set it under navigation bar
        //only if a soft navigation bar is available
        setBottomNavUnderNavigationbar();
        //set up click listner on bottom navigation menu
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        bottomPlayer = (FrameLayout) findViewById(R.id.bottom_player_sheet);
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomPlayer);
        // set the peek height
        bottomSheetBehavior.setPeekHeight((int) Utils.convertDpToPixel(152,MainActivity.this));
        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //disable dragging

                if (bottomSheetBehavior.getState()== BottomSheetBehavior.STATE_EXPANDED){
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setPeekHeight((int) Utils.convertDpToPixel(152,MainActivity.this));
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }

    /**********************************
     * Initialize Views
     *********************************/
    public void initViews() {
        parentLayout =(CoordinatorLayout) findViewById(R.id.container);
        bottomNavParentlayout = (RelativeLayout) findViewById(R.id.bottom_nav_parent_layout);
        bottomNavBackgroundContainer = (FrameLayout) findViewById(R.id.bottom_nav_background_container);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        constantBackground = findViewById(R.id.navigation_constant_background);
        revealBackground =findViewById(R.id.navigation_reveal_background);
        revealFront =findViewById(R.id.navigation_reveal_front);
    }

    /**********************************
     * Initialize BottomNav Behavior
     *********************************/
    public void initBottomNavigationview(boolean disableShiftMode,
                                         boolean makeRevealBackgroundAnimation,
                                         int constantBackgroundColor) {
        iscolorRevealBackground = makeRevealBackgroundAnimation;
        if (makeRevealBackgroundAnimation==true) {
            revealFront.setVisibility(View.VISIBLE);
            revealFront.setBackgroundColor(ContextCompat.getColor(this, constantBackgroundColor));
            revealBackground.setVisibility(View.VISIBLE);
            revealBackground.setBackgroundColor(ContextCompat.getColor(this,
                    constantBackgroundColor));
            mBottomNavigationView.setBackgroundColor(ContextCompat.getColor(this,
                    android.R.color.transparent));
            constantBackground.setVisibility(View.VISIBLE);
            constantBackground.setBackgroundColor(ContextCompat.getColor(this,
                    constantBackgroundColor));
        }else {
            constantBackground.setVisibility(View.GONE);
            revealFront.setVisibility(View.GONE);
            revealBackground.setVisibility(View.GONE);
            mBottomNavigationView.setBackgroundColor(ContextCompat.getColor(this,
                    constantBackgroundColor));
        }

        //find reveal position on initialization to avoid dummy
        // animation position on first item selection...
        findRevealPosition();

    }

    /*************************************
     * Handle revealColor and Menu state
     ************************************/
    public void retrieveMenuItemDimension() {
        final int mInactiveItemMaxWidth = res.getDimensionPixelSize(android.support.design.R.dimen.design_bottom_navigation_item_max_width);
        final int  mInactiveItemMinWidth = res.getDimensionPixelSize(android.support.design.R.dimen.design_bottom_navigation_item_min_width);
        final int mActiveItemMaxWidth = res.getDimensionPixelSize(android.support.design.R.dimen.design_bottom_navigation_active_item_max_width);
        final int inactiveCount = totalNavItems - 1;
        final int bottomNavWidth = mBottomNavigationView.getResources().getDisplayMetrics().widthPixels;
        //todo - comment
        totalNavItems = mBottomNavigationView.getMenu().size();
        //todo - comment
        final int activeMaxAvailable = bottomNavWidth - inactiveCount * mInactiveItemMinWidth;
        //todo - comment
        activeWidth = Math.min(activeMaxAvailable, mActiveItemMaxWidth);
        //todo - comment
        final int inactiveMaxAvailable = (bottomNavWidth - activeWidth) / inactiveCount;
        //todo - comment
        inactiveWidth = Math.min(inactiveMaxAvailable, mInactiveItemMaxWidth);
        //todo - comment
        targetWidth=inactiveWidth/2;
    }


    //Find the position of each item of the BottomNavigationView
    //Even if the shiftMode is activated
    public void findRevealPosition() {
        retrieveMenuItemDimension();
        //find position of selected item when shiftingmode is activated
        if (mShiftingMode==true && totalNavItems>3) {
            if (currentItemSelected == previousItemSelected) {
                revealItemPosition = activeWidth / 2;
                revealFinalPosition = revealItemPosition + (inactiveWidth * currentItemSelected);
            } else {
                revealItemPosition = inactiveWidth / 2;
                if (currentItemSelected < previousItemSelected) {
                    revealFinalPosition = revealItemPosition + (inactiveWidth
                            * currentItemSelected);
                } else if (currentItemSelected > previousItemSelected) {
                    revealFinalPosition = revealItemPosition + activeWidth + (inactiveWidth *
                            (currentItemSelected - 1));
                }

            }

        } else {
            //if shiftmode is disabled or number of item is under four,
            //the item are equally distribued inside menu
            //we use these method to find the center of each item
            int bottomNavWidth = (getResources().getDisplayMetrics().widthPixels);
            int bottomNavItemSize = ((bottomNavWidth/totalNavItems)); //find width of items
            double itemTargetCenter = (((bottomNavItemSize)*(currentItemSelected+1))
                    -(bottomNavItemSize/2)); //find center of selected item
            revealFinalPosition = (int) itemTargetCenter;
        }
    }

    //handle reveal color background on item click
    //Handle of if you use makeMultipleRevealMode or makeOneRevealBackground
    public void setRevealColorAnimationBackground(@Nullable int revalColorArray, final int pos){
        colorNumberarray = context.getResources().getIntArray(revalColorArray);

        if (iscolorRevealBackground && currentItemSelected != previousItemSelected) {
            mBottomNavigationView.setBackgroundColor(ContextCompat.getColor(this,
                    android.R.color.transparent));

            makeMultipleRevealMode(circularRevealAnimator(revealFront, targetWidth), pos);

        }

    }

    //Circular Reveal Animator
    private Animator circularRevealAnimator (View viewTarget, int startRevealRadius){
        findRevealPosition();
        float HeightrevealPosition = Utils.convertDpToPixel(56/2,this);
        Animator animator =
                ViewAnimationUtils.createCircularReveal(
                        viewTarget,
                        revealFinalPosition,
                        (int) HeightrevealPosition,
                        startRevealRadius,//0,
                        mBottomNavigationView.getWidth()
                );
        //duration : triple of icon animation duration
        animator.setDuration(ACTIVE_ANIMATION_DURATION_MS * 3);
        return animator;
    }

    //Used to make different color for each item thanks to a array of color hexa
    private void makeMultipleRevealMode(Animator animator,final int pos){
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                revealFront.setBackgroundColor(colorNumberarray[pos]);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                revealBackground.setBackgroundColor(colorNumberarray[pos]);
            }
        });

        animator.start();

    }

    //retrieve the menu state
    //used to define the previous selected item and the current
    private void updateBottomNavMenuState(@NonNull MenuItem item) {
        for (int i=0;i<mBottomNavigationView.getMenu().size();i++){
            if(item==mBottomNavigationView.getMenu().getItem(i)){
                previousItemSelected = currentItemSelected;
                currentItemSelected = i;
                break;
            }
        }
    }

    /***************************************************
     * OnClick Listener for BottomNavigation Menu Item
     ***************************************************/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            updateBottomNavMenuState(item);
            setRevealColorAnimationBackground(R.array.reveal_bottom_nav_bg, currentItemSelected);
            if (currentItemSelected!=previousItemSelected) {
                //do something if you want like focus on top
            }
            switch (item.getItemId()) {
                case R.id.navigation_movies_tv:
                    return true;
                case R.id.navigation_music:
                    return true;
                case R.id.navigation_book:
                    return true;
                case R.id.navigation_newsstand:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
            }

            return false;
        }

    };

    /**********************************
     * Window Support
     *********************************/
    //this methd is used in addition to the style "AppTheme.BottomNavigationActivit"
    // aplied on this activity in the manifest...
    public void setBottomNavUnderNavigationbar() {
        windowNoLimit();
        if (hasSoftNavBar(this)) {
            // 99% sure there's a navigation bar... does not work every time...
            // so we redefine the height of the bottom navigation View
            float newBottomNavHeight = Utils.convertDpToPixel(48+56,this);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                    bottomNavBackgroundContainer.getLayoutParams();
            params.height = (int) newBottomNavHeight;
            bottomNavBackgroundContainer.setLayoutParams(params);

        } else {
            //do nothing
        }
    }

    //check if the device has a soft navigationbar
    //to draw behinf if it has it
    boolean hasSoftNavBar(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // navigation bar was introduced in Android 4.0 (API level 14)
            Resources resources = context.getResources();
            int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                return resources.getBoolean(id);
            } else {
                // Check for keys
                boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
                boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
                return !hasMenuKey && !hasBackKey;
            }
        } else {
            return false;
        }
    }
    public void windowNoLimit () {
        //this value needs to be combined with style to works fine...
        parentLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

    }


}
