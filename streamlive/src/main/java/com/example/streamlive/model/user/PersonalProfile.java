package com.example.streamlive.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalProfile {
    private Long id;
    private String name;
    private String image;
    private String backgroundImage;
    private String description;
    private Long followers;
    private Long totalViewers;  // 總觀看數
    private Long totalQuantity; // 總銷售數
    private Long totalFigures;  // 總銷售額
    private Long averageScore;  // 直播平均分數
    private Long totalProducts; // 上架商品數
    private Long averageProductScore;  // 平均商品評價
    private Long totalDelegationCount; // 累積代理數
}
