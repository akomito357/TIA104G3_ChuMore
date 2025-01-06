package com.chumore.menuimg.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.envimg.model.EnvImgVO;

@Service("menuImgService")
public class MenuImgService {
	@Autowired
	MenuImgRepository repository;

	// 新增環境圖片
	
	public void addMenuImg(MenuImgVO menuImg) {
		repository.save(menuImg);
	}

	// 新增多張環境圖片
	public void addMultipleMenuImgs(MenuImgVO menuImg) {
		repository.save(menuImg);
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
