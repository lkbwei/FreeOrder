package com.example.lkbwei.freeOrder.DataBase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.Boss.MenuListFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by lkbwei on 2017/3/8.
 */

public class Lab {

    public static final String TAG = "BMOB";

    private static Lab sLab;
    private Context mContext;
    private Lab(Context context){
         mContext = context.getApplicationContext();
    }

    /**
     * 获取Lab实例
     * 获取单例Lab实例
     * @param context 上下文
     * @return Lab实例
     * @since 1.0
     */
    public static Lab getLab(Context context){
        if (sLab == null){
            sLab =  new Lab(context);
        }
        return sLab;
    }


    /**
     * 获取菜单
     * 根据条件搜索数据库，返回菜单列表。
     * @param whereClause 键数组，与每个键与值数组相对应
     * @param whereArgs 值数组
     * @param order 排序字段1
     * @param sequence 1表示正序，2表示逆序，0表示无序
     * @param order1 排序字段2
     * @param sequence1 1表示正序，2表示逆序，0表示无序
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public void getGoodsList(String[] whereClause, Object[] whereArgs,
                                        String order, int sequence,String order1,int sequence1,
                             final Handler handler, final int what){

        BmobQuery<GoodsData> query = new BmobQuery<>();
        for (int i = 0;i < whereClause.length;i++){
            query.addWhereEqualTo(whereClause[i],whereArgs[i]);
        }
        if (order != null){
            if (sequence == 1){
                query.order(order);
            }else if (sequence == 2){
                query.order("-" + order);
            }
        }
        if (order1 != null){
            if (sequence1 == 1){
                query.order(order1);
            }else if(sequence1 == 2){
                query.order("-" + order1);
            }
        }
        query.findObjects(new FindListener<GoodsData>() {
            @Override
            public void done(List<GoodsData> list, BmobException e) {
                if (e == null){

                    handler.obtainMessage(what,list).sendToTarget();//发送回主线程处理

                }else {
                    Log.e("CLASSIGY",e.toString());
                }
            }
        });

    }

    /**
     * 获取商家名
     * 根据餐厅，获取商家名
     * @param what 键
     * @param value 值
     * @param handler 传递消息的Handler
     * @param tag 标记
     * @since 1.0
     */
    public void getBoss(String what, String value, final Handler handler, final int tag){
        BmobQuery<LoginTable> query = new BmobQuery<>();
        query.addWhereEqualTo(what,value);
        query.findObjects(new FindListener<LoginTable>() {
            @Override
            public void done(List<LoginTable> list, BmobException e) {
                if (e == null && list.size() == 1){
                    handler.obtainMessage(tag,list).sendToTarget();
                }else {
                    Log.e(TAG,"找不到商家");
                }
            }
        });
    }


    /**
     * 比较查询
     * 根据条件，进行比较查询，where数组第一项为主键，一次只能比较一个字段
     * @param where 键
     * @param values 值
     * @param condition 为TRUE表示大于查询，否则相反
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public void doConditionQuery(String[] where, Object[] values, boolean condition,
                                 final Handler handler, final int what){
        BmobQuery<GoodsData> query = new BmobQuery<>();
        query.addWhereEqualTo(where[0],values[0]);
        if (condition){
            query.addWhereGreaterThan(where[1],values[1]);
        }else {
            query.addWhereLessThan(where[1],values[1]);
        }

        query.findObjects(new FindListener<GoodsData>() {
            @Override
            public void done(List<GoodsData> list, BmobException e) {
                if (e == null){
                    handler.obtainMessage(what,list).sendToTarget();
                }else {
                    Log.e(TAG,e.toString());
                }
            }
        });

    }

    /**
     * 插入数据
     * 往数据中插入信息
     * @param context 上下文
     * @param user 商家名
     * @param foodName 食品
     * @param imageUrl 图片地址
     * @param saveVolume 销量
     * @param price 价格
     * @param positive 好评数
     * @param negative 差评数
     * @param description 描述
     * @param stock 是否有货
     * @param classify 分类
     * @param coverNum 不是封面图片的，coverNum字段为-1，否则为对应的位置
     * @since 1.0
     */
    public void insertGoodsData(Context context,String user,String foodName,String imageUrl,Integer saveVolume,
                                Double price,Integer positive,Integer negative,String description,
                                Boolean stock,String classify,Integer coverNum){
        //图片的更新需要先上传图片，获得url后再保存在表中
        File imageFile = new File(imageUrl);

        final GoodsData data = new GoodsData();
        data.setUser(user);
        data.setFoodName(foodName);
        data.setSaleVolume(saveVolume);
        data.setPrice(price);
        data.setPositive(positive);
        data.setNegative(negative);
        data.setDescription(description);
        data.setStock(stock);
        data.setClassify(classify);
        data.setCoverNum(coverNum);

        final ProgressDialog dialog = createDialog("上传中...",context);
        dialog.show();

        final BmobFile bmobFile = new BmobFile(imageFile);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                  if (e == null){
                      data.setImageUrl(bmobFile.getUrl());
                      data.save(new SaveListener<String>() {
                          @Override
                          public void done(String s, BmobException e) {
                              if (e == null){
                                  Toast.makeText(mContext,"上传成功",Toast.LENGTH_LONG).show();
                                  dialog.cancel();
                                  MenuListFragment.sHandler.obtainMessage(
                                          MenuListFragment.SAVE_SUCCESS).sendToTarget();
                              }else {
                                  Log.e(TAG,"上传失败");
                                  Toast.makeText(mContext,"上传失败",Toast.LENGTH_LONG).show();
                              }
                          }
                      });
                  }else {
                      Log.e("IMAGE","*******************错误");
                  }
            }

