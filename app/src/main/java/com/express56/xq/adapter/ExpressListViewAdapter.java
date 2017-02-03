package com.express56.xq.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.activity.BaseActivity;
import com.express56.xq.activity.ZoomImageActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.http.HttpHelper;
import com.express56.xq.http.RequestID;
import com.express56.xq.model.ExpressInfo;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.SharedPreUtils;
import com.express56.xq.util.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 *
 */
public class ExpressListViewAdapter extends BaseAdapter {

    private static final String TAG = ExpressListViewAdapter.class.getSimpleName();

    private final BaseActivity activity;

    private float iamgeShowWidth = 0;

    /**
     * 记录数组
     */
    public ArrayList<ExpressInfo> expressInfos = null;

    public ExpressListViewAdapter(BaseActivity context, ArrayList<ExpressInfo> expressInfos, float iamgeShowWidth) {
        this.activity = context;
        this.expressInfos = expressInfos;
        this.iamgeShowWidth = iamgeShowWidth;
    }

    @Override
    public int getCount() {
        return expressInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return expressInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.express_search_result_listview_item, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_listholder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_listholder);
        }
        viewHolder.contentView = convertView;
        bingHolder(viewHolder, expressInfos, position);
        return convertView;
    }

    private void bingHolder(final ViewHolder viewHolder, ArrayList<ExpressInfo> expressInfos, int position) {
        if (viewHolder != null) {
            reset(viewHolder);
            if (expressInfos != null && expressInfos.size() > 0) {
                ExpressInfo expressInfo = expressInfos.get(position);
                if (expressInfo == null) {
                    return;
                }
                viewHolder.textView_express_id.setText(expressInfo.expressNo);

                //sdcard 中寻找图片，没有找到则下载该图片
                File folder = new File(ExpressConstant.COMPRESS_PHOTO_FILE_PATH);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                final File file = new File(folder, expressInfo.expressNo + "_p.jpeg");
                if (file != null && file.exists()) {
                    Bitmap bitmap = BitmapUtils.getSDCardBitmap(ExpressConstant.COMPRESS_PHOTO_FILE_PATH + File.separator + expressInfo.expressNo + "_p.jpeg");
                    LogUtil.d(TAG, "bitmap = " + bitmap);
                    if (bitmap != null) {
                        bitmap = BitmapUtils.getZoomBitmap(bitmap, iamgeShowWidth / bitmap.getWidth());
                        viewHolder.imageView_express_photo.setImageBitmap(bitmap);
                    } else {
                        downloadPic(viewHolder, expressInfo, file);
                    }
                } else {
                    downloadPic(viewHolder, expressInfo, file);
                }
                viewHolder.textView_express_upload_time.setText(expressInfo.createDate);

                viewHolder.contentView.setTag(R.id.tag_imageview, viewHolder.imageView_express_photo);

                viewHolder.contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.imageView_express_photo.getDrawable() == null) return;
                        Drawable drawable = viewHolder.imageView_express_photo.getDrawable();
                        if (drawable != null) {
                            Intent intent = new Intent(activity, ZoomImageActivity.class);//大图界面
                            intent.putExtra("the_bitmap_path", file.getPath());//下个界面显示的大图
                            activity.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    private void downloadPic(ViewHolder viewHolder, ExpressInfo expressInfo, File file) {
        if (!StringUtils.isEmpty(expressInfo.filePath)) {
            SharedPreUtils sp = new SharedPreUtils(activity);
            String url = HttpHelper.HTTP + HttpHelper.IP.substring(0, HttpHelper.IP.length() - 0) + "/images/" + expressInfo.filePath.replace("\\", "/");
//                        String url = "http://192.168.31.234" + "/images/" + expressInfo.filePath.replace("\\", "/");
            HttpHelper.sendRequest_loadExpressImage(
//                                    activity, RequestID.REQ_DOWNLOAD_PICTURE, "http://192.168.31.234/images/send/2016-11-06/9c9fe63663824f3ca7949cc4cdd83de1.jpeg", viewHolder.imageView_express_photo, file);
                    activity, RequestID.REQ_DOWNLOAD_PICTURE, url, viewHolder.imageView_express_photo, file, iamgeShowWidth);
        }
    }

    /**
     * 清空旧数据
     *
     * @param viewHolder
     */
    private void reset(ViewHolder viewHolder) {
        viewHolder.textView_express_id.setText("");
        viewHolder.textView_express_upload_time.setText("");
//        viewHolder.imageView_express_photo.setImageResource(R.drawable.default_pic);
    }

    private ViewHolder createHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.textView_express_id = (TextView) convertView.findViewById(R.id.express_list_textView_express_id);
        holder.textView_express_upload_time = (TextView) convertView.findViewById(R.id.express_list_textView_express_upload_time);
        holder.imageView_express_photo = (ImageView) convertView.findViewById(R.id.express_list_imageView_express_img);
        return holder;
    }


    /**
     * 记录信息展示
     */
    private class ViewHolder {

        /**
         * 快递单号
         */
        public TextView textView_express_id = null;

        /**
         * 类型 normal
         */
        public TextView textView_express_upload_time = null;

        /**
         * 类型 icon
         */
        public ImageView imageView_express_photo = null;

        /**
         * 整个item部分
         */
        public View contentView = null;

    }
}
