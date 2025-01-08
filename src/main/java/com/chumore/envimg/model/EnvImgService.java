package com.chumore.envimg.model;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.rest.model.RestVO;

@Service("envImgService")
public class EnvImgService {
	@Autowired
	EnvImgRepository repository;

	@Transactional
	public List<EnvImgVO> addEnvImgs(MultipartFile[] files, Integer restId) throws IOException {
		if (files == null || restId == null) {
			throw new IllegalArgumentException("Files and restId cannot be null");
		}

		List<EnvImgVO> savedImages = new ArrayList<>();

		RestVO rest = new RestVO();
		rest.setRestId(restId);

		for (MultipartFile file : files) {
			try {
				EnvImgVO envImg = new EnvImgVO();
				envImg.setRest(rest);
				envImg.setImage(file.getBytes());

				EnvImgVO savedImg = repository.save(envImg);
				savedImages.add(savedImg);

			} catch (IOException e) {
				// 記錄錯誤並繼續處理其他文件
				System.err.println("Failed to process file: " + file.getOriginalFilename());
				e.printStackTrace();
				throw e; // 或者根據需求決定是否拋出異常
			}
		}

		return savedImages;
	}

	// 修改環境圖片
	public void updateEnvImg(EnvImgVO envImg) {
		repository.save(envImg);
	}

	// 查詢單一環境圖片
	public EnvImgVO getOneEnvImg(Integer envImgId) {
		Optional<EnvImgVO> optional = repository.findById(envImgId);
		return optional.orElse(null);
	}

	// 刪除環境圖片
	public void deleteEnvImg(Integer envImgId) {
		if (repository.existsById(envImgId))
			repository.deleteByEnvImgId(envImgId);
	}

	// 查詢所有環境圖片
	public List<EnvImgVO> getAllByRestId(Integer restId) {
		return repository.findByRestId(restId);
	}

	public List<EnvImgVO> getAll() {
		return repository.findAll();
	}

}
