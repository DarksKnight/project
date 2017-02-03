package com.express56.xq.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.express56.xq.R;
import com.express56.xq.activity.InvokeStaticMethod;
import com.express56.xq.activity.MainActivity;
import com.express56.xq.adapter.MainGridViewAdapter;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.ADInfo;
import com.express56.xq.model.Adv;
import com.express56.xq.util.DisplayUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.CycleViewPager;
import com.express56.xq.widget.ViewFactory;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends MyBaseFragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private Button btn_logout = null;

    private Context context = null;

    private GridView functionGridView;

    /**
     * GridView显示的数据集合
     */
    private ArrayList<HashMap<String, String>> mlist = new ArrayList<HashMap<String, String>>();
    private MainGridViewAdapter mainGridViewAdapter;

    public void setOnClickListener(FragmentOnClickListener listener) {
        this.listener = listener;
    }

    private FragmentOnClickListener listener = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
//    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        context = getActivity();

//        btn_logout = (Button) rootView.findViewById(R.id.btn_logout);
//        btn_logout.setOnClickListener(this);

        functionGridView = (GridView) rootView.findViewById(R.id.main_gridview);
        functionGridView.setColumnWidth((DisplayUtil.screenWidth - DisplayUtil.getPxByDP(40, context)) / (ExpressConstant.MAIN_GRIDVIEW_COLUMN + 0));
        functionGridView.setOnItemClickListener((MainActivity) getActivity());
        cycleViewPager = MainActivity.mainActivity.getCycleViewPager();

    }

    @Override
    protected void initData() {
        super.initData();
        mlist.clear();
        initGridViewData();
        mainGridViewAdapter = new MainGridViewAdapter(context, mlist);
        functionGridView.setAdapter(mainGridViewAdapter);

//        "http://site.com/image.png" // from Web
//        "file:///mnt/sdcard/image.png" // from SD card
//        "file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)
//        "content://media/external/images/media/13" // from content provider
//        "content://media/external/video/media/13" // from content provider (video thumbnail)
//        "assets://image.png" // from assets
//        "drawable://" + R.drawable.img // from drawables (non-9patch images)

        configImageLoader();
        initializeAdvWidget();

    }

    /**
     * 数据准备
     */
    private void initGridViewData() {
        String[] functionNames = null;
        String[] imageNames = null;
        user = sp.getUserInfo();
        if (user.userType == ExpressConstant.USER_TYPE_NORMAL) {
            functionNames = getResources().getStringArray(R.array.wansha_tab_func_name_user);
            imageNames = getResources().getStringArray(R.array.wansha_tab_func_image_user);
        } else if (user.userType == ExpressConstant.USER_TYPE_COURIER) {
            functionNames = getResources().getStringArray(R.array.wansha_tab_func_name_courier);
            imageNames = getResources().getStringArray(R.array.wansha_tab_func_image_courier);
        }
        int length = functionNames.length;
        for (int i = 0; i < length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("functionName", functionNames[i]);
            map.put("iamgeName", imageNames[i]);
            map.put("funcId", String.valueOf(i));
            mlist.add(map);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        listener.FragmentViewOnClick(v);
    }

    private List<ImageView> views = new ArrayList<ImageView>();
    private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
    private CycleViewPager cycleViewPager;

    private String[] imageUrls = {"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
            "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
            "http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
            "http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
            "http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg"};

    private ArrayList<String> imageUrlList = new ArrayList<String>();

    @SuppressLint("NewApi")
    public void initializeAdvWidget() {
        ArrayList<Adv> infos = MainActivity.mainActivity.getAdvs();
//        for (int j = 0; j < infos.size(); j++) {
//            if (j == 0) {
////                infos.get(j).url = "http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg";
//                infos.get(j).url = "assets://adv1.png";
//
//            }
//            if (j == 1) {
////                infos.get(j).url = "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg";
//                infos.get(j).url = "assets://adv2.png";
//            }
//            if (j == 2) {
//                infos.get(j).url = "http://pic18.nipic.com/20111215/577405_080531548148_2.jpg";
//            }
//        }


        views.clear();

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(context, infos.get(infos.size() - 1).getImageUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(context, infos.get(i).getImageUrl()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(context, infos.get(0).getImageUrl()));

        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true);

        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, mAdCycleViewListener);
        //设置轮播
        cycleViewPager.setWheel(true);

        // 设置轮播时间，默认5000ms
        cycleViewPager.setTime(2000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(Adv info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
            }
            InvokeStaticMethod.openHtmlLink(getActivity(), info.getImageLink());

        }

    };

    /**
     * 配置ImageLoder
     */
    private void configImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中

                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        LogUtil.d(TAG, "getActivity = " + getActivity());
        LogUtil.d(TAG, "getActivity().getApplicationContext() = " + getActivity().getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

}
