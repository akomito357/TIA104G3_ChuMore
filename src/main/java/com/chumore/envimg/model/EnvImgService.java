package com.chumore.envimg.model;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service("envImgService")
public class EnvImgService {
	@Autowired
	EnvImgRepository repository;

	// 新增單一環境圖片
	@Transactional(transactionManager = "jpaTransactionManager")
	public void addEnvImg(EnvImgVO envImg) {
		repository.save(envImg);
	}

	// 新增多張環境圖片
	@Transactional(transactionManager = "jpaTransactionManager")
	public void addMultipleEnvImgs(MultipartFile file, Integer restId) {
		try {
			EnvImgVO envImg = new EnvImgVO();
			envImg.setImage(file.getBytes());
			envImg.getRest().getRestId();
			repository.save(envImg);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save menu image", e);
		}
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
	public List<EnvImgVO> getAll() {
		return repository.findAll();
	}
}
