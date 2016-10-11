package com.inst.greendao3_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inst.greendao3_demo.db.DbCore;
import com.inst.greendao3_demo.db.DbUtil;
import com.inst.greendao3_demo.db.StudentHelper;
import com.inst.greendao3_demo.entity.Student;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bt_add)
    Button mBtAdd;
    @BindView(R.id.bt_minus)
    Button mBtMinus;
    @BindView(R.id.rv_main)
    RecyclerView mRvMain;

    private FastItemAdapter<StudentItem> mFastAdapter;
    private List<StudentItem> mStudentItems;

    private StudentHelper mHelper;
    private List<Student> students;

    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        //初始化数据库
        DbCore.init(this);
        DbCore.enableQueryBuilderLog(); //开启调试 log

        mFastAdapter = new FastItemAdapter<>();
        mStudentItems = new ArrayList<>();

        mRandom = new Random();

        GridLayoutManager manager = new GridLayoutManager(this, 1);
        manager.setSpanSizeLookup(new GridLayoutManager.DefaultSpanSizeLookup());


        mRvMain.setAdapter(mFastAdapter);
        mRvMain.setLayoutManager(manager);

        mHelper = DbUtil.getDriverService();

        //读取所有学生
        students = mHelper.queryAll();

        //把学生信息显示到界面
        for (Student s : students) {
            StudentItem item = new StudentItem();
            item.id = s.getId();
            item.name = s.getName();
            item.age = s.getAge();
            item.number = s.getNumber();
            mStudentItems.add(item);
        }
        mFastAdapter.add(mStudentItems);

    }


    @OnClick({R.id.bt_add, R.id.bt_minus})
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.bt_add:
                //数据库保存数据
                long id = mHelper.count();

                //界面添加
                StudentItem item = new StudentItem();
                item.id = id + 1;
                item.name = "Nauto_" + (id + 1);
                item.age = mRandom.nextInt(60) + "";
                item.number = 6 + "" + (id + 1);
                mFastAdapter.add(item);

                //保存到数据库s
                Student student = new Student();
                student.setId(id + 1);
                student.setName("Nauto_" + (id + 1));
                student.setAge(mRandom.nextInt(20) + "");
                student.setNumber(6 + "" + (id + 1));
                //添加一条记录
                mHelper.save(student);
                break;
            case R.id.bt_minus:
                //查询所有数据
                students = mHelper.queryAll();
                if (students.size() > 0) {
                    Student s = students.get(students.size() - 1);
                    //删除一条记录
                    mHelper.delete(s);

                    //更新界面
                    mFastAdapter.remove(students.size() - 1);
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

    class StudentItem extends AbstractItem<StudentItem, ViewHolder> {
        private long id;
        private String name;
        private String age;
        private String number;

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
            holder.mTvId.setText(id + "");
            holder.mTvName.setText(name);
            holder.mTvAge.setText(age);
            holder.mTvNumber.setText(number);

        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected final View view;
        @BindView(R.id.tv_id) TextView mTvId;
        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.tv_age) TextView mTvAge;
        @BindView(R.id.tv_number) TextView mTvNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}
