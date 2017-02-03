package com.express56.xq.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.adapter.ExpressListViewAdapter;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.fragment.DatePickerFragment;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.ExpressInfo;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.DateUtil;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import java.util.ArrayList;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private ImageView btn_search;

    private EditText editText_SearchKey;

    private ArrayList<String> typeNames = new ArrayList<>();

    /**
     * 查询关键字
     */
    private String searchKeyStr;

    private ArrayList<ExpressInfo> expressInfos = new ArrayList<ExpressInfo>();

    private ExpressListViewAdapter expressListViewAdapter;

    private ListView listView;

    /**
     * 查询结果索引
     */
    private int pageIndex;

    private int expressType = ExpressConstant.EXPRESS_TYPE_ALL;

    private int lastItem;

    /**
     * 本次查询结果已经全部显示，不在自动请求更多
     */
    private boolean noMoreData;
    private TextView textView_footer;

    private ImageView btn_clear_startDate;
    private ImageView btn_clear_endDate;

    private EditText editText_startDate;
    private EditText editText_endDate;

    //    private CheckBox checkBox_receiver;
//    private CheckBox checkBox_sender;

    private EditText editText_phone;

    private Spinner mySpinner;

    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        btn_search = (ImageView) findViewById(R.id.btn_express_search);
        btn_search.setOnClickListener(this);

        btn_clear_startDate = (ImageView) findViewById(R.id.search_startdate_btn_clear);
        btn_clear_startDate.setOnClickListener(this);

        btn_clear_endDate = (ImageView) findViewById(R.id.search_enddate_btn_clear);
        btn_clear_endDate.setOnClickListener(this);

        editText_startDate = (EditText) findViewById(R.id.search_editText_startdate);
        editText_startDate.setOnClickListener(this);

        editText_endDate = (EditText) findViewById(R.id.search_editText_enddate);
        editText_endDate.setOnClickListener(this);

        addSpinner();

