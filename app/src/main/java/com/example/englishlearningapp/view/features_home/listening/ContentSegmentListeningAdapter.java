package com.example.englishlearningapp.view.features_home.listening;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.io.IOException;
import java.util.List;

public class ContentSegmentListeningAdapter extends RecyclerView.Adapter<ContentSegmentListeningAdapter.ViewHolder> {

    private final List<ContentSegmentListening> segments;
    private final Context context;
    private final String audioFilePath; // "listening/audio/xxx.mp3"
    private MediaPlayer segmentPlayer;

    // Trạng thái dịch cho từng item
    private final boolean[] translateStates;
    // Trạng thái repeat cho từng item
    private final boolean[] repeatStates;
    // Trạng thái highlight cho từng item
    private final boolean[] highlightStates;
    // Vị trí đang được repeat (-1 nếu không có)
    private int currentRepeatPosition = -1;

    public ContentSegmentListeningAdapter(List<ContentSegmentListening> segments, String audioFilePath) {
        this.segments = segments;
        this.context = null; // Sẽ được gán trong onCreateViewHolder
        this.audioFilePath = audioFilePath;
        this.translateStates = new boolean[segments.size()];
        this.repeatStates = new boolean[segments.size()];
        this.highlightStates = new boolean[segments.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_content_listening, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(segments.get(position), position, highlightStates[position]);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String payloadStr = (String) payload;
                    if ("highlight".equals(payloadStr)) {
                        holder.updateHighlight(true);
                        return;
                    } else if ("normal".equals(payloadStr)) {
                        holder.updateHighlight(false);
                        return;
                    }
                } else if (payload instanceof Boolean) {
                    holder.updateHighlight((Boolean) payload);
                    return;
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return segments.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        releaseSegmentPlayer();
    }

    private void releaseSegmentPlayer() {
        if (segmentPlayer != null) {
            segmentPlayer.stop();
            segmentPlayer.release();
            segmentPlayer = null;
        }
        currentRepeatPosition = -1;
    }

    // Phương thức để dừng repeat từ bên ngoài ViewHolder
    private void stopRepeat(int position) {
        if (position >= 0 && position < repeatStates.length) {
            repeatStates[position] = false;
        }
        currentRepeatPosition = -1;
        notifyItemChanged(position);

        if (segmentPlayer != null) {
            segmentPlayer.stop();
            segmentPlayer.release();
            segmentPlayer = null;
        }
    }

    // Phương thức để cập nhật highlight từ Activity
    public void updateHighlight(int position, boolean isHighlighted) {
        if (position >= 0 && position < highlightStates.length) {
            highlightStates[position] = isHighlighted;
            if (isHighlighted) {
                notifyItemChanged(position, "highlight");
            } else {
                notifyItemChanged(position, "normal");
            }
        }
    }

    // Phương thức để clear tất cả highlight
    public void clearAllHighlights() {
        for (int i = 0; i < highlightStates.length; i++) {
            if (highlightStates[i]) {
                highlightStates[i] = false;
                notifyItemChanged(i, "normal");
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpeaker, tvTextEn, tvTextVi;
        ImageButton btnTranslate, btnSpeaker, btnRepeat;
        Context context;
        private double currentStartSec, currentEndSec;
        private int currentPosition;
        private boolean isRepeatMode = false;
        private boolean isHighlighted = false;

        ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvSpeaker = itemView.findViewById(R.id.tv_speaker);
            tvTextEn = itemView.findViewById(R.id.tv_text_en);
            tvTextVi = itemView.findViewById(R.id.tv_text_vi);
            btnTranslate = itemView.findViewById(R.id.btn_translate);
            btnSpeaker = itemView.findViewById(R.id.btn_speaker);
            btnRepeat = itemView.findViewById(R.id.btn_repeat);
        }

        void bind(ContentSegmentListening segment, int position, boolean isHighlighted) {
            this.currentPosition = position;
            this.isHighlighted = isHighlighted;

            // Áp dụng highlight state
            applyHighlightState();

            // === SPEAKER ===
            if (segment.speaker != null && !segment.speaker.trim().isEmpty()) {
                tvSpeaker.setText(segment.speaker);
                tvSpeaker.setVisibility(View.VISIBLE);
            } else {
                tvSpeaker.setVisibility(View.GONE);
            }

            // === TEXT EN ===
            tvTextEn.setText(segment.text_en);

            // === TEXT VI (ẩn ban đầu) ===
            if (segment.text_vi != null && !segment.text_vi.trim().isEmpty()) {
                tvTextVi.setText(segment.text_vi);
                tvTextVi.setVisibility(translateStates[position] ? View.VISIBLE : View.GONE);
            } else {
                tvTextVi.setVisibility(View.GONE);
            }

            // === NÚT DỊCH (Toggle) ===
            btnTranslate.setImageResource(translateStates[position] ? R.drawable.ic_tab_close : R.drawable.ic_g_translate);
            btnTranslate.setOnClickListener(v -> {
                if (segment.text_vi == null || segment.text_vi.trim().isEmpty()) return;

                translateStates[position] = !translateStates[position];
                tvTextVi.setVisibility(translateStates[position] ? View.VISIBLE : View.GONE);
                btnTranslate.setImageResource(translateStates[position] ? R.drawable.ic_tab_close : R.drawable.ic_g_translate);
            });

            // === NÚT PHÁT ĐOẠN ===
            btnSpeaker.setOnClickListener(v -> {
                // Nếu đang repeat ở vị trí này thì dừng repeat trước
                if (repeatStates[position]) {
                    stopRepeat(position);
                }
                playSegment(segment.start, segment.end, false, position);
            });

            // === NÚT LẶP LẠI ===
            // Cập nhật icon dựa trên trạng thái repeat
            btnRepeat.setImageResource(repeatStates[position] ? R.drawable.ic_repeat_on : R.drawable.ic_repeat);

            btnRepeat.setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition == -1) return;

                // Nếu đang repeat ở vị trí khác, dừng nó trước
                if (currentRepeatPosition != -1 && currentRepeatPosition != currentPosition) {
                    stopRepeat(currentRepeatPosition);
                }

                if (repeatStates[currentPosition]) {
                    // Đang repeat -> tắt repeat
                    stopRepeat(currentPosition);
                } else {
                    // Chưa repeat -> bật repeat
                    repeatStates[currentPosition] = true;
                    currentRepeatPosition = currentPosition;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                    currentStartSec = segment.start;
                    currentEndSec = segment.end;
                    playSegment(currentStartSec, currentEndSec, true, currentPosition);
                }
            });
        }

