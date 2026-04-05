package com.techlabs.app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.techlabs.app.entity.FileItem;
import com.techlabs.app.exception.FileRelatedException;
import com.techlabs.app.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private Cloudinary cloudinary;
    @Mock
    private Uploader uploader;
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void saveFile_rejectsDisallowedExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream", "content".getBytes());

        assertThrows(FileRelatedException.class, () -> fileService.saveFileAndReturnItem(file));
    }

    @Test
    void saveFile_rejectsNullFilename() {
        MockMultipartFile file = new MockMultipartFile(
                "file", null, "image/png", "content".getBytes());

        assertThrows(FileRelatedException.class, () -> fileService.saveFileAndReturnItem(file));
    }

    @Test
    void saveFile_rejectsFileWithoutExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "noextension", "image/png", "content".getBytes());

        assertThrows(FileRelatedException.class, () -> fileService.saveFileAndReturnItem(file));
    }

    @Test
    void saveFile_acceptsAllowedExtensions() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any())).thenReturn(
                Map.of("secure_url", "https://res.cloudinary.com/test/image/upload/fortunelife/abc123.pdf"));
        when(fileRepository.save(any(FileItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "pdf content".getBytes());
        FileItem result = fileService.saveFileAndReturnItem(pdfFile);

        assertNotNull(result);
        assertEquals("application/pdf", result.getType());
        assertTrue(result.getLocation().contains("cloudinary.com"));
    }

    @Test
    void saveFile_acceptsJpgFiles() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any())).thenReturn(
                Map.of("secure_url", "https://res.cloudinary.com/test/image/upload/fortunelife/img.jpg"));
        when(fileRepository.save(any(FileItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile jpgFile = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "jpg content".getBytes());
        FileItem result = fileService.saveFileAndReturnItem(jpgFile);

        assertNotNull(result);
        assertEquals("image/jpeg", result.getType());
    }

    @Test
    void getFileByUUIDName_throwsWhenNotFound() {
        when(fileRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        assertThrows(FileRelatedException.class, () -> fileService.getFileByUUIDName("nonexistent"));
    }

    @Test
    void getFileByUUIDName_returnsFileWhenFound() {
        FileItem fileItem = FileItem.builder()
                .name("abc123")
                .type("image/png")
                .location("https://res.cloudinary.com/test/abc123.png")
                .build();
        when(fileRepository.findByName("abc123")).thenReturn(Optional.of(fileItem));

        FileItem result = fileService.getFileByUUIDName("abc123");

        assertEquals("abc123", result.getName());
        assertEquals("image/png", result.getType());
    }
}
