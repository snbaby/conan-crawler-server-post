package com.conan.crawler.server.post.mapper;

import com.conan.crawler.server.post.entity.SellerTb;

public interface SellerTbMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller_tb
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller_tb
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    int insert(SellerTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller_tb
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    int insertSelective(SellerTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller_tb
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    SellerTb selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller_tb
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    int updateByPrimaryKeySelective(SellerTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller_tb
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    int updateByPrimaryKey(SellerTb record);
}