package com.video.streaming_app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "yt_videos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    private String videoId;
    private String title;
    private String description;
    private String contentType;
    private String filePath;
}
