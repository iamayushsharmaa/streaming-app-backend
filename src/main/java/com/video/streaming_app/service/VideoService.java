package com.video.streaming_app.service;

import com.video.streaming_app.entities.Video;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface VideoService {

    Video save(Video video, MultipartFile file);

    Video get(String videoId);

    Video getByTitle(String title);

    List<Video> getAll();

    String processVideo(String videoId);
}
