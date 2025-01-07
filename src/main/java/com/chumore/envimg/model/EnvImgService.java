package com.chumore.envimg.model;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.rest.model.RestVO;

@Service("envImgService")
public class EnvImgService {
	@Autowired
	EnvImgRepository repository;
	
	
    

	// 新增多張環境圖片
    public List<EnvImgVO> addEnvImgs(MultipartFile[] files, Integer restId) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Files cannot be empty");
        }
        if (restId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        
        List<EnvImgVO> savedImages = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                // 檢查檔案類型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid file type for: " + file.getOriginalFilename());
                }
                RestVO rest = new RestVO();
                rest.setRestId(restId);
                // 建立新的環境圖片物件
                EnvImgVO envImg = new EnvImgVO();
                envImg.setImage(file.getBytes());
                
                // 儲存圖片
                savedImages.add(repository.save(envImg));
                
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image: " + file.getOriginalFilename(), e);
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
