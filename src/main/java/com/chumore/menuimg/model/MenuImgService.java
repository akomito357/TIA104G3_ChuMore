package com.chumore.menuimg.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.envimg.model.EnvImgVO;
import com.chumore.rest.model.RestVO;

@Service("menuImgService")
public class MenuImgService {
	@Autowired
	MenuImgRepository repository;

	// 新增環境圖片

	@Transactional
	public List<MenuImgVO> addMenuImgs(MultipartFile[] files, Integer restId) throws IOException {
		if (files == null || restId == null) {
			throw new IllegalArgumentException("Files and restId cannot be null");
		}

		List<MenuImgVO> savedImages = new ArrayList<>();

		RestVO rest = new RestVO();
		rest.setRestId(restId);

		for (MultipartFile file : files) {
			try {
				MenuImgVO menuImg = new MenuImgVO();
				menuImg.setRest(rest);
				menuImg.setImage(file.getBytes());

				MenuImgVO savedImg = repository.save(menuImg);
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
	public void updateMenuImg(MenuImgVO menuImg) {
		repository.save(menuImg);
	}

	// 查詢單一環境圖片
	public MenuImgVO getOneMenuImg(Integer menuImgId) {
		Optional<MenuImgVO> optional = repository.findById(menuImgId);
		return optional.orElse(null);
	}

	// 刪除環境圖片
	public void deleteMenuImg(Integer menuImgId) {
		if (repository.existsById(menuImgId))
			repository.deleteByMenuImgId(menuImgId);
	}

	public List<MenuImgVO> getAllByRestId(Integer restId) {
		return repository.findByRestId(restId);
	}

	// 查詢所有環境圖片
	public List<MenuImgVO> getAll() {
		return repository.findAll();
	}
}
