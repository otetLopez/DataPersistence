package com.f19.databasedemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EmployeeAdapter extends ArrayAdapter {
    Context mContext;
    int layoutRes;
    List<Employee> employees;
    SQLiteDatabase mDataBase;

    public EmployeeAdapter(Context mContext, int resource, List<Employee> employees, SQLiteDatabase mDataBase) {
        super(mContext, resource, employees);
        this.mContext = mContext;
        this.layoutRes = resource;
        this.employees = employees;
        this.mDataBase = mDataBase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View v = layoutInflater.inflate(layoutRes, null);

        TextView tvName = v.findViewById(R.id.tv_name);
        TextView tvSalary = v.findViewById(R.id.tv_salary);
        TextView tvDepartment = v.findViewById(R.id.tv_department);
        TextView tvJoiningDate = v.findViewById(R.id.tv_joiningdate);
        
        final Employee employee = employees.get(position);
        tvName.setText(employee.getName());
        tvSalary.setText(String.valueOf(employee.getSalary()));
        tvDepartment.setText(employee.getDept());
        tvJoiningDate.setText(employee.getJoiningdate());
        
        
        v.findViewById(R.id.btn_edit_employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(employee);
            }
        });

        v.findViewById(R.id.btn_delete_employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEmployee(employee);
            }
        });

        return v;
    }

    private void deleteEmployee(final Employee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String sql = "DELETE FROM employees WHERE id=?";
                mDataBase.execSQL(sql, new Integer[]{employee.getId()});
                loadEmployees();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateEmployee(final Employee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_layout_update_employee, null);
        builder.setView(v);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText etName = v.findViewById(R.id.editTextName);
        final EditText etsalary = v.findViewById(R.id.editTextSalary);
        final Spinner spinner = v.findViewById(R.id.spinnerDepartment);

        etName.setText(employee.getName());
        etsalary.setText(String.valueOf(employee.getSalary()));
        switch(employee.getDept()) {
            case "Technical":
                spinner.setSelection(0);
                break;
            case "Support":
                spinner.setSelection(1);
                break;
            case "Research and Development":
                spinner.setSelection(2);
                break;
            case "Marketing":
                spinner.setSelection(3);
                break;
            case "Human Resource":
                spinner.setSelection(4);
                break;
            default:
                break;
        }

        //spinner.setSelection();

        v.findViewById(R.id.btn_update_employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String salary = etsalary.getText().toString().trim();
                String dept = spinner.getSelectedItem().toString();

                if(name.isEmpty()) {
                    etName.setError("Name field is mandatory!");
                    etName.requestFocus();
                    return;
                }
                if(salary.isEmpty()) {
                    etsalary.setError("Salary field is mandatory!");
                    etsalary.requestFocus();
                    return;
                }
                String sql = "UPDATE employees SET name = ?, department = ? , salary = ?  WHERE id = ?";
                mDataBase.execSQL(sql, new String[]{name, dept, salary, String.valueOf(employee.getId())});
                Toast.makeText(mContext, "Employee updated", Toast.LENGTH_SHORT).show();
                loadEmployees();
                alertDialog.dismiss();
            }
        });
    }


    private void loadEmployees() {
        String sql = "SELECT * FROM employees";
        Cursor cursor = mDataBase.rawQuery(sql, null);
        employees.clear();
        if (cursor.moveToFirst()) {
            do {
                employees.add(new Employee(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();

            notifyDataSetChanged();
        }
    }
}
