package com.express56.xq.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.express56.xq.R;
import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.adapter.AreaAdapter;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.IHttpResponse;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.AreaInfo;
import com.express56.xq.util.SharedPreUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alibaba.fastjson.JSON;
import alibaba.fastjson.JSONArray;
import alibaba.fastjson.JSONObject;


/**
 * Created by bojoy-sdk2 on 17/2/6.
 */

public class ChoosePlaceLayout extends LinearLayout implements IHttpResponse {

    private Context context = null;
    private LinearLayout llContent = null;
    private RecyclerView rvList = null;
    private ArrayList<AreaInfo> infos = null;
    private AreaAdapter adapter = null;
    private String tag = "";
    private boolean isShow = false;

    private ChooseListener listener = null;

    private String parentId = "";
    private ArrayList<String> chooseAreaId = new ArrayList<>();
    private ArrayList<String> originalAreaId = new ArrayList<>();
    private ArrayList<String> chooseAreaName = new ArrayList<>();
    private ArrayList<String> originalAreaName = new ArrayList<>();
    private List<AreaInfo> selectedArea = null;
    private SharedPreUtils sp = null;
    private Dialog dialog = null;

    private ChoosePlaceItemLayout item = null;

    public ChoosePlaceLayout(Context context) {
        this(context, null);
    }

    public ChoosePlaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_choose_place, this, false);
        addView(view);

        initView(context);
    }

    private void initView(final Context context) {
        this.context = context;

        llContent = (LinearLayout) findViewById(R.id.ll_choose_place);
        rvList = (RecyclerView) findViewById(R.id.rv_choose_place);

        infos = new ArrayList<>();

        adapter = new AreaAdapter(context, infos, new AreaAdapter.AreaAdapterListener() {
            @Override
            public void choose(AreaInfo info) {
                item.setAreaInfo(info);
                chooseAreaId.add(info.id);
                chooseAreaName.add(info.name);

                HttpHelper.sendRequest_getArea(ChoosePlaceLayout.this, getContext(), RequestID.REQ_GET_AREA, info.id, sp.getUserInfo().token, dialog);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(adapter);
    }

    private ChoosePlaceItemLayout createItem(Context context, int... index) {
        item = new ChoosePlaceItemLayout(context);
        if (index.length > 0) {
            item.setIndex(index[0]);
        } else {
            item.setIndex(llContent.getChildCount());
        }
        item.setListener(new ChoosePlaceItemLayout.ChoosePlaceItemListener() {
            @Override
            public void choose(List<AreaInfo> listAreaInfos, int index) {
                llContent.removeViews(index + 1, llContent.getChildCount() - (index + 1));
                while (chooseAreaId.size() != index) {
                    chooseAreaId.remove(index);
                    chooseAreaName.remove(index);
                }
                item = (ChoosePlaceItemLayout) llContent.getChildAt(llContent.getChildCount() - 1);
                item.selected();
                infos.clear();
                infos.addAll(listAreaInfos);
                adapter.notifyDataSetChanged();
            }
        });
        return item;
    }

    public void show(String areaCode, Dialog dialog, String tag) {
        isShow = true;
        this.dialog = dialog;
        this.tag = tag;
        setVisibility(VISIBLE);

        if (sp == null) sp = new SharedPreUtils(getContext());
        HttpHelper.sendRequest_editArea(this, getContext(), RequestID.REQ_GET_AREA_EDIT, areaCode, sp.getUserInfo().token, dialog);

    }

    public void hide() {
        isShow = false;
        reset();
        setVisibility(GONE);
    }

    @Override
    public void doHttpResponse(Object... param) {
        String result = (String) param[0];
        if (result == null) {
            return;
        }
        if (InvokeStaticMethod.isNotJSONstring(getContext(), result)) {
            return;
        }
        JSONObject object = JSON.parseObject(result);
        if (object == null) {
            ToastUtil.showMessage(getContext(), "返回数据异常", false);
            return;
        }
        switch (Integer.parseInt(param[1].toString())) {
            case RequestID.REQ_GET_AREA_EDIT:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            Map<String, Object> map = JSON.parseObject(content, Map.class);
                            JSONObject json = (JSONObject) map.get("areas");
                            try {
                                org.json.JSONObject obj = new org.json.JSONObject(json.toJSONString());
                                for (Iterator<String> iterator = obj.keys(); iterator.hasNext(); ) {
                                    String key = iterator.next();
                                    parentId = key;
                                    List<AreaInfo> list = JSONArray.parseArray(obj.getString(key), AreaInfo.class);
                                    item = createItem(context);
                                    llContent.addView(item);
                                    item.setListAreaInfos(list);
                                    infos.clear();
                                    infos.addAll(list);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            JSONArray str = (JSONArray) map.get("selectedAreas");
                            selectedArea = JSONArray.parseArray(str.toJSONString(), AreaInfo.class);
                            for (int i = 0; i < llContent.getChildCount(); i++) {
                                ChoosePlaceItemLayout layout = (ChoosePlaceItemLayout) llContent.getChildAt(i);
                                if (selectedArea.size() > 0) {
                                    layout.setAreaInfo(selectedArea.get(i));
                                    layout.reset();
                                    chooseAreaId.add(selectedArea.get(i).id);
                                    chooseAreaName.add(selectedArea.get(i).name);
                                    if (i == llContent.getChildCount() - 1) {
                                        layout.selected(selectedArea.get(i).name);
                                        chooseAreaId.remove(i);
                                        chooseAreaName.remove(i);
                                        for (int j = 0; j < infos.size(); j++) {
                                            if (selectedArea.get(i).name.equals(infos.get(j).name)) {
                                                infos.get(j).selected = true;
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    layout.selected();
                                }
                            }
                        }
                    }
                }
                break;
            case RequestID.REQ_GET_AREA:
                if (object.containsKey("code")) {
                    int code = object.getIntValue("code");
                    if (code == 9) {
                        if (object != null && object.containsKey("result")) {
                            String content = object.getString("result");
                            List<AreaInfo> list = JSONArray.parseArray(content, AreaInfo.class);
                            if (list.size() > 0) {
                                item.reset();
                                item = createItem(context);
                                item.selected();
                                llContent.addView(item);
                                infos.clear();
                                infos.addAll(list);
                                item.setListAreaInfos((List<AreaInfo>) infos.clone());
                                adapter.notifyDataSetChanged();
                            } else {
                                originalAreaId = (ArrayList<String>) chooseAreaId.clone();
                                originalAreaName = (ArrayList<String>) chooseAreaName.clone();
                                listener.chooseCompelete(originalAreaId, originalAreaName, tag);
                                hide();
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void doHttpCanceled(Object... param) {

    }

    private void reset() {
        llContent.removeAllViews();
        parentId = "";
        infos.clear();
        chooseAreaId.clear();
        chooseAreaName.clear();
    }

    public List<String> getChooseArea() {
        return originalAreaId;
    }

    public void setListener(ChooseListener listener) {
        this.listener = listener;
    }

    public interface ChooseListener {
        void chooseCompelete(List<String> areaIds, List<String> areaNames, String tag);
    }

    public boolean isShow() {
        return isShow;
    }

    public void dispatchTouchEventA(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = ((Activity) context).getCurrentFocus();
            if (isShow()) {
                if (isShouldHide(v, ev)) {
                    hide();
                }
            }
        }
    }

    private boolean isShouldHide(View v, MotionEvent event) {
        if (v != null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            if (event.getY() < size.y - getContext().getResources().getDimension(R.dimen.dp_202)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
