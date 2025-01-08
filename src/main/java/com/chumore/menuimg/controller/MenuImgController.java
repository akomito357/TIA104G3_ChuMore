package com.chumore.menuimg.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.menuimg.model.MenuImgService;
import com.chumore.menuimg.model.MenuImgVO;

@CrossOrigin
@Controller
@RequestMapping("/menuImg")
@MultipartConfig
public class MenuImgController {

	@Autowired
	MenuImgService menuImgSvc;

	@GetMapping("/images/{restId}")
	@ResponseBody
	public ResponseEntity<List<Integer>> getImageIds(@PathVariable Integer restId) {
		List<MenuImgVO> images = menuImgSvc.getAllByRestId(restId);
		List<Integer> imageIds = images.stream().map(img -> img.getMenuImgId()).collect(Collectors.toList());
		return ResponseEntity.ok(imageIds);
	}

	@GetMapping("/image/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		MenuImgVO image = menuImgSvc.getOneMenuImg(id);
		if (image != null && image.getImage() != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image.getImage());
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/deleteMenuImage/{imgId}")
	@ResponseBody
	public ResponseEntity<?> deleteMenuImage(@PathVariable Integer imgId) {
		try {
			menuImgSvc.deleteMenuImg(imgId);
			return ResponseEntity.ok(Map.of("success", true));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "Failed to delete Menu image"));
		}
	}

	@PostMapping("/addMenuImg")
	@ResponseBody
	public ResponseEntity<?> addMenuImage(@RequestParam("upFiles") MultipartFile[] files,
			@RequestParam Integer restId) {
		try {
			// 基本驗證
			if (files == null || files.length == 0) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No files were uploaded"));
			}

			// 檢查每個文件
			for (MultipartFile file : files) {
				// 檢查文件類型
				if (!file.getContentType().startsWith("image/")) {
					return ResponseEntity.badRequest().body(
							Map.of("success", false, "message", "Invalid file type: " + file.getOriginalFilename()));
				}

			}
			// 儲存圖片
			List<MenuImgVO> savedImages = menuImgSvc.addMenuImgs(files, restId);

			return ResponseEntity.ok(Map.of("success", true, "message", "Images uploaded successfully", "imageCount",
					savedImages.size()));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "Failed to upload images: " + e.getMessage()));
		}
	}

}
