package com.chumore.envimg.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service("envImgService")
public class EnvImgService {
	@Autowired
	EnvImgRepository repository;

	// 新增單一環境圖片
	public void addEnvImg(EnvImgVO envImgVO) {
		repository.save(envImgVO);
	}

	// 新增多張環境圖片
	public void addMultipleEnvImgs(List<EnvImgVO> envImgVOs) {
		if (envImgVOs != null && !envImgVOs.isEmpty()) {
			repository.saveAll(envImgVOs); // 使用 JPA 的批量儲存方法
		}
	}

	// 修改環境圖片
	public void updateEnvImg(EnvImgVO envImgVO) {
		repository.save(envImgVO);
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
