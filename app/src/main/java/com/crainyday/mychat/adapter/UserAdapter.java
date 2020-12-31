package com.crainyday.mychat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crainyday.mychat.R;
import com.crainyday.mychat.entity.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.myViewHolder> {
    private List<User> userList;
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        final myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        User user = userList.get(position);
        // 注意: 这里需要显示图片
//        holder.userImage.setImageResource(Integer.parseInt(user.getImage()));
        holder.userImage.setImageResource(Integer.parseInt(user.getImage()));
        holder.userName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        ImageView userImage;
        TextView userName;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = (ImageView)itemView.findViewById(R.id.user_image);
            userName = (TextView)itemView.findViewById(R.id.user_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Item的点击事件
            if(itemClickListener!=null){
                itemClickListener.onItemClick(view, userList.get(getLayoutPosition()));
            }
        }
    }

    private ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, User user);
    }
}
