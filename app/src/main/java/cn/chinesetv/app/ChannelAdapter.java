package cn.chinesetv.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

final class ChannelAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Channel> channels;
    private Channel playingChannel;

    ChannelAdapter(Context context, List<Channel> channels) {
        inflater = LayoutInflater.from(context);
        this.channels = channels;
    }

    void setPlayingChannel(Channel channel) {
        playingChannel = channel;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView instanceof TextView) {
            view = (TextView) convertView;
        } else {
            view = (TextView) inflater.inflate(R.layout.channel_item, parent, false);
        }
        view.setText((position + 1) + ".  " + getItem(position).name);
        boolean playing = getItem(position) == playingChannel;
        view.setSelected(playing);
        view.setTextColor(playing ? Color.rgb(255, 224, 149) : Color.WHITE);
        return view;
    }
}
