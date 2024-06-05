package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllTaskAdapter extends RecyclerView.Adapter<AllTaskAdapter.ViewHolder>{
    Context context;
    ArrayList<TaskModel> taskList;

    public AllTaskAdapter(Context context, ArrayList<TaskModel> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public AllTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.all_task_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllTaskAdapter.ViewHolder holder, int position) {
        holder.taskName.setText(taskList.get(position).getTaskName());
        holder.taskTime.setText(taskList.get(position).getTaskTime());
        holder.taskDate.setText(taskList.get(position).getTaskDate());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskTime, taskDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskDate = itemView.findViewById(R.id.taskDate);
        }
    }
}