//        checkBox_sender = (CheckBox) findViewById(R.id.checkbox_sender);
//        checkBox_sender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    if (expressType == ExpressConstant.EXPRESS_TYPE_RECEIVE) {
//                        expressType = ExpressConstant.EXPRESS_TYPE_ALL;
//                    } else {
//                        expressType = ExpressConstant.EXPRESS_TYPE_SEND;
//                    }
//                } else {
//                    if (expressType == ExpressConstant.EXPRESS_TYPE_ALL) {
//                        if (checkBox_receiver.isChecked()) {
//                            expressType = ExpressConstant.EXPRESS_TYPE_RECEIVE;
//                        }
//                    } else {
//                        expressType = ExpressConstant.EXPRESS_TYPE_ALL;
//                    }
//                }
//            }
//        });
//
//        checkBox_receiver = (CheckBox) findViewById(R.id.checkbox_receiver);
//        checkBox_receiver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    if (expressType == ExpressConstant.EXPRESS_TYPE_SEND) {
//                        expressType = ExpressConstant.EXPRESS_TYPE_ALL;
//                    } else {
//                        expressType = ExpressConstant.EXPRESS_TYPE_RECEIVE;
//                    }
//                } else {
//                    if (expressType == ExpressConstant.EXPRESS_TYPE_ALL) {
//                        if (checkBox_sender.isChecked()) {
//                            expressType = ExpressConstant.EXPRESS_TYPE_SEND;
//                        }
//                    } else {
//                        expressType = ExpressConstant.EXPRESS_TYPE_ALL;
//                    }
//                }
//            }
//        });

        editText_phone = (EditText) findViewById(R.id.editText_search_by_phone);

        findViewById(R.id.search_back_btn).setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listView_express_result);
        LayoutInflater inflater = LayoutInflater.from(this);
        View footView = inflater.inflate(R.layout.express_listview_footer, null);
        textView_footer = (TextView) footView.findViewById(R.id.listView_footer);
        //listview的addFooterView()添加view到listview底部一定要加在listview.setAdapter(adapter);这代码前面
        listView.addFooterView(footView);

        editText_SearchKey = (EditText) findViewById(R.id.editText_search_key);

    }

    private void addSpinner() {
        typeNames.add("全部");
        typeNames.add("寄件");
        typeNames.add("签收");
        mySpinner = (Spinner) findViewById(R.id.search_type_spinner);
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeNames);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
//                myTextView.setText("您选择的是："+ adapter.getItem(arg2));
                if (arg2 == 0) {
                    expressType = ExpressConstant.EXPRESS_TYPE_ALL;
                } else if (arg2 == 1) {
                    expressType = ExpressConstant.EXPRESS_TYPE_SEND;
                } else if (arg2 == 2) {
                    expressType = ExpressConstant.EXPRESS_TYPE_RECEIVE;
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

    }

    @Override
    protected void initData() {
        super.initData();

        resetStartDate();
        resetEndDate();

        pageIndex = 1;

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        loadMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (checkFastClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.search_startdate_btn_clear:
                clearStartDate();
                break;
            case R.id.search_enddate_btn_clear:
                clearEndDate();
                break;
            case R.id.search_editText_startdate:
                showStartDatePickerDialog();
                break;
            case R.id.search_editText_enddate:
                showEndDatePickerDialog();
                break;
            case R.id.search_back_btn:
                finish();
                break;
            case R.id.btn_express_search:
                String startDate = editText_startDate.getText().toString().trim();
                String endDate = editText_endDate.getText().toString().trim();
                String key = editText_SearchKey.getText().toString().trim();
                String phoneEnd = editText_phone.getText().toString().trim();
//                if (StringUtils.isEmpty(key)) {
//                    ToastUtil.showMessage(this, "请输入你要查询的内容");
//                    return;
//                }
                doSearch(key, startDate, endDate, phoneEnd, expressType);
                break;
        }
    }

    private void resetEndDate() {
        editText_endDate.setText(DateUtil.getDay(System.currentTimeMillis()));
        btn_clear_endDate.setVisibility(View.VISIBLE);
    }

    private void resetStartDate() {
        editText_startDate.setText(DateUtil.getDay(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2));
        btn_clear_startDate.setVisibility(View.VISIBLE);
    }

    private void clearEndDate() {
        editText_endDate.setText("");
        btn_clear_endDate.setVisibility(View.GONE);
    }

    private void clearStartDate() {
        editText_startDate.setText("");
        btn_clear_startDate.setVisibility(View.GONE);
    }

    private void loadMoreData() {
        if (!noMoreData) {
            String startDate = editText_startDate.getText().toString().trim();
            String endDate = editText_endDate.getText().toString().trim();
            searchKeyStr = editText_SearchKey.getText().toString().trim();
            String phoneEnd = editText_phone.getText().toString().trim();
            HttpHelper.sendRequest_searchExpress(this, RequestID.REQ_SEARCH, user.token,
                    searchKeyStr, startDate, endDate, phoneEnd, expressType, pageIndex, null);
        }
    }

    public void showStartDatePickerDialog() {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setContext(this);
        datePicker.setLatestDate(DateUtil.str2Date(editText_startDate.getText().toString(), DateUtil.FORMAT_DATE));
        datePicker.setDateType(ExpressConstant.SEARCH_START_DATE);
        datePicker.setEditText(editText_startDate);
        datePicker.setDate(DateUtil.str2Date(editText_endDate.getText().toString(), DateUtil.FORMAT_DATE));
        datePicker.setView(btn_clear_startDate);
        datePicker.show(getFragmentManager(), "请选择开始日期");

    }

    public void showEndDatePickerDialog() {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setContext(this);
        datePicker.setLatestDate(DateUtil.str2Date(editText_endDate.getText().toString(), DateUtil.FORMAT_DATE));
        datePicker.setDateType(ExpressConstant.SEARCH_END_DATE);
        datePicker.setEditText(editText_endDate);
        datePicker.setDate(DateUtil.str2Date(editText_startDate.getText().toString(), DateUtil.FORMAT_DATE));
        datePicker.setView(btn_clear_endDate);
        datePicker.show(getFragmentManager(), "请选择结束日期");

    }


    /**
     * 显示列表
     */
    private void showResultList() {
        if (expressListViewAdapter == null) {
            float imgShowWidth = DisplayUtil.screenWidth - (listView.getPaddingLeft() + listView.getPaddingRight());
            expressListViewAdapter = new ExpressListViewAdapter(this, expressInfos, imgShowWidth);
            listView.setAdapter(expressListViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                        long id) {
                    ExpressInfo expressInfo = expressInfos.get(position - 1);
//                    if (expressInfo != null) {
//                        Object object = arg1.getTag(R.id.tag_imageview);
//                        if (object instanceof ImageView) {
//                            Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(((ImageView) object).getDrawable());
//                            if (bitmap == null) return;
//                            startActivity(new Intent(context, ZoomImageActivity.class).putExtra("bitmap", bitmap));
//                        }
//                    }
                }

            });
        } else {
            expressListViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 点击查询按钮调用
     *
     * @param number
     */
    private void doSearch(String number, String startDate, String endDate, String code, int expressType) {
        pageIndex = 1;
        noMoreData = false;
        searchKeyStr = number;
        expressInfos.clear();
        if (expressListViewAdapter != null) {
            expressListViewAdapter.notifyDataSetChanged();
        }
        HttpHelper.sendRequest_searchExpress(this, RequestID.REQ_SEARCH, user.token,
                searchKeyStr, startDate, endDate, code, expressType, pageIndex, dialog);
    }

    @Override
    public void doHttpResponse(Object... param) {
        if (this.isFinishing()) {
            return;
        }
        if (param[1] != null
                && param[0] != null
                && param[0] instanceof Bitmap
                && (Integer.parseInt(param[1].toString()) == RequestID.REQ_DOWNLOAD_PICTURE)) {
            return;
        }
        String result = (String) param[0];
        LogUtil.d(TAG, "response str=" + result);
        if (result == null) {
            String errMsg = getString(R.string.str_net_request_fail);
//            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(this, result)) {
            finish();
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            ToastUtil.showMessage(this, "返回数据异常", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_SEARCH:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
//                        ToastUtil.showMessage(this, "search->" + object.toString());
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            ArrayList<ExpressInfo> tempData = (ArrayList<ExpressInfo>) JSONArray.parseArray(content, ExpressInfo.class);
                            if (pageIndex == 1) {//第一次请求
                                if (expressInfos != null) {
                                    expressInfos.clear();
                                }
                                if (tempData.size() == 0) {
                                    textView_footer.setText("没有相关记录");
                                }
                                expressInfos.addAll(tempData);
                            } else {//加载更多
                                if (expressInfos != null && tempData != null && tempData.size() > 0) {
                                    expressInfos.addAll(tempData);
                                }
                            }
                            if (tempData != null && tempData.size() > 0) {
                                pageIndex += 1;
                                showResultList();
                            } else {
                                if (pageIndex > 1) {
                                    textView_footer.setText("已全部加载");
                                    ToastUtil.showMessage(this, "没有更多数据");
                                } else {
                                    ToastUtil.showMessage(this, "没有相关记录");
                                }
                                noMoreData = true;
                            }
                        }
                    } else if (code == 0) {
                        //token 过期
                        showReloginDialog();
                    } else {
                        showErrorMsg(object);
                    }
                }
                break;
            case RequestID.REQ_LOGOUT:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            sp.remove("userInfo");
                            ToastUtil.showMessage(this, "退出登录成功");
                            startActivity(new Intent(this, LoginActivity.class));
                        }
                    } else {
                        sp.remove("userInfo");
                        startActivity(new Intent(this, LoginActivity.class));
                    }
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
