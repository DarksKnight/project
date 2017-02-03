package com.express56.xq.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.activity.PhotoActivity;
import com.express56.xq.activity.ZoomImageActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.ExpressPhoto;
import com.express56.xq.service.UploadService;
import com.express56.xq.util.BitmapUtils;
import com.express56.xq.util.DateUtil;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.CustomDialog;
import com.express56.xq.widget.ToastUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 未识别列表适配器
 */
public class ExpressNotUploadListViewAdapter extends BaseAdapter {

    private static final String TAG = ExpressNotUploadListViewAdapter.class.getSimpleName();

    private final PhotoActivity activity;

    private float iamgeShowWidth = 0;

    /**
     * 记录数组
     */
    public ArrayList<ExpressPhoto> expressPhotos = null;

    public ExpressNotUploadListViewAdapter(PhotoActivity context, ArrayList<ExpressPhoto> expressPhotos, float iamgeShowWidth) {
        this.activity = context;
        this.expressPhotos = expressPhotos;
        this.iamgeShowWidth = iamgeShowWidth;
    }

    @Override
    public int getCount() {
        return expressPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return expressPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.express_discriminate_listview_item, null);
            viewHolder = createHolder(convertView);
            convertView.setTag(R.id.tag_listholder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_listholder);
        }
        viewHolder.contentView = convertView;
        bingHolder(viewHolder, expressPhotos, position);
        return convertView;
    }

    private void bingHolder(final ViewHolder viewHolder, ArrayList<ExpressPhoto> expressPhotos, final int position) {
        if (viewHolder != null) {
            reset(viewHolder);
            if (expressPhotos != null && expressPhotos.size() > 0) {
                final ExpressPhoto expressPhoto = expressPhotos.get(position);
                if (expressPhoto == null) {
                    return;
                }
//                final Bitmap tempBitmap = BitmapUtils.BytesToBimap(expressPhoto.photoFilePath);
                final Bitmap tempBitmap = BitmapUtils.getNotScaledBitmapFromFile(expressPhoto.photoFilePath);
                final String filePath = ExpressConstant.IMAGE_FILE_LOCATION_FOLDER_PATH + File.separator + "not_up_" + expressPhoto + ".jpeg";
                if (expressPhoto.photoFilePath == null || expressPhoto.photoFilePath.length() == 0) {
                    viewHolder.imageView_express_photo.setImageResource(R.drawable.default_pic);
                } else {
                    Bitmap bitmap = BitmapUtils.getZoomBitmap(tempBitmap, iamgeShowWidth / tempBitmap.getWidth());
                    viewHolder.imageView_express_photo.setImageBitmap(bitmap);
                    BitmapUtils.saveBitmapToFile(bitmap, new File(filePath));
                    viewHolder.express_photo_size.setText("(" + expressPhoto.size + ")");
                }
                viewHolder.express_no.setText(expressPhoto.photo_express_no);
//                viewHolder.express_no.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (activity instanceof PhotoActivity) {
//                            ((PhotoActivity)activity).showInputDialog(expressPhoto.photo_id, position);
//                        }
//                    }
//                });
                String date = DateUtil.getDateS(Long.parseLong(expressPhoto.photo_id));
                viewHolder.photo_create_time.setText(date.substring(0, date.length() - 3));
                viewHolder.contentView.setTag(R.id.tag_imageview, viewHolder.imageView_express_photo);
                viewHolder.imageView_express_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expressPhoto.photoFilePath == null || expressPhoto.photoFilePath.length() == 0 || StringUtils.isEmpty(filePath))
                            return;
                        Intent intent = new Intent(activity, ZoomImageActivity.class);//大图界面
                        intent.putExtra("the_bitmap_path", filePath);
                        activity.startActivity(intent);
                    }
                });

                viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmDialog(expressPhoto);
                    }
                });
                viewHolder.btn_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //上传
                        boolean hasUploading = UploadService.photoDataHelper.isPhotoUploading(expressPhoto.photo_id,
                                activity.getSharedPreUtils().getUserInfo().phone);
                        if (hasUploading) {
                            ToastUtil.showMessage(activity, "图片已经上传");
                            activity.refreshAfterDelete(expressPhoto.photo_id);//从listview中删除item
                        } else {
                            //上传图片
                            activity.uploadPhoto(expressPhoto);
                        }

                    }
                });
            }
        }
    }

    /**
     * 删除图片
     */
    protected void showDeleteConfirmDialog(final ExpressPhoto expressPhoto) {
        String prompt = "确定删除图片吗？";
        String confirm = activity.getString(R.string.str_dialog_btn_to_ok);
        String cancel = activity.getString(R.string.str_dialog_btn_cancel);
        final CustomDialog customDialog = new CustomDialog(activity, prompt, confirm, cancel, 60);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                customDialog.dismiss();
                //删除
                boolean hasUploading = UploadService.photoDataHelper.isPhotoUploading(expressPhoto.photo_id,
                        activity.getSharedPreUtils().getUserInfo().phone);
                if (hasUploading) {
                    ToastUtil.showMessage(activity, "图片已经上传");
                } else {
                    //从本地数据库删除
                    UploadService.photoDataHelper.deletePhotoByID(expressPhoto.photo_id, activity.getSharedPreUtils().getUserInfo().phone);
                }
                activity.refreshAfterDelete(expressPhoto.photo_id);//从listview中删除item
            }

            @Override
            public void doCancel() {
                customDialog.dismiss();
            }
        });
    }


    /**
     * 清空旧数据
     *
     * @param viewHolder
     */
    private void reset(ViewHolder viewHolder) {
        viewHolder.photo_create_time.setText("");
    }

    private ViewHolder createHolder(View convertView) {
        final ViewHolder holder = new ViewHolder();
        holder.express_no = (TextView) convertView.findViewById(R.id.express_discriminate_express_no);
        holder.express_photo_size = (TextView) convertView.findViewById(R.id.express_discriminate_photo_size);
        holder.photo_create_time = (TextView) convertView.findViewById(R.id.express_discriminate_photo_create_time);
        holder.imageView_express_photo = (ImageView) convertView.findViewById(R.id.express_discriminate_express_img);
        holder.btn_delete = (ImageView) convertView.findViewById(R.id.express_discriminate_btn_delete);
        holder.btn_upload = (ImageView) convertView.findViewById(R.id.express_discriminate_btn_upload);
        return holder;
    }


    /**
     * 记录信息展示
     */
    private class ViewHolder {

        /**
         * 快递单号
         */
        public TextView express_no = null;

        /**
         * 拍摄时间
         */
        public TextView photo_create_time = null;

        /**
         * 照片
         */
        public ImageView imageView_express_photo = null;

        /**
         * 删除记录
         */
        public ImageView btn_delete = null;

        /**
         * 上传
         */
        public ImageView btn_upload = null;

        /**
         * 整个item部分
         */
        public View contentView = null;

        /**
         * 图片大小
         */
        public TextView express_photo_size;
    }
}
