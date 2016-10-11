/*
******************************* Copyright (c)*********************************\
**
**                 (c) Copyright 2015, 蒋朋, china, qd. sd
**                          All Rights Reserved
**
**                           By()
**
**-----------------------------------版本信息------------------------------------
** 版    本: V0.1
**
**------------------------------------------------------------------------------
********************************End of Head************************************\
*/

package com.inst.greendao3_demo.entity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.inst.greendao3_demo.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文 件 名: Student
 * 创 建 人: 蒋朋
 * 创建日期: 16-8-26 17:25
 * 描    述:
 * 修 改 人:
 * 修改时间：
 * 修改备注：
 */

@Entity
public class Student extends AbstractItem<Student, Student.ViewHolder> {

    @Id
    public Long id;
    public String name;
    public String age;
    public String number;
    public String score;

    @Generated(hash = 1672075654)
    public Student(Long id, String name, String age, String number, String score) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.number = number;
        this.score = score;
    }
    @Generated(hash = 1556870573)
    public Student() {
    }

    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getAge() {
        return this.age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getScore() {
        return this.score;
    }
    public void setScore(String score) {
        this.score = score;
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
        holder.mTvId.setText(id + "");
        holder.mTvName.setText(name);
        holder.mTvAge.setText(age);
        holder.mTvNumber.setText(number);
        holder.mTvScore.setText(score);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
}
