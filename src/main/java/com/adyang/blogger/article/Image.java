package com.adyang.blogger.article;

import com.adyang.blogger.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
public class Image extends BaseEntity {
    private String fileName;
    private String contentType;
    private Long fileSize;

    public Image(String fileName, String contentType, Long fileSize) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
}