        // Phương thức cập nhật highlight (dùng cho payload)
        void updateHighlight(boolean isHighlighted) {
            this.isHighlighted = isHighlighted;
            applyHighlightState();
        }

        // Áp dụng trạng thái highlight lên view
        private void applyHighlightState() {
            if (isHighlighted) {
                // Highlight state
                tvTextEn.setTextColor(Color.parseColor("#1976D2")); // Blue text
                tvSpeaker.setTextColor(Color.parseColor("#1976D2")); // Blue speaker
                if (tvTextVi.getVisibility() == View.VISIBLE) {
                    tvTextVi.setTextColor(Color.parseColor("#1976D2")); // Blue translation
                }
            } else {
                // Normal state
                tvTextEn.setTextColor(Color.parseColor("#1A1A1A")); // Dark text
                tvSpeaker.setTextColor(Color.parseColor("#666666")); // Gray speaker
                if (tvTextVi.getVisibility() == View.VISIBLE) {
                    tvTextVi.setTextColor(Color.parseColor("#666666")); // Gray translation
                }
            }
        }

        private void playSegment(double startSec, double endSec, boolean isRepeatMode, int position) {
            this.isRepeatMode = isRepeatMode;

            // Nếu không phải repeat mode, dừng mọi repeat đang chạy
            if (!isRepeatMode && currentRepeatPosition != -1) {
                stopRepeat(currentRepeatPosition);
            }

            releaseSegmentPlayer();

            try {
                segmentPlayer = new MediaPlayer();
                var afd = context.getAssets().openFd(audioFilePath);
                segmentPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();

                segmentPlayer.prepare();
                int startMs = (int) (startSec * 1000);
                int endMs = (int) (endSec * 1000);

                segmentPlayer.seekTo(startMs);

                // Sử dụng OnSeekComplete để đảm bảo seek hoàn thành trước khi play
                segmentPlayer.setOnSeekCompleteListener(mp -> {
                    if (segmentPlayer != null) {
                        segmentPlayer.start();

                        // Kiểm tra và dừng tại endMs
                        itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (segmentPlayer != null && segmentPlayer.isPlaying()) {
                                    int currentPosition = segmentPlayer.getCurrentPosition();
                                    if (currentPosition >= endMs) {
                                        segmentPlayer.pause();

                                        // Xử lý repeat mode
                                        if (isRepeatMode && repeatStates[position]) {
                                            itemView.postDelayed(() -> {
                                                if (repeatStates[position]) {
                                                    playSegment(startSec, endSec, true, position);
                                                }
                                            }, 1000); // Delay 1 giây trước khi lặp lại
                                        }
                                    } else {
                                        // Tiếp tục kiểm tra sau 50ms
                                        itemView.postDelayed(this, 50);
                                    }
                                }
                            }
                        }, 50);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}