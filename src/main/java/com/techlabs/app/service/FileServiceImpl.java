package com.techlabs.app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.techlabs.app.entity.FileItem;
import com.techlabs.app.exception.FileRelatedException;
import com.techlabs.app.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
			"pdf", "jpg", "jpeg", "png", "gif", "doc", "docx", "xls", "xlsx");

	@Autowired
	private Cloudinary cloudinary;

	@Autowired
	private FileRepository fileRepository;

	@Override
	public FileItem saveFileAndReturnItem(MultipartFile file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || !originalFilename.contains(".")) {
			throw new FileRelatedException("Invalid file name");
		}

		String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new FileRelatedException(
					"File type '" + extension + "' is not allowed. Allowed types: " + ALLOWED_EXTENSIONS);
		}

		String publicId = "fortunelife/" + UUID.randomUUID();

		Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
				ObjectUtils.asMap(
						"public_id", publicId,
						"resource_type", "auto"));

		String secureUrl = (String) uploadResult.get("secure_url");
		String name = publicId.substring(publicId.lastIndexOf("/") + 1);

		FileItem fileItem = FileItem.builder()
				.name(name)
				.type(file.getContentType())
				.location(secureUrl)
				.build();

		logger.info("File uploaded to Cloudinary: {}", secureUrl);
		return fileRepository.save(fileItem);
	}

	@Override
	public FileItem getFileByUUIDName(String name) {
		return fileRepository.findByName(name)
				.orElseThrow(() -> new FileRelatedException("File with UUID name: " + name + " not found"));
	}
}
