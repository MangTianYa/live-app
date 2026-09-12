package cn.chinesetv.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends Activity {
    private static final String IPV6_PLAYLIST_URL = "https://live.zbds.top/tv/iptv6.m3u";
    private static final String IPV4_PLAYLIST_URL = "https://live.zbds.top/tv/iptv4.m3u";
    private static final int SWIPE_DISTANCE_DP = 64;
    private static final int SWIPE_VELOCITY_DP = 200;
    private static final String PLAYER_PREFERENCES = "player_preferences";
    private static final String DISPLAY_MODE_KEY = "display_mode";
    private static final String LAST_CHANNEL_URL_KEY = "last_channel_url";
    private static final String LAST_CHANNEL_NAME_KEY = "last_channel_name";

    private final List<Channel> allChannels = new ArrayList<Channel>();
    private final List<Channel> visibleChannels = new ArrayList<Channel>();
    private AspectRatioVideoView videoView;
    private View channelPanel;
    private View infoBar;
    private ListView channelList;
    private EditText searchView;
    private TextView statusView;
    private Button retryButton;
    private Button displayModeButton;
    private ChannelAdapter adapter;
    private Channel playingChannel;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        videoView = (AspectRatioVideoView) findViewById(R.id.video);
        channelPanel = findViewById(R.id.channel_panel);
        infoBar = findViewById(R.id.info_bar);
        channelList = (ListView) findViewById(R.id.channel_list);
        searchView = (EditText) findViewById(R.id.channel_search);
        statusView = (TextView) findViewById(R.id.status);
        retryButton = (Button) findViewById(R.id.retry_button);
        displayModeButton = (Button) findViewById(R.id.display_mode_button);

        int savedDisplayMode = getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE)
                .getInt(DISPLAY_MODE_KEY, AspectRatioVideoView.MODE_FIT);
        setDisplayMode(savedDisplayMode);

        configureTouchControls();
        adaptLayoutToScreen();

        adapter = new ChannelAdapter(this, visibleChannels);
        channelList.setAdapter(adapter);
        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playChannel(position);
                showChannelPanel(false);
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                filterChannels(text.toString());
            }

            @Override
            public void afterTextChanged(Editable text) {
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadChannels();
            }
        });
        displayModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDisplayModeDialog();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.setVideoDimensions(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
                mediaPlayer.start();
                if (playingChannel != null) {
                    statusView.setText(getString(R.string.playing, playingChannel.name));
                }
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                statusView.setText(R.string.play_failed);
                infoBar.setVisibility(View.VISIBLE);
                return true;
            }
        });

        loadChannels();
    }

    private void showDisplayModeDialog() {
        final String[] labels = getResources().getStringArray(R.array.display_modes);
        final int selectedMode = getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE)
                .getInt(DISPLAY_MODE_KEY, AspectRatioVideoView.MODE_FIT);
        new AlertDialog.Builder(this)
                .setTitle(R.string.display_mode_title)
                .setSingleChoiceItems(labels, selectedMode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setDisplayMode(which);
                        getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE).edit()
                                .putInt(DISPLAY_MODE_KEY, which)
                                .apply();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void setDisplayMode(int mode) {
        if (mode < AspectRatioVideoView.MODE_FIT || mode > AspectRatioVideoView.MODE_4_3) {
            mode = AspectRatioVideoView.MODE_FIT;
        }
        videoView.setDisplayMode(mode);
        String[] labels = getResources().getStringArray(R.array.display_modes);
        displayModeButton.setText(getString(R.string.display_mode_button, labels[mode]));
    }

    private void configureTouchControls() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float minimumDistance = SWIPE_DISTANCE_DP * metrics.density;
        final float minimumVelocity = SWIPE_VELOCITY_DP * metrics.density;
        final GestureDetector gestures = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent event) {
                        return true;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent event) {
                        return videoView.performClick();
                    }

                    @Override
                    public boolean onFling(MotionEvent start, MotionEvent end,
                                           float velocityX, float velocityY) {
                        float distanceY = end.getY() - start.getY();
                        float distanceX = end.getX() - start.getX();
                        if (Math.abs(distanceY) < minimumDistance
                                || Math.abs(distanceY) <= Math.abs(distanceX)
                                || Math.abs(velocityY) < minimumVelocity) {
                            return false;
                        }
                        changeChannel(distanceY < 0 ? 1 : -1);
                        return true;
                    }
                });
        videoView.setClickable(true);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChannelPanel(channelPanel.getVisibility() != View.VISIBLE);
            }
        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return gestures.onTouchEvent(event);
            }
        });
    }

    private void adaptLayoutToScreen() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int smallestWidthDp = getResources().getConfiguration().smallestScreenWidthDp;
        int minimumWidth = getResources().getDimensionPixelSize(R.dimen.panel_min_width);
        int maximumWidth = getResources().getDimensionPixelSize(R.dimen.panel_max_width);
        float panelRatio = smallestWidthDp < 600 ? 0.46f : smallestWidthDp < 960 ? 0.40f : 0.34f;
        float maximumRatio = smallestWidthDp < 600 ? 0.52f : 0.48f;
        int targetWidth = Math.round(metrics.widthPixels * panelRatio);
        targetWidth = Math.max(minimumWidth, Math.min(maximumWidth, targetWidth));
        targetWidth = Math.min(targetWidth, Math.round(metrics.widthPixels * maximumRatio));

        ViewGroup.LayoutParams layoutParams = channelPanel.getLayoutParams();
        if (layoutParams.width != targetWidth) {
            layoutParams.width = targetWidth;
            channelPanel.setLayoutParams(layoutParams);
        }
        updateInfoBarMargin(channelPanel.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adaptLayoutToScreen();
    }

    private void loadChannels() {
        if (loading) {
            return;
        }
        loading = true;
        retryButton.setEnabled(false);
        statusView.setText(R.string.loading);

        new AsyncTask<Void, Void, List<Channel>>() {
            private boolean failed;

            @Override
            protected List<Channel> doInBackground(Void... ignored) {
                try {
                    return M3uLoader.load(IPV4_PLAYLIST_URL);
                } catch (Exception ipv4Error) {
                    try {
                        return M3uLoader.load(IPV6_PLAYLIST_URL);
                    } catch (Exception ipv6Error) {
                        failed = true;
                        return M3uLoader.fallbackChannels();
                    }
                }
            }

            @Override
            protected void onPostExecute(List<Channel> loadedChannels) {
                if (isFinishing()) {
                    return;
                }
                loading = false;
                retryButton.setEnabled(true);
                allChannels.clear();
                allChannels.addAll(loadedChannels);
                filterChannels(searchView.getText().toString());
                statusView.setText(failed ? R.string.load_failed : R.string.channels);
                int savedChannelPosition = findSavedChannelPosition();
                if (savedChannelPosition >= 0) {
                    playChannel(savedChannelPosition);
                } else if (!visibleChannels.isEmpty()) {
                    channelList.setSelection(0);
                    channelList.requestFocus();
                }
            }
        }.execute();
    }

    private void playChannel(int position) {
        if (position < 0 || position >= visibleChannels.size()) {
            return;
        }
        playingChannel = visibleChannels.get(position);
        getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE).edit()
                .putString(LAST_CHANNEL_URL_KEY, playingChannel.url)
                .putString(LAST_CHANNEL_NAME_KEY, playingChannel.name)
                .apply();
        adapter.setPlayingChannel(playingChannel);
        channelList.setItemChecked(position, true);
        channelList.setSelection(position);
        statusView.setText(getString(R.string.playing, playingChannel.name));
        infoBar.setVisibility(View.VISIBLE);
        videoView.stopPlayback();
        videoView.setVideoURI(Uri.parse(playingChannel.url));
        videoView.requestFocus();
        videoView.start();
    }

    private int findSavedChannelPosition() {
        SharedPreferences preferences = getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE);
        String savedUrl = preferences.getString(LAST_CHANNEL_URL_KEY, null);
        String savedName = preferences.getString(LAST_CHANNEL_NAME_KEY, null);
        for (int position = 0; position < visibleChannels.size(); position++) {
            Channel channel = visibleChannels.get(position);
            if (savedUrl != null && savedUrl.equals(channel.url)) {
                return position;
            }
        }
        for (int position = 0; position < visibleChannels.size(); position++) {
            Channel channel = visibleChannels.get(position);
            if (savedName != null && savedName.equals(channel.name)) {
                return position;
            }
        }
        return -1;
    }

    private void changeChannel(int direction) {
        if (visibleChannels.isEmpty()) {
            return;
        }
        int current = visibleChannels.indexOf(playingChannel);
        int next = current < 0 ? 0
                : (current + direction + visibleChannels.size()) % visibleChannels.size();
        playChannel(next);
    }

    private void filterChannels(String query) {
        String normalizedQuery = query.trim().toLowerCase();
        visibleChannels.clear();
        for (Channel channel : allChannels) {
            if (normalizedQuery.length() == 0
                    || channel.name.toLowerCase().contains(normalizedQuery)) {
                visibleChannels.add(channel);
            }
        }
        adapter.notifyDataSetChanged();
        int playingPosition = visibleChannels.indexOf(playingChannel);
        adapter.setPlayingChannel(playingChannel);
        if (playingPosition >= 0) {
            channelList.setItemChecked(playingPosition, true);
            channelList.setSelection(playingPosition);
        } else {
            channelList.clearChoices();
            if (!visibleChannels.isEmpty()) {
                channelList.setSelection(0);
            }
        }
        channelList.setEmptyView(findViewById(R.id.empty_channels));
    }

    private void showChannelPanel(boolean show) {
        channelPanel.setVisibility(show ? View.VISIBLE : View.GONE);
        updateInfoBarMargin(show);
        if (show) {
            infoBar.setVisibility(View.VISIBLE);
            int playingPosition = visibleChannels.indexOf(playingChannel);
            int selection = playingPosition >= 0 ? playingPosition : 0;
            channelList.setSelection(selection);
            channelList.requestFocus();
        } else {
            videoView.requestFocus();
        }
    }

    private void updateInfoBarMargin(boolean panelVisible) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) infoBar.getLayoutParams();
        layoutParams.leftMargin = panelVisible ? channelPanel.getLayoutParams().width : 0;
        infoBar.setLayoutParams(layoutParams);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN || event.getRepeatCount() > 0) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_GUIDE) {
            showChannelPanel(channelPanel.getVisibility() != View.VISIBLE);
            return true;
        }
        if (channelPanel.getVisibility() != View.VISIBLE) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_UP) {
                changeChannel(-1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN) {
                changeChannel(1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                showChannelPanel(true);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (channelPanel.getVisibility() == View.VISIBLE && playingChannel != null) {
            showChannelPanel(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }
}
