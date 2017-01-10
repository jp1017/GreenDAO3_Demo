package com.inst.greendao3_demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inst.greendao3_demo.R;
import com.inst.greendao3_demo.dao.DaoMaster;
import com.inst.greendao3_demo.dao.StudentDao;
import com.inst.greendao3_demo.db.DbUtil;
import com.inst.greendao3_demo.db.StudentHelper;
import com.inst.greendao3_demo.entity.Student;
import com.mikepenz.fastadapter.commons.adapters.GenericFastItemAdapter;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.Function;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.socks.library.KLog;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)     Toolbar mToolbar;
    @BindView(R.id.bt_add)      Button mBtAdd;
    @BindView(R.id.bt_minus)    Button mBtMinus;
    @BindView(R.id.rv_main)     RecyclerView mRvMain;

    private GenericFastItemAdapter<Student, StudentItem> mFastAdapter;
    private List<Student> mStudents;
    private List<Student> dbStudents;

    private StudentHelper mHelper;

    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);

        //当前数据库版本
        KLog.w("db version: " + DaoMaster.SCHEMA_VERSION);

        mFastAdapter = new GenericFastItemAdapter<>(new Function<Student, StudentItem>() {
            @Override
            public StudentItem apply(Student student) {
                return new StudentItem(student);
            }
        });

        mStudents = new ArrayList<>();

        mRandom = new Random();

        GridLayoutManager manager = new GridLayoutManager(this, 1);
        manager.setSpanSizeLookup(new GridLayoutManager.DefaultSpanSizeLookup());


        mRvMain.setAdapter(mFastAdapter);
        mRvMain.setLayoutManager(manager);

        mHelper = DbUtil.getDriverHelper();

        //读取所有学生
        dbStudents = mHelper.queryAll();

        //把学生信息显示到界面
        for (Student s : dbStudents) {
            Student item = new Student();
            item.id = s.getId();
            item.name = s.getName();
            item.age = s.getAge();
            item.number = s.getNumber();
            item.score = s.getScore();

            KLog.w("db: " + item.id + ", "
                    + item.age + ", " + item.name + ", "
                    + item.number
            );

            mStudents.add(item);
        }
        mFastAdapter.addModel(mStudents);

        //获取age大于20的数据
        Query<Student> query = mHelper.queryBuilder()
                .where(StudentDao.Properties.Age.ge("20"))
                .build();
        dbStudents = query.list();

    }


    @OnClick({R.id.bt_add, R.id.bt_minus})
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.bt_add:
                long id = mHelper.count();

                //界面添加
                Student stu = new Student();
                stu.id = id + 1;
                stu.name = "Nauto_" + (id + 1);
                stu.age = mRandom.nextInt(60) + "";
                stu.number = 6 + "" + (id + 1);
                stu.score = mRandom.nextInt(100) + "";

                mFastAdapter.addModel(stu);

                //保存到数据库s
                mHelper.save(stu);
                break;
            case R.id.bt_minus:
                //查询所有数据
                dbStudents = mHelper.queryAll();
                if (dbStudents.size() > 0) {
                    Student s = dbStudents.get(dbStudents.size() - 1);
                    //删除一条记录
                    mHelper.delete(s);

                    //更新界面
                    mFastAdapter.removeModel(dbStudents.size() - 1);
                } else {
                    showToast("no student now");
                }


                break;
        }
    }

    private Toast mToast;

    /**
     * 防多次点击的吐司
     * @param msg
     */
    public void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.show();
        }
    }


    public class StudentItem extends GenericAbstractItem<Student, StudentItem, StudentItem.ViewHolder> {
        //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
        private final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

        public StudentItem(Student student) {
            super(student);
        }

        @Override
        public int getType() {
            return R.id.rv_content_main;
        }

        @Override
        public int getLayoutRes() {
            return R.layout.item_rv_student;
        }

        @Override
        public void bindView(ViewHolder holder, List payloads) {
            super.bindView(holder, payloads);
            holder.mTvId.setText(getModel().getId() + "");
            holder.mTvName.setText(getModel().getName());
            holder.mTvAge.setText(getModel().getAge());
            holder.mTvNumber.setText(getModel().getNumber());
            holder.mTvScore.setText(getModel().getScore());

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            protected final View view;
            @BindView(R.id.tv_id) TextView mTvId;
            @BindView(R.id.tv_name) TextView mTvName;
            @BindView(R.id.tv_age) TextView mTvAge;
            @BindView(R.id.tv_number) TextView mTvNumber;
            @BindView(R.id.tv_score) TextView mTvScore;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                ButterKnife.bind(this, view);
            }
        }


        /**
         * our ItemFactory implementation which creates the ViewHolder for our adapter.
         * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
         * and it is also many many times more efficient if you define custom listeners on views within your item.
         */
        protected class ItemFactory implements ViewHolderFactory<ViewHolder> {
            public ViewHolder create(View v) {
                return new ViewHolder(v);
            }
        }

        /**
         * return our ViewHolderFactory implementation here
         *
         * @return
         */
        @Override
        public ViewHolderFactory<? extends ViewHolder> getFactory() {
            return FACTORY;
        }

    }

}
