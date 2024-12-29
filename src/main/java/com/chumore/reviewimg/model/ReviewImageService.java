package com.chumore.reviewimg.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.review.model.ReviewRepository;

@Service
public class ReviewImageService {

    @Autowired
    private ReviewImageRepository reviewImageRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;

    private static final int MAX_IMAGE_COUNT = 5;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Transactional
    public void uploadImage(ReviewImageVO reviewImage) {
    	reviewImageRepository.save(reviewImage);
    	
//        ReviewVO review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new RuntimeException("評論不存在"));

//        if (!review.getMember().equals(memberId)) {
//            throw new RuntimeException("無權限上傳圖片至此評論");
//        }

//        validateImageUpload(file, reviewId);
//        try {
//            ReviewImageVO reviewImage = new ReviewImageVO();
//            reviewImage.setReview(review);
//            reviewImage.setReviewImage(compressImage(file.getBytes()));
////            reviewImage.setUploadDatetime(new Timestamp(System.currentTimeMillis()));
//
//            return reviewImageRepository.save(reviewImage);
//        } catch (IOException e) {
//            throw new RuntimeException("圖片處理失敗: " + e.getMessage());
//        }
    }
    
    
    public void addImage(ReviewImageVO reviewImage) {
    	reviewImageRepository.save(reviewImage);
    }

    @Transactional
    public void deleteImage(Integer imageId, Integer memberId) {
    	if (reviewImageRepository.existsById(imageId)) {
    		reviewImageRepository.deleteByReviewIdNative(imageId);
    	}
    	
//        Optional<ReviewImageVO> imageOpt = reviewImageRepository.findById(imageId);
//        if (imageOpt.isEmpty()) {
//            throw new RuntimeException("圖片不存在");
//        }
//        ReviewImageVO image = imageOpt.get();
//        if (!image.getReview().getMember().equals(memberId)) {
//            throw new RuntimeException("無權限刪除此圖片");
//        }
//        reviewImageRepository.deleteById(imageId);
    }

    public List<ReviewImageVO> getReviewImages(Integer reviewId) {
        return reviewImageRepository.findByReviewOrderByUploadDatetimeDesc(reviewId);
    }
    
    public ReviewImageVO getOneReviewImageById(Integer reviewImagesId) {
    	Optional<ReviewImageVO> optional = reviewImageRepository.findById(reviewImagesId);
    	return optional.orElse(null);
    }

    @Transactional
    public void deleteAllByReviewId(Integer reviewId) {
//        reviewImageRepository.deleteByReview_ReviewId(reviewId);
    	reviewImageRepository.deleteByReviewIdNative(reviewId);
    }

    private void validateImageUpload(MultipartFile file, Integer reviewId) {
        if (file.isEmpty()) {
            throw new RuntimeException("請選擇要上傳的圖片");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("圖片大小不得超過5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上傳圖片檔案");
        }

        long currentImageCount = reviewImageRepository.countByReviewId(reviewId);
        if (currentImageCount >= MAX_IMAGE_COUNT) {
            throw new RuntimeException("每則評論最多上傳" + MAX_IMAGE_COUNT + "張圖片");
        }
    }

    private byte[] compressImage(byte[] imageData) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        BufferedImage originalImage = ImageIO.read(bis);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        // 將圖片轉換為JPEG格式並壓縮
        // 可以根據需求調整壓縮品質
        ImageIO.write(originalImage, "JPEG", bos);
        
        return bos.toByteArray();
    }

//    public boolean isImageOwner(Integer imageId, Integer memberId) {
//        Optional<ReviewImageVO> imageOpt = reviewImageRepository.findById(imageId);
//        return imageOpt.map(image -> 
//            image.getReview().getMember().getMamberId().equals(memberId)
//        ).orElse(false);
//    }
}