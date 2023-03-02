package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.WeatherGetSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ExampleViewHolder>{

    private Context mContext;
    private ArrayList<WeatherGetSet> People_MemList_ArrayList;
    //new line
    private ArrayList<WeatherGetSet> People_MemArrayListFull;
    //end
    private static OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ActiveAdapter(Context context, ArrayList<WeatherGetSet> camplistArrayList) {
        mContext = context;
        People_MemList_ArrayList = camplistArrayList;
        //new line
        People_MemArrayListFull = new ArrayList<>(camplistArrayList);
        //end

    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // View v = LayoutInflater.from(mContext).inflate(R.layout.people_memlist_layout, parent, false);
        View v = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {

       // final WeatherGetSet myListData = People_MemList_ArrayList.get(position);

        WeatherGetSet currentItem = People_MemList_ArrayList.get(position);

        Integer intValue = Integer.valueOf((int) Math.round(Float.parseFloat(People_MemList_ArrayList.get(position).getTemp())));
       // tv_temperature.setText(""+intValue + "\u2103");
        holder.status.setText(""+intValue + "\u2103");

       /* long dv = Long.parseLong(currentItem.getDate());// its need to be in milisecond
        Date df = new java.util.Date(dv);*/
     //   String vv = new SimpleDateFormat("dd, MMM").format(df);


        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "E, ha";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(currentItem.getDate());
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.date.setText(str);

        String iconUrl = "https://openweathermap.org/img/w/" + currentItem.getIcon() + ".png";

        //Picasso.with(context).load(iconUrl).into(yourImageView);
        Glide.with(mContext)
                .load(iconUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageView);

        System.out.println("Date is: in adapter: "+ currentItem.getDate());

        /*WeatherGetSet currentItem = People_MemList_ArrayList.get(position);

        String date = currentItem.getDate();
        String image = currentItem.getIcon();
        String stauts = currentItem.getMain();


        // holder.mIcon.setText(currentItem.getIcon().substring(0, 1));
        holder.status.setText(stauts);

        long dv = Long.parseLong(date)*1000;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        String vv = new SimpleDateFormat("dd, MMM").format(df);
        holder.date.setText(vv);

        String iconUrl = "https://openweathermap.org/img/w/" + image + ".png";

        //Picasso.with(context).load(iconUrl).into(yourImageView);
        Glide.with(mContext)
                .load(iconUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageView);*/

       /* holder.status.setText(People_MemList_ArrayList.get(position).getMain().toString());
        long dv = Long.parseLong(People_MemList_ArrayList.get(position).getDate())*1000;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        String vv = new SimpleDateFormat("dd, MMM").format(df);

        holder.date.setText(vv);

        String image = People_MemList_ArrayList.get(position).getIcon();
        String iconUrl = "https://openweathermap.org/img/w/" + image + ".png";

        Glide.with(mContext)
                .load(iconUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageView);*/


    }

    @Override
    public int getItemCount() {
        return People_MemList_ArrayList.size();
    }

    //New Method for getting count of filtered list
    public void FilteredList(List<WeatherGetSet> newList){

        People_MemList_ArrayList = new ArrayList<>();
        People_MemList_ArrayList.addAll(newList);
        notifyDataSetChanged();
    }

   /* @Override
    public Filter getFilter() {
        return (Filter) exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<WeatherGetSet> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(People_MemArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (WeatherGetSet item : People_MemArrayListFull) {
                    if (item.getMain().toLowerCase().contains(filterPattern) || item.getDate().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            People_MemList_ArrayList.clear();
            People_MemList_ArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };*/

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView status, date;
        ImageView imageView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            //MemName = itemView.findViewById(R.id.tv_people_memName);
            //MemId = itemView.findViewById(R.id.tv_people_memId);


            status = itemView.findViewById(R.id.tv_main);
            date = itemView.findViewById(R.id.tv_date);
            imageView = itemView.findViewById(R.id.iv_icon);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
    public WeatherGetSet getItem (int position) {
        return People_MemList_ArrayList.get(position);
    }
}
