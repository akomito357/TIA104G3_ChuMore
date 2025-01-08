package com.chumore.envimg.controller;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.chumore.envimg.model.EnvImgService;
import com.chumore.envimg.model.EnvImgVO;


@CrossOrigin
@Controller
@RequestMapping("/envImg")
@MultipartConfig
public class EnvImgController {
	
	@Autowired
	EnvImgService envImgSvc;
	
	@GetMapping("/images/{restId}")
	@ResponseBody
	public ResponseEntity<List<Integer>> getImageIds(@PathVariable Integer restId) {
	    List<EnvImgVO> images = envImgSvc.getAllByRestId(restId);
	    List<Integer> imageIds = images.stream()
	        .map(img -> img.getEnvImgId())
	        .collect(Collectors.toList());
	    return ResponseEntity.ok(imageIds);
	}

	@GetMapping("/image/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
	    EnvImgVO image = envImgSvc.getOneEnvImg(id);
	    if (image != null && image.getImage() != null) {
	        return ResponseEntity.ok()
	        	.contentType(MediaType.IMAGE_JPEG)
	            .body(image.getImage());
	    }
	    return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/deleteEnvImage/{imgId}")
	@ResponseBody
	public ResponseEntity<?> deleteEnvImage(@PathVariable Integer imgId) {
	    try {
	        envImgSvc.deleteEnvImg(imgId);
	        return ResponseEntity.ok(Map.of("success", true));
	    } catch (Exception e) {
	        return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of(
	                "success", false,
	                "message", "Failed to delete environment image"
	            ));
	    }
	}

	@PostMapping("/addEnvImg")
	@ResponseBody
	public ResponseEntity<?> addEnvImage(
	    @RequestParam("upFiles") MultipartFile[] files,
	    @RequestParam Integer restId
	) {
	    try {
	        List<EnvImgVO> savedImages = envImgSvc.addEnvImgs(files, restId);
	        return ResponseEntity.ok(Map.of(
	            "success", true,
	            "message", "Images uploaded successfully"
	        ));
	    } catch (Exception e) {
	        return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of(
	                "success", false,
	                "message", "Failed to upload images"
	            ));
	    }
	}

}
