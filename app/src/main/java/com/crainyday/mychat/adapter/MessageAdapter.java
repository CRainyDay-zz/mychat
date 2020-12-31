package com.crainyday.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crainyday.mychat.R;
import com.crainyday.mychat.activity.ChatActivity;
import com.crainyday.mychat.entity.Message;
import com.crainyday.mychat.entity.User;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.myViewHolder> {
    private List<Message> msgList;
    private Context context;

    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public MessageAdapter(Context context, List<Message> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    public void setMsgList(List<Message> msgList) {
        this.msgList = msgList;
    }

    @NonNull
    @Override
    public MessageAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);
        final myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        final myViewHolder myViewHolder = (MessageAdapter.myViewHolder)holder;
        final Message message = msgList.get(position);
        if(message.isMine()){
            myViewHolder.getLeftLayout().setVisibility(View.GONE);
            myViewHolder.getRightLayout().setVisibility(View.VISIBLE);
            if("text".equals(message.getContentsType())){
                myViewHolder.getRightImg().setVisibility(View.GONE);
                myViewHolder.getRightTextLayout().setVisibility(View.VISIBLE);
                myViewHolder.getRightText().setText(message.getContents());
            }else if ("image".equals(message.getContentsType())){
                myViewHolder.getRightTextLayout().setVisibility(View.GONE);
                myViewHolder.getRightImg().setImageBitmap(message.getBitmap());
            }
        }else{
            myViewHolder.getLeftLayout().setVisibility(View.VISIBLE);
            myViewHolder.getRightLayout().setVisibility(View.GONE);
            if("text".equals(message.getContentsType())){
                myViewHolder.getLeftImg().setVisibility(View.GONE);
                myViewHolder.getLeftTextLayout().setVisibility(View.VISIBLE);
                myViewHolder.getLeftText().setText(message.getContents());
            }else if ("image".equals(message.getContentsType())){
                myViewHolder.getLeftTextLayout().setVisibility(View.GONE);
                myViewHolder.getLeftImg().setImageBitmap(message.getBitmap());
            }
        }

        // Item的点击事件
        if(itemClickListener!=null){
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(myViewHolder.itemView, myViewHolder.getLayoutPosition());
                }
            });
        }

        // Item的点击事件
        if(itemLongClickListener!=null){
            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemLongClickListener.onItemLongClick(myViewHolder.itemView, myViewHolder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener {

        private LinearLayout leftLayout;
        private LinearLayout rightLayout;
        private LinearLayout leftTextLayout;
        private LinearLayout rightTextLayout;
        private ImageView leftImg;
        private ImageView rightImg;
        private TextView leftText;
        private TextView rightText;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayout = (LinearLayout)itemView.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout)itemView.findViewById(R.id.right_layout);
            leftTextLayout = (LinearLayout)itemView.findViewById(R.id.left_text_layout);
            rightTextLayout = (LinearLayout)itemView.findViewById(R.id.right_text_layout);
            leftImg = (ImageView)itemView.findViewById(R.id.left_img);
            rightImg = (ImageView)itemView.findViewById(R.id.right_img);
            leftText = (TextView)itemView.findViewById(R.id.left_msg);
            rightText = (TextView)itemView.findViewById(R.id.right_msg);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public LinearLayout getLeftLayout() {
            return leftLayout;
        }
        public LinearLayout getRightLayout() {
            return rightLayout;
        }
        public LinearLayout getLeftTextLayout() {
            return leftTextLayout;
        }
        public LinearLayout getRightTextLayout() {
            return rightTextLayout;
        }
        public ImageView getLeftImg() {
            return leftImg;
        }
        public ImageView getRightImg() {
            return rightImg;
        }
        public TextView getLeftText() {
            return leftText;
        }
        public TextView getRightText() {
            return rightText;
        }

        @Override
        public void onClick(View view) {
            // Item的点击事件
            if(itemClickListener!=null){
                itemClickListener.onItemClick(view, getLayoutPosition());
            }
        }
        @Override
        public boolean onLongClick(View view) {
            // Item的点击事件
            if(itemLongClickListener!=null){
                itemLongClickListener.onItemLongClick(view, getLayoutPosition());
            }
            return true;
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void setOnItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
}
