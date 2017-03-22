package com.knowall.findots.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.knowall.findots.restcalls.history.HistoryData;
import com.knowall.findots.restcalls.history.HistoryModel;
import com.knowall.findots.restcalls.history.HistoryRestCall;
import com.knowall.findots.restcalls.history.IHistory;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class DestinationsTabFragment extends Fragment implements IGetDestinations, IHistory {

    ViewPager viewPagerDestinations = null;
    TabLayout tabLayout = null;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static String current_selected_dateTime = "";
    private long lastClickTime = 0;
    public static MaterialCalendarView materialCalendarView = null;
    public static int pagerCurrentItem = 1;
    public static DestinationData[] destinationDatas = null;
    private static final int REQUEST_CODE_ADD_DESTINATION = 9999;
    public static ArrayList<HistoryData> historyDatas = new ArrayList<HistoryData>();
    boolean resetViewPager = false;
    FloatingActionButton floatingbtn_download = null;

    public static DestinationsTabFragment newInstance() {
        DestinationsTabFragment destinationsTabFragment = new DestinationsTabFragment();
        return destinationsTabFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
        resetViewPager = true;
        Log.d("paul", "oncreateTab..");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_tab_layout, null);

        initializeMaterialCalendarView(rootView);
        Log.d("paul", "oncreateViewTab..");
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_color, R.color.darkgreen, R.color.darkblue);
        mSwipeRefreshLayout.setEnabled(true);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayoutDestinations);
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Trips"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        floatingbtn_download = (FloatingActionButton) rootView.findViewById(R.id.floatingbtn_generate_pdf);


        FloatingActionButton fabAddDestination = (FloatingActionButton) rootView.findViewById(R.id.fabAddDestination);
        fabAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
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
        tabLayout.setScrollPosition(1, 0f, true);

        ViewCompat.setElevation(tabLayout, 5f);

        if (viewPagerDestinations == null)
            viewPagerDestinations = (ViewPager) rootView.findViewById(R.id.viewPagerDestinations);

        callDestinationRestOperation();

        viewPagerDestinations.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerDestinations.setCurrentItem(tab.getPosition());
                pagerCurrentItem = tab.getPosition();


                if (pagerCurrentItem == 2) {
                    mSwipeRefreshLayout.setEnabled(true);
                    floatingbtn_download.setVisibility(View.VISIBLE);
                    if (mCalendarDay != null && !(mCalendarDay.isAfter(CalendarDay.today()))) {
                        callHistoryRestCall();
                    } else {
                        historyDatas.clear();
                        EventBus.getDefault().post(AppEvents.NOHISTORY);
                    }
                } else if (pagerCurrentItem == 1) {
                    mSwipeRefreshLayout.setEnabled(true);
                    floatingbtn_download.setVisibility(View.GONE);
                    EventBus.getDefault().post(AppEvents.SCHEDULEDDATELIST);
                    Log.i("paul", "pagerCurrentItemEvent --> " + pagerCurrentItem);
                }
                else {
                    floatingbtn_download.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (pagerCurrentItem == 2) {
                    floatingbtn_download.setVisibility(View.VISIBLE);
                } else {
                    floatingbtn_download.setVisibility(View.GONE);
                }

                if (pagerCurrentItem == 2) {
                    if (mCalendarDay != null && !(mCalendarDay.isAfter(CalendarDay.today())))
                        callHistoryRestCall();
                } else {
                    callDestinationRestOperation();
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }

        });
        return rootView;
    }

    void callDestinationRestOperation() {
        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(MenuActivity.ContextMenuActivity);
        destinationsRestCall.delegate = DestinationsTabFragment.this;
        destinationsRestCall.callGetDestinations();

    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        destinationDatas = destinationsModel.getDestinationData();

        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = new DateTime();

        if (current_selected_dateTime == null || current_selected_dateTime.length() < 1) {
            current_selected_dateTime = dateTime.toString(fmt1);
            Log.d("paul", "current_selected_dateTime22" + current_selected_dateTime);
        }

        createScheduledUnscheduledListByDate(current_selected_dateTime);

        /**
         *   Tabs adapter
         */
        if (viewPagerDestinations != null && resetViewPager == true) {
            DestinationsPagerAdapter pagerAdapter = new DestinationsPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
            viewPagerDestinations.setAdapter(pagerAdapter);
            viewPagerDestinations.setOffscreenPageLimit(tabLayout.getTabCount());
            resetViewPager = false;
        }
        viewPagerDestinations.setCurrentItem(pagerCurrentItem);

        if (pagerCurrentItem == 2) {
            if (mCalendarDay != null && !(mCalendarDay.isAfter(CalendarDay.today())))
                callHistoryRestCall();
        }
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

            callDestinationRestOperation();

        } else if (resultCode == 3) {
            Log.d("jomy", "check...");
            callDestinationRestOperation();
        } else {
        }
    }

    public void initializeMaterialCalendarView(ViewGroup rootView) {
        materialCalendarView = (MaterialCalendarView) rootView.findViewById(R.id.materialCalendarView);
        materialCalendarViewSettings();
    }

    public static CalendarDay mCalendarDay = null;

    public void materialCalendarViewSettings() {

        int calendarViewHeight = (int) getActivity().getResources().getDimension(R.dimen.calendar_view_height);
        materialCalendarView.setTileHeightDp(calendarViewHeight);
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

                if (pagerCurrentItem == 2) {
                    if (mCalendarDay != null && !(mCalendarDay.isAfter(CalendarDay.today()))) {
                        callHistoryRestCall();
                    } else {
                        historyDatas.clear();
                        EventBus.getDefault().post(AppEvents.NOHISTORY);
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
        mCalendarDay = materialCalendarView.getSelectedDate();
        Log.d("jo", "Build version ..." + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > 22) {
            materialCalendarView.goToNext();
        }

//        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
//
//        Date d1 = null;
//        try {
//            d1 = sdf3.parse(mCalendarDay.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        // ------------------------------------
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);

        try {
            current_selected_dateTime = sdf4.format(mCalendarDay.getDate());
            Log.d("paul", "current_selected_dateTime1" + current_selected_dateTime);
        } catch (Exception e) {
            Log.d("paul", "err : " + e.getMessage());
            e.printStackTrace();
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
            Log.d("paul", "current_selected_dateTimekali" + current_selected_dateTime);
        } catch (Exception e) {
            Log.d("paul", "err : " + e.getMessage());
            e.printStackTrace();
        }

        createScheduledUnscheduledListByDate(current_selected_dateTime);
    }

    public static void createScheduledUnscheduledListByDate(String dateTime) {


        String selectedDateFromCalendar = dateTime.substring(0, 10);

        String scheduleDate = null;

        int i = 0;

        if (destinationDatas != null) {
            for (DestinationData data : destinationDatas) {
                if (data.getScheduleDate() != null && data.getScheduleDate().length() != 0) {
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
        //pagerCurrentItem = 0;
        current_selected_dateTime = "";
        MaterialCalendarView materialCalendarView = null;
        pagerCurrentItem = 1;
        DestinationData[] destinationDatas = null;
        ArrayList<HistoryData> historyDatas = null;
        mCalendarDay = null;

//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//        EventBus.getDefault().unregister(this);
    }

    public void callHistoryRestCall() {
        String startDate = "", endDate = "";

        String date = DestinationsTabFragment.current_selected_dateTime;
        if (!date.equals("")) {
            startDate = date.substring(0, 10) + " " + "00:00:00";
            endDate = date.substring(0, 10) + " " + "23:59:59";
        }
        HistoryRestCall historyRestCall = new HistoryRestCall(MenuActivity.ContextMenuActivity);
        historyRestCall.delegate = DestinationsTabFragment.this;
        historyRestCall.callGetReport(startDate, endDate);
    }

    @Override
    public void onHistorySuccess(HistoryModel historyModel) {
        if (historyModel.getHistoryData().size() > 0) {
            historyDatas.clear();
            Log.d("paul", "historySucess...");
            historyDatas.addAll(historyModel.getHistoryData());
            EventBus.getDefault().post(AppEvents.HISTORY);
        } else {
            historyDatas.clear();
            EventBus.getDefault().post(AppEvents.NOHISTORY);
        }
    }

    @Override
    public void onHistoryFailure(String errorMessage) {
        historyDatas.clear();
        EventBus.getDefault().post(AppEvents.NOHISTORY);
    }
}
