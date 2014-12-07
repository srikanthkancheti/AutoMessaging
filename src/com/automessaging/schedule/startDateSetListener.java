package com.automessaging.schedule;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class startDateSetListener implements OnDateSetListener {

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		int mYear = year;
        int mMonth = monthOfYear+1;
        int mDay = dayOfMonth;
       // int month = monthOfYear + 1;
        String formattedMonth = "" + mMonth;
        String formattedDayOfMonth = "" + mDay;
        if(mMonth < 10){

        	formattedMonth = "0" + mMonth;
        }
        if(mDay < 10){

        	formattedDayOfMonth  = "0" + mDay ;
        }
        
        ScheduleMessage.schedule_date_edt.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(formattedDayOfMonth).append("-").append(formattedMonth).append("-")
                .append(mYear).append(" "));
        System.out.println(ScheduleMessage.schedule_date_edt.getText().toString());

	}

}
