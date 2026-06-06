package com.example.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {

    private String title;
    private String content;
    private Date timestamp;
    private String type; // SUCCESS, ERROR, WARNING, INFO
    private Integer resourceId;

}
