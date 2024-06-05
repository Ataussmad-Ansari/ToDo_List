package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Calendar selectedDate;
    private Calendar taskSelectedDate;
    ArrayList<TaskModel> tasks = new ArrayList<>();
    ActivityMainBinding binding;
    TaskAdapter adapter;
    DatabaseHelper databaseHelper;
    SimpleDateFormat todayTask, taskDate, fullDateFormat, dateFormat, dayFormat, dateFormat1;
    TaskModel taskModel;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Define the date format
        // Set the current date in the TextView
        dateFormat1 = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
        binding.setTodayDate.setText(dateFormat1.format(new Date()));
        dayFormat = new SimpleDateFormat("E", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        fullDateFormat = new SimpleDateFormat("EEEE MMMM dd", Locale.getDefault());
        taskDate = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
        todayTask = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());

        createDateLayout();
        //hide drawer
        binding.view.setVisibility(View.GONE);
        binding.cancelBtn.setVisibility(View.GONE);
        binding.imageView.setVisibility(View.GONE);
        binding.textView2.setVisibility(View.GONE);
        binding.textView3.setVisibility(View.GONE);
        binding.allTaskRV.setVisibility(View.GONE);

        binding.add.setOnClickListener(v -> {
            showAddTaskDialog();
        });
        //calender
        binding.calender.setOnClickListener(v -> {
            showCalenderDialog();
        });
        binding.menu.setOnClickListener(v -> {
            binding.view.setVisibility(View.VISIBLE);
            binding.cancelBtn.setVisibility(View.VISIBLE);
            binding.imageView.setVisibility(View.VISIBLE);
            binding.textView2.setVisibility(View.VISIBLE);
            binding.textView3.setVisibility(View.VISIBLE);
            binding.allTaskRV.setVisibility(View.VISIBLE);
            allTask();
        });
        binding.cancelBtn.setOnClickListener(v -> {
            binding.view.setVisibility(View.GONE);
            binding.cancelBtn.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.GONE);
            binding.textView2.setVisibility(View.GONE);
            binding.textView3.setVisibility(View.GONE);
            binding.allTaskRV.setVisibility(View.GONE);
        });
        //End MainBody...
    }

    //Functions Start...

    private void showCalenderDialog() {
        // Get the current calendar instance
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, dayOfMonth1) -> {
                    // Update the selected date
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, dayOfMonth1);

                    // Format the selected date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMMM dd", Locale.getDefault());
                    String SelectedDate = dateFormat.format(selectedDate.getTime());
                    binding.setTodayDate.setText(SelectedDate);

                    String selectedDateStr = (selectedMonth + 1) + "/" + dayOfMonth1 + "/" + selectedYear;

                    // Fetch tasks for the selected date
                    fetchTasksForDate(selectedDateStr);

                    // Refresh the date layout to update highlights
                    refreshDateLayout(new SimpleDateFormat("E", Locale.getDefault()),
                            new SimpleDateFormat("dd", Locale.getDefault()),
                            fullDateFormat,
                            taskDate,
                            selectedDate.get(Calendar.DAY_OF_MONTH));
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    /*private void showCalenderDialog() {
        // Get the current calendar instance
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, dayOfMonth1) -> {
                    String selectedDate = (selectedMonth + 1) + "/" + dayOfMonth1 + "/" + selectedYear;
                    fetchTasksForDate(selectedDate);

                    Calendar selectedDateCalendar = Calendar.getInstance();
                    selectedDateCalendar.set(selectedYear, selectedMonth, dayOfMonth1);

                    // Format the selected date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMMM dd", Locale.getDefault());
                    String SelectedDate = dateFormat.format(selectedDateCalendar.getTime());
                    binding.setTodayDate.setText(SelectedDate);


                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }*/

    private void createDateLayout() {

        String currentDate = todayTask.format(new Date());
        fetchTasksForDate(currentDate);

        // Get the current calendar instance
        Calendar calendar = Calendar.getInstance();
        // int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int today = calendar.get(Calendar.DAY_OF_MONTH);

        // Set initial view to show the previous 3 days, the current day, and the next 3 days
        calendar.add(Calendar.DAY_OF_MONTH, -3); // Move back 3 days

        // Create TextViews for each day of the week
        for (int i = 0; i < 7; i++) {
            // Create a vertical LinearLayout for each day
            LinearLayout dayLayout = new LinearLayout(this);
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams dayLayoutParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            dayLayoutParams.setMargins(4, 0, 4, 0);
            dayLayout.setLayoutParams(dayLayoutParams);

            // Create TextView for day name (e.g., S, M, ...)
            TextView dayNameTextView = new TextView(this);
            String dayName = dayFormat.format(calendar.getTime()).substring(0, 1);
            dayNameTextView.setText(dayName);
            dayNameTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            dayNameTextView.setTextSize(16);
            dayNameTextView.setPadding(0, 0, 0, 8);
            dayNameTextView.setGravity(Gravity.CENTER);
            dayLayout.addView(dayNameTextView);

            // Create TextView for date (e.g., 1, 2, ...)
            TextView dateTextView = new TextView(this);
            dateTextView.setText(dateFormat.format(calendar.getTime()));
            dateTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            dateTextView.setTextSize(16);
            dateTextView.setPadding(0, 0, 0, 8);
            dateTextView.setGravity(Gravity.CENTER);
            dayLayout.addView(dateTextView);

            // Highlight the current day
            if (calendar.get(Calendar.DAY_OF_MONTH) == today) {
                dayNameTextView.setTextColor(ContextCompat.getColor(this, R.color.white)); // Highlight current day
                dateTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else if (calendar.get(Calendar.DAY_OF_MONTH) == today - 1) {
                dayNameTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor)); // Highlight previous day
                dateTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            } else if (calendar.get(Calendar.DAY_OF_MONTH) == today + 1) {
                dayNameTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor)); // Highlight next day
                dateTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            }

            // Add OnClickListener to each dayLayout to set the full date format and highlight the selected date
            final Calendar clickedDate = (Calendar) calendar.clone();
            dayLayout.setOnClickListener(v -> {
                // Update the selected date
                selectedDate = (Calendar) clickedDate.clone();

                // Update the full date TextView
                String fullDate = fullDateFormat.format(selectedDate.getTime());
                binding.setTodayDate.setText(fullDate);

                taskSelectedDate = (Calendar) clickedDate.clone();
                String a = taskDate.format(taskSelectedDate.getTime());

                fetchTasksForDate(a);

                // Refresh the date layout to update highlights
                refreshDateLayout(dayFormat, dateFormat, fullDateFormat, taskDate, today);
            });

            // Add the day layout to the parent layout
            binding.dateLayout.addView(dayLayout);

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void refreshDateLayout(SimpleDateFormat dayFormat, SimpleDateFormat dateFormat, SimpleDateFormat fullDateFormat, SimpleDateFormat taskDate, int today) {
        // Remove all views from dateLayout
        binding.dateLayout.removeAllViews();

        // Get the current calendar instance and move back 3 days
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.add(Calendar.DAY_OF_MONTH, -3);

        // Create TextViews for each day of the week
        for (int i = 0; i < 7; i++) {
            // Create a vertical LinearLayout for each day
            LinearLayout dayLayout = new LinearLayout(this);
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams dayLayoutParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            dayLayoutParams.setMargins(4, 0, 4, 0);
            dayLayout.setLayoutParams(dayLayoutParams);

            // Create TextView for day name (e.g., S, M, ...)
            TextView dayNameTextView = new TextView(this);
            String dayName = dayFormat.format(calendar.getTime()).substring(0, 1);
            dayNameTextView.setText(dayName);
            dayNameTextView.setTextSize(16);
            dayNameTextView.setPadding(0, 0, 0, 8);
            dayNameTextView.setGravity(Gravity.CENTER);
            dayLayout.addView(dayNameTextView);

            // Create TextView for date (e.g., 1, 2, ...)
            TextView dateTextView = new TextView(this);
            dateTextView.setText(dateFormat.format(calendar.getTime()));
            dateTextView.setTextSize(16);
            dateTextView.setPadding(0, 0, 0, 8);
            dateTextView.setGravity(Gravity.CENTER);
            dayLayout.addView(dateTextView);

            // Highlight the selected date
            if (calendar.equals(selectedDate)) {
                dayNameTextView.setTextColor(ContextCompat.getColor(this, R.color.white)); // Highlight selected date
                dateTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                dayNameTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor)); // Other dates in light gray
                dateTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            }

            // Add OnClickListener to each dayLayout to set the full date format and highlight the selected date
            final Calendar clickedDate = (Calendar) calendar.clone();
            dayLayout.setOnClickListener(v -> {
                // Update the selected date
                selectedDate = (Calendar) clickedDate.clone();

                // Update the full date TextView
                String fullDate = fullDateFormat.format(selectedDate.getTime());
                binding.setTodayDate.setText(fullDate);

                taskSelectedDate = (Calendar) clickedDate.clone();
                String a = taskDate.format(taskSelectedDate.getTime());

                fetchTasksForDate(a);

                // Refresh the date layout to update highlights
                refreshDateLayout(dayFormat, dateFormat, fullDateFormat, taskDate, today);
            });

            // Add the day layout to the parent layout
            binding.dateLayout.addView(dayLayout);

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @SuppressLint("MissingInflatedId")
    private void showAddTaskDialog() {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText taskNameInput = dialogView.findViewById(R.id.editTextTaskName);
        EditText taskTimeInput = dialogView.findViewById(R.id.editTextTaskTime);
        EditText taskDateInput = dialogView.findViewById(R.id.editTextTaskDate);
        Button addTaskButton = dialogView.findViewById(R.id.buttonAddTask);
        ImageView cancelButton = dialogView.findViewById(R.id.cancelBtn);

        AlertDialog dialog = builder.create();*/
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_task_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        EditText taskNameInput = dialog.findViewById(R.id.taskName);
        EditText taskTimeInput = dialog.findViewById(R.id.taskTime);
        EditText taskDateInput = dialog.findViewById(R.id.taskDate);
        ImageView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        Button addTaskButton = dialog.findViewById(R.id.addTaskBtn);

        taskDateInput.setRawInputType(InputType.TYPE_NULL);
        taskTimeInput.setRawInputType(InputType.TYPE_NULL);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        // Set OnClickListener for the Task Time EditText to show time picker
        taskTimeInput.setOnClickListener(v -> showTimePicker(taskTimeInput));

        // Set OnClickListener for the Task Date EditText to show date picker
        taskDateInput.setOnClickListener(v -> showDatePicker(taskDateInput));

        addTaskButton.setOnClickListener(v -> {
            // Implement your logic to add task here
            String taskName = taskNameInput.getText().toString();
            String taskTime = taskTimeInput.getText().toString();
            String taskDate = taskDateInput.getText().toString();

            if (taskName.isEmpty()) {
                taskNameInput.setError("Task name is required");
                return;
            }

            if (taskTime.isEmpty()) {
                taskTimeInput.setError("Task time is required");
                return;
            }

            if (taskDate.isEmpty()) {
                taskDateInput.setError("Task date is required");
                return;
            }

            // Insert task into database
            id = databaseHelper.insertTask(taskName, taskTime, taskDate, false);

            if (id != -1) {
                // Update UI
                TaskModel task;
                String currentDate = todayTask.format(new Date());
                if (taskDate.equals(currentDate)) {
//                    task = new TaskModel((int) id, taskName, taskTime, taskDate, false);
                    fetchTasksForDate(taskDate);
//                    tasks.add(task);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Task added for date: " + taskDate, Toast.LENGTH_SHORT).show();
                    fetchTasksForDate(taskDate);
                    adapter.notifyDataSetChanged();
                }
                // Dismiss the dialog
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showTimePicker(final EditText taskTimeInput) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, dayOfMonth1) -> {
                    String selectedDate = (selectedMonth + 1) + "/" + dayOfMonth1 + "/" + selectedYear;
                    taskDateInput.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void fetchTasksForDate(String date) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, tasks);
        binding.recyclerView.setAdapter(adapter);
        // Clear the current tasks list
        tasks.clear();

        // Query the database for tasks on the selected date
        @SuppressLint("Range")
        Cursor cursor = databaseHelper.getTasksForDate(date);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskTime = cursor.getString(2);
                String taskDate = cursor.getString(3);
                // Retrieve the status column value
                int statusInt = cursor.getInt(cursor.getInt(4));
                boolean status = (statusInt == 0); // Convert int to boolean
                TaskModel task = new TaskModel(id, taskName, taskTime, taskDate, status);
                tasks.add(task);
                Log.d("status", taskName + " : " + status);
            }
            cursor.close();
        } else {
            String currentDate = todayTask.format(new Date());
            if (date.equals(currentDate)) {
                Toast.makeText(this, "Add Today tasks", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No tasks found for this date", Toast.LENGTH_SHORT).show();
            }
        }
        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }

    private void allTask() {
        binding.allTaskRV.setLayoutManager(new LinearLayoutManager(this));
        AllTaskAdapter allTaskAdapter = new AllTaskAdapter(this, tasks);
        binding.allTaskRV.setAdapter(allTaskAdapter);

        tasks.clear();

        Cursor cursor = databaseHelper.getInfo();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskTime = cursor.getString(2);
                String taskDate = cursor.getString(3);
                TaskModel task = new TaskModel(id, taskName, taskTime, taskDate);
                tasks.add(task);
            }
        } else {
            Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
        }

    }

}