            @Override
            public void onProgress(Integer value) {
                dialog.setProgress(value);
                // 返回的上传进度（百分比）
            }
        });

    }

    //数组一二项用来找出对应行的objectID,图片需要更新的话，需要放在数组最后一项。

    /**
     * 更新数据
     * 先找出对应行的ObjectID，再进行更新操作
     * @param context 上下文
     * @param oldFoodName 旧食品名
     * @param newFoodName 新食品名
     * @param name 键数组，数组一二项用来找出对应行的objectID,图片需要更新的话，需要放在数组最后一项
     * @param values 值数组
     * @since 1.0
     */
    public  void updateGoodsData(final Context context, final String oldFoodName,
                                 final String newFoodName, final String[] name, final Object[] values){
        //先通过条件查询objectId，再进行更操作
        final String[] id = new String[1];
        final BmobQuery<GoodsData> query = new BmobQuery<>();
        query.addWhereEqualTo(name[0],values[0]);
        query.addWhereEqualTo("FoodName",oldFoodName);

        final ProgressDialog dialog = createHorizontalDialog("更新中...",context);
        dialog.show();
        query.findObjects(new FindListener<GoodsData>() {
            @Override
            public void done(List<GoodsData> list, BmobException e) {
                id[0] = list.get(0).getObjectId();
                final GoodsData data = new GoodsData();
                if (newFoodName != null){
                    data.setValue("FoodName",newFoodName);
                }
                for (int i = 1;i < name.length;i++){
                    //判断是否是更新图片
                    if (name[i].equals("ImageUrl")){
                        File imageFile = new File((String)values[i]);

                        final BmobFile bmobFile = new BmobFile(imageFile);
                        bmobFile.uploadblock(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null){
                                    data.setValue("ImageUrl",bmobFile.getUrl());

                                    data.update(id[0], new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null){
                                                dialog.setMessage("更新成功");
                                                dialog.cancel();
                                                Toast.makeText(mContext,"更新数据成功",Toast.LENGTH_LONG).show();
                                                MenuListFragment.sHandler.obtainMessage(
                                                        MenuListFragment.UPDATE_DATA).sendToTarget();
                                            }else {
                                                Log.e(TAG,"更新数据失败");
                                                Toast.makeText(mContext,"更新数据失败",Toast.LENGTH_LONG).show();
                                                Toast.makeText(mContext,"更新数据失败",Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    });
                                }else {
                                    Log.e(TAG,"图片不存在" + e.toString());
                                }
                            }
                        });

                    }else {
                        data.setValue(name[i], values[i]);
                    }
                }
                data.update(id[0], new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            dialog.cancel();
                            Toast.makeText(mContext,"更新数据成功",Toast.LENGTH_LONG).show();
                            MenuListFragment.sHandler.obtainMessage(
                                    MenuListFragment.UPDATE_DATA).sendToTarget();
                        }else {
                            Log.e(TAG,"更新数据失败" + e.toString());
                            Toast.makeText(mContext,"更新数据失败",Toast.LENGTH_LONG).show();
                        }
                    }

                });

            }
        });
    }

    /**
     * 批量更新数据
     * 批量更新操作
     * @param context 上下文
     * @param where 键数组
     * @param values 值数组
     * @param handler 传递消息的Handler
     * @param what 标记
     * @since 1.0
     */
    public void updateBatchGoodsData(final Context context, String[] where, final Object[] values,
                                     final Handler handler, final int what){

        for (int i = 1; i < where.length;i ++){
            BmobQuery<GoodsData> query = new BmobQuery<>();
            query.addWhereEqualTo(where[0],values[0]);
            query.addWhereEqualTo("classify",where[i]);
            final int finalI = i;
            query.findObjects(new FindListener<GoodsData>() {
                @Override
                public void done(List<GoodsData> list, BmobException e) {
                    if (e == null && list != null && list.size() != 0){
                        List<BmobObject> batchList = new ArrayList<>();
                        for (int j = 0;j < list.size();j ++){
                            GoodsData data = new GoodsData();
                            data.setObjectId(list.get(j).getObjectId());
                            data.setClassify(String.valueOf(values[finalI]));
                            batchList.add(data);
                        }
                        new BmobBatch().updateBatch(batchList).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> list, BmobException e) {
                                if (e == null){
                                    handler.obtainMessage(what).sendToTarget();
                                }else {
                                    Log.e(TAG,e.toString());
                                    Toast.makeText(context,"更新不成功",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        Log.e(TAG,e.toString());
                        Toast.makeText(context,"更新不成功",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    /**
     * 删除数据
     * 先找出对应行的ObjectID，再进行删除操作
     * @param context 上下文
     * @param name 键数组
     * @param values 值数组
     * @param handler 传递消息的Handler
     * @param what 标记
     * @param hasProgressDialog 是否开启进度窗口
     * @since 1.0
     */
    public void deleteGoodsData(Context context, String[] name, Object[] values,
                                final Handler handler, final int what, final boolean hasProgressDialog){
        final ProgressDialog dialog = createHorizontalDialog("删除中，请稍后...",context);
        if (hasProgressDialog) {
            dialog.show();
        }

        BmobQuery<GoodsData> query = new BmobQuery<>();
        for (int i = 0;i < 2;i++) {
            query.addWhereEqualTo(name[i],values[i]);
        }
        query.findObjects(new FindListener<GoodsData>() {
            @Override
            public void done(List<GoodsData> list, BmobException e) {
                if (e == null && list != null & list.size() != 0){
                    List<BmobObject> datas = new ArrayList<>();
                    for (int i = 0;i < list.size();i ++){
                        String id = list.get(i).getObjectId();
                        GoodsData data = new GoodsData();
                        data.setObjectId(id);
                        datas.add(data);
                    }

                    new BmobBatch().deleteBatch(datas).doBatch(new QueryListListener<BatchResult>() {
                        @Override
                        public void done(List<BatchResult> list, BmobException e) {
                            if (e == null){
                                handler.obtainMessage(what).sendToTarget();
                                if (hasProgressDialog) {
                                    dialog.cancel();
                                }
                            }else {
                                Log.e(TAG,"删除失败" + e.toString());
                                Toast.makeText(mContext,"删除失败",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else {
                    if (list == null || list.size() ==0){
                        handler.obtainMessage(what).sendToTarget();
                    }else {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }

    /**
     * 更新销量
     * 先找出对应行的ObjectID，再更新
     * @param boss 商家名
     * @param foodName 食品名
     * @since 1.0
     */
    public void updateVolume(String boss,String foodName){
        BmobQuery<GoodsData> query = new BmobQuery<>();
        query.addWhereEqualTo("User",boss);
        query.addWhereEqualTo("FoodName",foodName);
        query.findObjects(new FindListener<GoodsData>() {
            @Override
            public void done(List<GoodsData> list, BmobException e) {
                if (e == null && list != null && list.size() != 0){
                    GoodsData data = list.get(0);
                    data.increment("SaleVolume");
                    data.update(data.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null){
                                Log.e(TAG,"销量更新失败" + e.toString());
                            }
                        }
                    });

                }
            }
        });
    }

    /**
     * 更新好评或差评数
     * 先找出对应行的ObjectID，再更新
     * @param boss 商家名
     * @param food 食品名
     * @param what 更新数
     * @since 1.0
     */
    public void updateGoodOrBad(String boss, String food, final String what){
        BmobQuery<GoodsData> query = new BmobQuery<>();
        query.addWhereEqualTo("User",boss);
        query.addWhereEqualTo("FoodName",food);
        query.findObjects(new FindListener<GoodsData>(){
            @Override
            public void done(List<GoodsData> list, BmobException e) {
                if (e == null && list != null && list.size() != 0){
                    GoodsData data = list.get(0);
                    data.increment(what);
                    data.update(data.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null){
                                Log.e(TAG,"销量更新失败" + e.toString());
                            }
                        }
                    });
                }else {
                    Log.e(TAG,"更新点赞失败" + e.toString());
                }
            }
        });
    }

    /**
     * 开启进度窗口
     * @param tip 提示
     * @param context 上下文
     * @return 窗口ProgressDialog
     * @since 1.0
     */
    public ProgressDialog createDialog(String tip,Context context){
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle(tip);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 开启水平进度窗口
     * @param tip 提示
     * @param context 上下文
     * @return 窗口ProgressDialog
     * @since 1.0
     */
    public ProgressDialog createHorizontalDialog(String tip,Context context){
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(tip);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


}
