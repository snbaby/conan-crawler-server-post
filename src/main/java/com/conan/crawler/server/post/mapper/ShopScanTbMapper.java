package com.conan.crawler.server.post.mapper;

import com.conan.crawler.server.post.entity.ShopScanTb;

public interface ShopScanTbMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_scan_tb
     *
     * @mbg.generated Sun Apr 08 14:32:35 CST 2018
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_scan_tb
     *
     * @mbg.generated Sun Apr 08 14:32:35 CST 2018
     */
    int insert(ShopScanTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_scan_tb
     *
     * @mbg.generated Sun Apr 08 14:32:35 CST 2018
     */
    int insertSelective(ShopScanTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_scan_tb
     *
     * @mbg.generated Sun Apr 08 14:32:35 CST 2018
     */
    ShopScanTb selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_scan_tb
     *
     * @mbg.generated Sun Apr 08 14:32:35 CST 2018
     */
    int updateByPrimaryKeySelective(ShopScanTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop_scan_tb
     *
     * @mbg.generated Sun Apr 08 14:32:35 CST 2018
     */
    int updateByPrimaryKey(ShopScanTb record);
}