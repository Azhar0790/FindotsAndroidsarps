package com.knowall.findots.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.activities.DestinationAddMapActivity;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.adapters.DestinationsPagerAdapter;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class DestinationsTabFragment extends Fragment implements IGetDestinations {

    ViewPager viewPagerDestinations = null;
    TabLayout tabLayout = null;
    public static String current_selected_dateTime = "";

    MaterialCalendarView materialCalendarView = null;
    public static int pagerCurrentItem = 0;
    public static DestinationData[] destinationDatas = null;
    private static final int REQUEST_CODE_ADD_DESTINATION = 9999;

    public static DestinationsTabFragment newInstance() {
        DestinationsTabFragment destinationsTabFragment = new DestinationsTabFragment();
        return destinationsTabFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_tab_layout, null);

        initializeMaterialCalendarView(rootView);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayoutDestinations);
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("List"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        FloatingActionButton fabAddDestination = (FloatingActionButton) rootView.findViewById(R.id.fabAddDestination);
        fabAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinDotsApplication.getInstance().trackEvent("Destination", "Click", "Clicked Add Destination Event");
                // Click action
                pagerCurrentItem = viewPagerDestinations.getCurrentItem();
                Log.d("jomy", "pagerCurrentItem : " + pagerCurrentItem);
                Intent intent = new Intent(MenuActivity.ContextMenuActivity, DestinationAddMapActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DESTINATION);
            }
        });
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_color));
        tabLayout.setSelectedTabIndicatorHeight(8);
        tabLayout.setTabTextColors(getResources().getColor(R.color.app_color),
                getResources().getColor(R.color.app_color));

        ViewCompat.setElevation(tabLayout, 5f);

        viewPagerDestinations = (ViewPager) rootView.findViewById(R.id.viewPagerDestinations);

        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(MenuActivity.ContextMenuActivity);
        destinationsRestCall.delegate = DestinationsTabFragment.this;
        destinationsRestCall.callGetDestinations();

        viewPagerDestinations.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerDestinations.setCurrentItem(tab.getPosition());
                pagerCurrentItem = tab.getPosition();

                if (pagerCurrentItem == 2) {
                    if (mCalendarDay.isAfter(CalendarDay.today()))
                        EventBus.getDefault().post(AppEvents.NOHISTORY);
                    else
                        EventBus.getDefault().post(AppEvents.HISTORY);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        destinationDatas = destinationsModel.getDestinationData();

        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DateTime dateTime = new DateTime();
        current_selected_dateTime = dateTime.toString(fmt1);

        createScheduledUnscheduledListByDate(current_selected_dateTime);

        DestinationsPagerAdapter pagerAdapter = new DestinationsPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPagerDestinations.setAdapter(pagerAdapter);
        viewPagerDestinations.setCurrentItem(pagerCurrentItem);
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(MenuActivity.ContextMenuActivity, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "onResume: DestinationsTabFragment");
        FinDotsApplication.getInstance().trackScreenView("Destination Map & List Fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("jomy", "onActivityResult..//");
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_ADD_DESTINATION) {

            if (viewPagerDestinations != null && viewPagerDestinations.getChildCount() >= pagerCurrentItem) {
                viewPagerDestinations.setCurrentItem(pagerCurrentItem);
                Log.d("jomy", "set pagerCurrentItem..//" + pagerCurrentItem);
            }
            GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(MenuActivity.ContextMenuActivity);
            destinationsRestCall.delegate = DestinationsTabFragment.this;
            destinationsRestCall.callGetDestinations();

        } else if (resultCode == 3) {
            Log.d("jomy", "check...");
            GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(MenuActivity.ContextMenuActivity);
            destinationsRestCall.delegate = DestinationsTabFragment.this;
            destinationsRestCall.callGetDestinations();
        } else {
        }
    }

    public void initializeMaterialCalendarView(ViewGroup rootView) {
        materialCalendarView = (MaterialCalendarView) rootView.findViewById(R.id.materialCalendarView);
        materialCalendarViewSettings();
    }

    CalendarDay mCalendarDay = null;

    public void materialCalendarViewSettings() {
        materialCalendarView.setTileHeightDp(26);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        materialCalendarView.getShowOtherDates();

        setMaterialCalendarDate(CalendarDay.today().getYear(), CalendarDay.today().getMonth(), CalendarDay.today().getDay());
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                getSelectedDateFromCalendar(date.getDate());

                mCalendarDay = date;

                if (date.isAfter(CalendarDay.today())) {
                    EventBus.getDefault().post(AppEvents.NOHISTORY);
                } else {
                    if (pagerCurrentItem == 2) {
                        EventBus.getDefault().post(AppEvents.HISTORY);
                    }
                }

            }
        });


        /**
         *   current date selection
         */
        ArrayList<CalendarDay> dates = new ArrayList<>();
        dates.add(CalendarDay.today());
        materialCalendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.app_color), dates));

    }

    public void setMaterialCalendarDate(int year, int month, int day) {
        materialCalendarView.setSelectedDate(CalendarDay.from(year, month, day));
        materialCalendarView.setCurrentDate(CalendarDay.from(year, month, day));
        Log.d("jo", "Build version ..." + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > 22) {
            materialCalendarView.goToNext();
        }

    }

    class EventDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

    public void getSelectedDateFromCalendar(Date date) {
        // ------------------------------------
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        Date d1 = null;
        try {
            d1 = sdf3.parse(date.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // ------------------------------------
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);

        try {
            current_selected_dateTime = sdf4.format(d1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createScheduledUnscheduledListByDate(current_selected_dateTime);
    }

    public static void createScheduledUnscheduledListByDate(String dateTime) {


        String selectedDateFromCalendar = dateTime.substring(0, 10);

        String scheduleDate = null;

        int i = 0;

        for (DestinationData data : destinationDatas) {
            if (data.getScheduleDate().length() != 0) {
                scheduleDate = data.getScheduleDate().substring(0, 10);
                if (selectedDateFromCalendar.equals(scheduleDate)) {
                    destinationDatas[i].setScheduleDisplayStatus(true);
                } else {
                    destinationDatas[i].setScheduleDisplayStatus(false);
                }
            } else {
                destinationDatas[i].setScheduleDisplayStatus(true);
            }
            i++;
        }

        /**
         *   pass event to MapFragment or DestinationFragment
         */


        EventBus.getDefault().post(AppEvents.SCHEDULEDDATEMAP);
        EventBus.getDefault().post(AppEvents.SCHEDULEDDATELIST);

        //DestinationsPagerAdapter pagerAdapter = new DestinationsPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        //viewPagerDestinations.setAdapter(pagerAdapter);

    }


    public void onDestroy() {
        super.onDestroy();
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//        EventBus.getDefault().unregister(this);
    }

}
