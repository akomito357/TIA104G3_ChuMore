package com.chumore.reviewimg.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.chumore.review.model.ReviewVO;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "review_image")
public class ReviewImageVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_img_id")
    private Integer reviewImgId;

    @NotNull(message = "圖片不能為空")
    @Lob
    @Column(name = "review_image", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] reviewImage;

    @Column(name = "upload_datetime", nullable = false)
    private Timestamp uploadDatetime;

    @ManyToOne
    @JoinColumn(name = "review_id")
    @JsonBackReference("review-reviewImg")
    private ReviewVO review;

    // Getters and Setters
    public Integer getReviewImgId() {
        return reviewImgId;
    }

    public void setReviewImgId(Integer reviewImgId) {
        this.reviewImgId = reviewImgId;
    }

    public byte[] getReviewImage() {
        return reviewImage;
    }

    public void setReviewImage(byte[] reviewImage) {
        this.reviewImage = reviewImage;
    }

    public Timestamp getUploadDatetime() {
        return uploadDatetime;
    }

    public void setUploadDatetime(Timestamp uploadDatetime) {
        this.uploadDatetime = uploadDatetime;
    }

    public ReviewVO getReview() {
        return review;
    }

    public void setReview(ReviewVO review) {
        this.review = review;
    }
}