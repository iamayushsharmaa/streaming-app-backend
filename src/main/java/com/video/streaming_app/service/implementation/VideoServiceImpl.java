package com.video.streaming_app.service.implementation;

import com.video.streaming_app.entities.Video;
import com.video.streaming_app.repository.VideoRepository;
import com.video.streaming_app.service.VideoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    @Value("${files.video}")
    String DIR;

    @Value("${files.video.hsl}")
    String HSL_DIR;

    @PostConstruct
    public void init() {
        File file = new File(DIR);
        File file1 = new File(HSL_DIR);

        if (!file1.exists()) {
            file1.mkdir();
        }

        if (!file.exists()) {
            file.mkdir();
            System.out.println("Folder Created:");
        } else {
            System.out.println("Folder already created");
        }
    }

    @Override
    public Video save(Video video, MultipartFile file) {

        try {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();


            String cleanFileName = StringUtils.cleanPath(fileName);
            String cleanFolder = StringUtils.cleanPath(DIR);

            Path path = Paths.get(cleanFolder, cleanFileName);

            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            video.setContentType(contentType);
            video.setFilePath(path.toString());

            processVideo(video.getVideoId());

            return videoRepository.save(video);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Video get(String videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));

        return video;
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    @Override
    public String processVideo(String videoId) {
        Video video = this.get(videoId);
        String filePath= video.getFilePath();
        Path videoPath = Paths.get(filePath);

        String output360p = HSL_DIR+ videoId + "/360p/";
        String output720p = HSL_DIR+ videoId + "/720p/";
        String output1080p = HSL_DIR+ videoId + "/1080p/";

        try {
            Files.createDirectories(Paths.get(output360p));
            Files.createDirectories(Paths.get(output720p));
            Files.createDirectories(Paths.get(output1080p));

             StringBuilder ffmpegCmd = new StringBuilder();
             ffmpegCmd.append("ffmpeg -i")
                     .append(videoPath.toString())
                     .append(" ")
                     .append("-map 0: v -map 0:a -s:v:0 640*360 -b:v:0 800k ")
                     .append("-map 0: v -map 0:a -s:v:0 1280*720 -b:v:0 2800k ")
                     .append("-map 0: v -map 0:a -s:v:0 1920*1080 -b:v:0 5000k ")
                     .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\"")
                     .append("-master_pl_name ").append(HSL_DIR).append(videoId).append("/master.m3u8 ")
                     .append("-f hls -hls_time 10 -hls_list_size 0 ")
                     .append("-hls_segment_filename \"").append(HSL_DIR).append(videoId)
                     .append(" ").append(HSL_DIR).append(videoId);

        } catch (IOException e) {
            throw new RuntimeException("Video processing fail !!");
        }

        return "";
    }
}
