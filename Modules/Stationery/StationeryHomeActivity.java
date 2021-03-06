package com.prism.pickany247.Modules.Stationery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.prism.pickany247.Adapters.CatageoryAdapter;
import com.prism.pickany247.Adapters.ProductAadpter;
import com.prism.pickany247.Apis.Api;
import com.prism.pickany247.CartActivity;
import com.prism.pickany247.Helper.Converter;
import com.prism.pickany247.Helper.PrefManager;
import com.prism.pickany247.HomeActivity;
import com.prism.pickany247.ProductDetailsActivity;
import com.prism.pickany247.ProductListActivity;
import com.prism.pickany247.R;
import com.prism.pickany247.Response.CatResponse;
import com.prism.pickany247.Response.ProductResponse;
import com.prism.pickany247.Singleton.AppController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class StationeryHomeActivity extends AppCompatActivity {


    AppController appController;
    @BindView(R.id.catRecycler)
    RecyclerView catRecycler;
    @BindView(R.id.banner_slider1)
    BannerSlider bannerSlider1;
    @BindView(R.id.txtArtTitle)
    TextView txtArtTitle;
    @BindView(R.id.artViewAll)
    TextView artViewAll;
    @BindView(R.id.artRecycler)
    RecyclerView artRecycler;
    @BindView(R.id.txtDeskTitle)
    TextView txtDeskTitle;
    @BindView(R.id.deskViewAll)
    TextView deskViewAll;
    @BindView(R.id.deskRecycler)
    RecyclerView deskRecycler;
    @BindView(R.id.txtOfficeTitle)
    TextView txtOfficeTitle;
    @BindView(R.id.officeViewAll)
    TextView officeViewAll;
    @BindView(R.id.officeRecycler)
    RecyclerView officeRecycler;
    @BindView(R.id.txtSchoolTitle)
    TextView txtSchoolTitle;
    @BindView(R.id.schoolViewAll)
    TextView schoolViewAll;
    @BindView(R.id.schoolRecycler)
    RecyclerView schoolRecycler;
    @BindView(R.id.txtExtraTitle)
    TextView txtExtraTitle;
    @BindView(R.id.extraViewAll)
    TextView extraViewAll;
    @BindView(R.id.extraRecycler)
    RecyclerView extraRecycler;
    @BindView(R.id.simpleSwipeRefreshLayout)
    SwipeRefreshLayout simpleSwipeRefreshLayout;

    private CatageoryAdapter adapter;
    private ProductAadpter stationeryHomeAdapter;
    CatResponse homeResponse = new CatResponse();
    ProductResponse productResponse = new ProductResponse();
    Gson gson;
    private PrefManager pref;
    String userid;
    int  cartindex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationery_home);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Stationery");

        appController = (AppController) getApplication();

        pref = new PrefManager(getApplicationContext());

        // Displaying user information from shared preferences
        HashMap<String, String> profile = pref.getUserDetails();
        userid = profile.get("id");

        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorBlue, R.color.colorPrimary);
        appController = (AppController) getApplicationContext();
        if (appController.isConnection()) {

            prepareHomeData();

            // cart count
            appController.cartCount(userid);
            SharedPreferences preferences =getSharedPreferences("CARTCOUNT",0);
            cartindex =preferences.getInt("itemCount",0);
            Log.e("cartindex",""+cartindex);
            invalidateOptionsMenu();


            simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    prepareHomeData();

                    simpleSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } else {

            setContentView(R.layout.internet);
            Button tryButton = (Button) findViewById(R.id.btnTryagain);
            tryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });


        }


    }

    private void prepareHomeData() {

        simpleSwipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.STATIONERY_HOME_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", "" + response);
                simpleSwipeRefreshLayout.setRefreshing(false);

                gson = new Gson();
                homeResponse = gson.fromJson(response, CatResponse.class);

                for (CatResponse.CategoriesBean mainCategoriesBean : homeResponse.getCategories()) {

                    if (mainCategoriesBean.getId().equals("1") || mainCategoriesBean.getCategory_name().equalsIgnoreCase("Art & Craft")){

                        prepareArtData(mainCategoriesBean.getId(),mainCategoriesBean.getCategory_name(),mainCategoriesBean.getModule());

                        }
                    else if (mainCategoriesBean.getId().equals("2") || mainCategoriesBean.getCategory_name().equalsIgnoreCase("Desk Organizers")){

                        prepareDeskData(mainCategoriesBean.getId(),mainCategoriesBean.getCategory_name(),mainCategoriesBean.getModule());

                    }
                    else if (mainCategoriesBean.getId().equals("3") || mainCategoriesBean.getCategory_name().equalsIgnoreCase("Office Stationery")){

                        prepareOfficeData(mainCategoriesBean.getId(),mainCategoriesBean.getCategory_name(),mainCategoriesBean.getModule());

                    }
                    else if (mainCategoriesBean.getId().equals("4") || mainCategoriesBean.getCategory_name().equalsIgnoreCase("School & College")){

                        prepareSchoolData(mainCategoriesBean.getId(),mainCategoriesBean.getCategory_name(),mainCategoriesBean.getModule());

                    }
                    else if (mainCategoriesBean.getId().equals("5") || mainCategoriesBean.getCategory_name().equalsIgnoreCase("Extra Stationery")){

                        prepareExtraData(mainCategoriesBean.getId(),mainCategoriesBean.getCategory_name(),mainCategoriesBean.getModule());

                    }

                }

                // home Adapter
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                catRecycler.setLayoutManager(mLayoutManager);
                catRecycler.setItemAnimator(new DefaultItemAnimator());

                adapter = new CatageoryAdapter(getApplicationContext(), homeResponse.getCategories());
                catRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                // home banners
                List<Banner> banners = new ArrayList<>();
                banners.clear();
                Log.e("BANNERS", "" + banners.size());
                for (final CatResponse.BannersBean homeBannersBean : homeResponse.getBanners()) {

                    banners.add(new RemoteBanner(homeBannersBean.getImage()));

                }

                BannerSlider bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);

                //add banner using image url
                // banners.add(new RemoteBanner("Put banner image url here ..."));
                //add banner using resource drawable
                bannerSlider.setBanners(banners);
                bannerSlider.onIndicatorSizeChange();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleSwipeRefreshLayout.setRefreshing(true);
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Oops. Server error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Oops. AuthFailure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Oops. Parse error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Oops. Connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
       /* RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);*/


    }

    private void prepareArtData(final String id, final String catname,final String module) {

        simpleSwipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.STATIONERY_URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", "" + response);
                simpleSwipeRefreshLayout.setRefreshing(false);

                gson = new Gson();
                productResponse = gson.fromJson(response, ProductResponse.class);

                // homeKitchen Adapter
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                artRecycler.setLayoutManager(mLayoutManager);
                artRecycler.setItemAnimator(new DefaultItemAnimator());

                stationeryHomeAdapter = new ProductAadpter(getApplicationContext(), productResponse.getFiltered_products());
                artRecycler.setAdapter(stationeryHomeAdapter);
                stationeryHomeAdapter.notifyDataSetChanged();

                // textview
                txtArtTitle.setText(catname);
                artViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent1 = new Intent(getApplicationContext(), ProductListActivity.class);
                        intent1.putExtra("catId", id);
                        intent1.putExtra("title", catname);
                        intent1.putExtra("module", module);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Oops. Server error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Oops. AuthFailure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Oops. Parse error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Oops. Connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

    private void prepareDeskData(final String id, final String catname,final String module) {

        simpleSwipeRefreshLayout.setRefreshing(false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.STATIONERY_URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", "" + response);
                simpleSwipeRefreshLayout.setRefreshing(false);

                gson = new Gson();
                productResponse = gson.fromJson(response, ProductResponse.class);

                // homeKitchen Adapter
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                deskRecycler.setLayoutManager(mLayoutManager);
                deskRecycler.setItemAnimator(new DefaultItemAnimator());

                stationeryHomeAdapter = new ProductAadpter(getApplicationContext(), productResponse.getFiltered_products());
                deskRecycler.setAdapter(stationeryHomeAdapter);
                stationeryHomeAdapter.notifyDataSetChanged();


                // textview
                txtDeskTitle.setText(catname);
                deskViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent1 = new Intent(getApplicationContext(), ProductListActivity.class);
                        intent1.putExtra("catId", id);
                        intent1.putExtra("title", catname);
                        intent1.putExtra("module", module);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Oops. Server error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Oops. AuthFailure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Oops. Parse error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Oops. Connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

    private void prepareOfficeData(final String id, final String catname,final String module) {

        simpleSwipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.STATIONERY_URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", "" + response);
                simpleSwipeRefreshLayout.setRefreshing(false);

                gson = new Gson();
                productResponse = gson.fromJson(response, ProductResponse.class);

                // homeKitchen Adapter
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                officeRecycler.setLayoutManager(mLayoutManager);
                officeRecycler.setItemAnimator(new DefaultItemAnimator());

                stationeryHomeAdapter = new ProductAadpter(getApplicationContext(), productResponse.getFiltered_products());
                officeRecycler.setAdapter(stationeryHomeAdapter);
                stationeryHomeAdapter.notifyDataSetChanged();

                // textview
                txtOfficeTitle.setText(catname);
                officeViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent1 = new Intent(getApplicationContext(), ProductListActivity.class);
                        intent1.putExtra("catId", id);
                        intent1.putExtra("title", catname);
                        intent1.putExtra("module", module);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Oops. Server error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Oops. AuthFailure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Oops. Parse error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Oops. Connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

    private void prepareSchoolData(final String id, final String catname,final String module) {

        simpleSwipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.STATIONERY_URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", "" + response);
                simpleSwipeRefreshLayout.setRefreshing(false);

                gson = new Gson();
                productResponse = gson.fromJson(response, ProductResponse.class);

                // homeKitchen Adapter
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                schoolRecycler.setLayoutManager(mLayoutManager);
                schoolRecycler.setItemAnimator(new DefaultItemAnimator());

                stationeryHomeAdapter = new ProductAadpter(getApplicationContext(), productResponse.getFiltered_products());
                schoolRecycler.setAdapter(stationeryHomeAdapter);
                stationeryHomeAdapter.notifyDataSetChanged();

                // textview
                txtSchoolTitle.setText(catname);
                schoolViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent1 = new Intent(getApplicationContext(), ProductListActivity.class);
                        intent1.putExtra("catId", id);
                        intent1.putExtra("title", catname);
                        intent1.putExtra("module", module);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Oops. Server error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Oops. AuthFailure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Oops. Parse error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Oops. Connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

    private void prepareExtraData(final String id, final String catname,final String module) {

        simpleSwipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.STATIONERY_URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("RESPONSE", "" + response);
                simpleSwipeRefreshLayout.setRefreshing(false);

                gson = new Gson();
                productResponse = gson.fromJson(response, ProductResponse.class);

                // homeKitchen Adapter
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                extraRecycler.setLayoutManager(mLayoutManager);
                extraRecycler.setItemAnimator(new DefaultItemAnimator());

                stationeryHomeAdapter = new ProductAadpter(getApplicationContext(), productResponse.getFiltered_products());
                extraRecycler.setAdapter(stationeryHomeAdapter);
                stationeryHomeAdapter.notifyDataSetChanged();

                // textview
                txtExtraTitle.setText(catname);
                extraViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent1 = new Intent(getApplicationContext(), ProductListActivity.class);
                        intent1.putExtra("catId", id);
                        intent1.putExtra("title", catname);
                        intent1.putExtra("module", module);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Oops. Server error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Oops. AuthFailure error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Oops. Parse error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Oops. Connection error!", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Oops. Timeout error!", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }


    @Override
    protected void onRestart() {

        appController.cartCount(userid);
        SharedPreferences preferences =getSharedPreferences("CARTCOUNT",0);
        cartindex =preferences.getInt("itemCount",0);
        Log.e("cartindexonstart",""+cartindex);
        invalidateOptionsMenu();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        appController.cartCount(userid);
        SharedPreferences preferences =getSharedPreferences("CARTCOUNT",0);
        cartindex =preferences.getInt("itemCount",0);
        Log.e("cartindexonstart",""+cartindex);
        invalidateOptionsMenu();
        super.onStart();
    }
///
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.actionbar_menu, menu);
    final MenuItem menuItem = menu.findItem(R.id.action_cart);
    menuItem.setIcon(Converter.convertLayoutToImage(StationeryHomeActivity.this,cartindex,R.drawable.ic_actionbar_bag));
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_cart:
                Intent intent =new Intent(getApplicationContext(),CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.action_search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }




}
