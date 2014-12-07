package com.automessaging.readContacts;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.automessaging.R;
import com.automessaging.common.Astat;

public class ContanctAdapter extends ArrayAdapter<ContactBean> {
	
	private Activity activity;
	private List<ContactBean> items;
	private int row;
	private ContactBean objBean;
	private boolean isEdit;
	private ArrayList<ContactBean> arraylist;

	public ContanctAdapter(Activity act, int row, List<ContactBean> items) {
		super(act, row, items);

		this.activity = act;
		this.row = row;
		this.items = items;
		this.arraylist = new ArrayList<ContactBean>();
        this.arraylist.addAll(items);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = convertView == null ? new ViewHolder(): (ViewHolder) convertView.getTag();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(row, null);
			holder.tvname = (TextView) convertView.findViewById(R.id.tvname);
			holder.tvPhoneNo = (TextView) convertView.findViewById(R.id.tvphone);
			holder.iv = (ImageView)convertView.findViewById(R.id.contacts_imageview);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			
		}
		

		if ((items == null) || ((position + 1) > items.size()))
			return convertView;

		objBean = items.get(position);

		
		if (holder.tvname != null && null != objBean.getName()
				&& objBean.getName().trim().length() > 0) {
			holder.tvname.setText(Html.fromHtml(objBean.getName()));
		}
		if (holder.tvPhoneNo != null && null != objBean.getPhoneNo()
				&& objBean.getPhoneNo().trim().length() > 0) {
			holder.tvPhoneNo.setText(Html.fromHtml(objBean.getPhoneNo()));
		}
		
		if (isEdit) {
			holder.iv.setVisibility(View.VISIBLE);
			holder.iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (Astat.contactCheckArray!= null && Astat.contactCheckArray[position]) {
						Astat.contactCheckArray[position] = false;
						notifyDataSetChanged();
					} else {
						Astat.contactCheckArray[position] = true;
						notifyDataSetChanged();
					}
				}
			});

			if (Astat.contactCheckArray != null && Astat.contactCheckArray[position]) {
				holder.iv.setImageResource(R.drawable.setting_check);
			} else {
				holder.iv.setImageResource(R.drawable.setting_check_box_bg);
			}
		} else {
			holder.iv.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	public void setEditMode(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public class ViewHolder {
		public ImageView iv;
		public TextView tvname, tvPhoneNo;
	}

	public void filter(String charText ) {
		// TODO Auto-generated method stub
		charText = charText.toLowerCase(Locale.getDefault());
		items.clear();
        if (charText.length() == 0) {
        	items.addAll(arraylist);
        } 
        else 
        {
            for (ContactBean ob : arraylist) 
            {
                if (ob.getName().toLowerCase(Locale.getDefault()).contains(charText)) 
                {
                	items.add(ob);
                }
            }
        }
        notifyDataSetChanged();
    
	}

}
