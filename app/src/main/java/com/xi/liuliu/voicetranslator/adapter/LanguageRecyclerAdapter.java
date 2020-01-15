package com.xi.liuliu.voicetranslator.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.bean.Language;

import java.util.List;

/**
 * Date:2019/7/31
 * Author:zhangxiaobei
 * Describe:
 */
public class LanguageRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private List<Language> mLanguageList;
    private OnItemClickListener mOnItemClickListener;

    public LanguageRecyclerAdapter(List<Language> list) {
        mLanguageList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_view_fragment_language_list, viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.view = view;
        viewHolder.textView = view.findViewById(R.id.text_view_item_recycler_view_fragment_language_selector);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(i);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.textView.setText(mLanguageList.get(i).getChineseName());
    }

    @Override
    public int getItemCount() {
        if (mLanguageList != null) return mLanguageList.size();
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

}
