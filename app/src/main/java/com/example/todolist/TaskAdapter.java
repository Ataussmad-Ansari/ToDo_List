package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todolist.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.viewHolder> {
    Context context;
    ArrayList<TaskModel> taskModels;

    public TaskAdapter(Context context, ArrayList<TaskModel> taskModels) {
        this.context = context;
        this.taskModels = taskModels;
    }
    DatabaseHelper databaseHelper;

    @NonNull
    @Override
    public TaskAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.viewHolder holder, int position) {
        TaskModel taskModel = taskModels.get(position);
        holder.taskName.setText(taskModel.getTaskName());
        holder.taskTime.setText(taskModel.getTaskTime());
        holder.taskDate.setText(taskModel.getTaskDate());

        if (taskModel.getTaskStatus()) {
            holder.taskStatus.setImageResource(R.drawable.done);
        } else {
            holder.taskStatus.setImageResource(R.drawable.dot_menu);
        }

        databaseHelper = new DatabaseHelper(context);

        holder.taskStatus.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.taskStatus);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.status_menu, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.done) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    databaseHelper.updateStatus(taskModel.getId(), true);
                    taskModel.setTaskStatus(true);
                    notifyDataSetChanged();
                }
                return false;
            });
        });

        holder.edit.setOnClickListener(v -> {
            TaskModel task = databaseHelper.getTask(taskModel.getId());
            String taskName = task.getTaskName();
            String taskTime = task.getTaskTime();
            String taskDate = task.getTaskDate();
            Dialog editDialog = new Dialog(context);
            editDialog.setContentView(R.layout.edit_task_dialog);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            editDialog.setCancelable(false);
            editDialog.show();
            EditText taskNameEdit = editDialog.findViewById(R.id.taskName);
            EditText taskTimeEdit = editDialog.findViewById(R.id.taskTime);
            EditText taskDateEdit = editDialog.findViewById(R.id.taskDate);
            ImageView cancelBtn = editDialog.findViewById(R.id.cancelBtn);

            taskDateEdit.setRawInputType(InputType.TYPE_NULL);
            taskTimeEdit.setRawInputType(InputType.TYPE_NULL);

            taskNameEdit.setText(taskName);
            taskTimeEdit.setText(taskTime);
            taskDateEdit.setText(taskDate);

            taskDateEdit.setOnClickListener(v1 -> {
                showDatePicker(taskDateEdit);
            });
            taskTimeEdit.setOnClickListener(v1 -> {
                showTimePicker(taskTimeEdit);
            });

            editDialog.findViewById(R.id.editTaskBtn).setOnClickListener(v1 -> {
                String name = taskNameEdit.getText().toString();
                String time = taskTimeEdit.getText().toString();
                String date = taskDateEdit.getText().toString();
                if (name.isEmpty()) {
                    taskNameEdit.setError("Task name is required");
                    return;
                }
                if (time.isEmpty()) {
                    taskTimeEdit.setError("Task time is required");
                    return;
                }
                if (date.isEmpty()) {
                    taskDateEdit.setError("Task date is required");
                    return;
                }
                databaseHelper.updateTask(taskModel.getId(), name, time, date, false);
                taskModel.setTaskName(name);
                taskModel.setTaskTime(time);
                taskModel.setTaskDate(date);
                editDialog.dismiss();
                notifyDataSetChanged();
                if (!name.equals(taskName) || !time.equals(taskTime)){
                    notifyDataSetChanged();
                } else {
                    taskModels.remove(position);
                    notifyDataSetChanged();
                }
            });

            //cancel button
            cancelBtn.setOnClickListener(v1 -> {
                editDialog.dismiss();
            });

        });

        holder.delete.setOnClickListener(v -> {
            databaseHelper.deleteTask(taskModel.getId());
            taskModels.remove(position);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return taskModels.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskTime, taskDate;
        ImageView taskStatus;
        ImageView delete, edit;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskStatus = itemView.findViewById(R.id.statusIcon);
            delete = itemView.findViewById(R.id.deleteItem);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    private void showTimePicker(final EditText taskTimeInput) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minuteOfDay) -> {
                    // Convert 24-hour format to 12-hour format
                    String amPm;
                    if (hourOfDay >= 12) {
                        amPm = "PM";
                        hourOfDay -= 12;
                    } else {
                        amPm = "AM";
                    }
                    // Handle midnight (12 AM) and noon (12 PM)
                    if (hourOfDay == 0) {
                        hourOfDay = 12;
                    }
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minuteOfDay, amPm);
                    taskTimeInput.setText(selectedTime);
                }, hour, minute, false); // Use 12-hour format

        timePickerDialog.show();
    }

    private void showDatePicker(final EditText taskDateInput) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, dayOfMonth1) -> {
                    String selectedDate = (selectedMonth + 1) + "/" + dayOfMonth1 + "/" + selectedYear;
                    taskDateInput.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
}
