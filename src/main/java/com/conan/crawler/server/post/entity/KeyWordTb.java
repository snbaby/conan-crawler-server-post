package com.conan.crawler.server.post.entity;

import java.util.Date;

public class KeyWordTb {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column key_word_tb.id
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column key_word_tb.key_word
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    private String keyWord;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column key_word_tb.crt_user
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    private String crtUser;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column key_word_tb.crt_time
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    private Date crtTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column key_word_tb.status
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column key_word_tb.crt_ip
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    private String crtIp;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column key_word_tb.id
     *
     * @return the value of key_word_tb.id
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column key_word_tb.id
     *
     * @param id the value for key_word_tb.id
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column key_word_tb.key_word
     *
     * @return the value of key_word_tb.key_word
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public String getKeyWord() {
        return keyWord;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column key_word_tb.key_word
     *
     * @param keyWord the value for key_word_tb.key_word
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord == null ? null : keyWord.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column key_word_tb.crt_user
     *
     * @return the value of key_word_tb.crt_user
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public String getCrtUser() {
        return crtUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column key_word_tb.crt_user
     *
     * @param crtUser the value for key_word_tb.crt_user
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser == null ? null : crtUser.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column key_word_tb.crt_time
     *
     * @return the value of key_word_tb.crt_time
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column key_word_tb.crt_time
     *
     * @param crtTime the value for key_word_tb.crt_time
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column key_word_tb.status
     *
     * @return the value of key_word_tb.status
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column key_word_tb.status
     *
     * @param status the value for key_word_tb.status
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column key_word_tb.crt_ip
     *
     * @return the value of key_word_tb.crt_ip
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public String getCrtIp() {
        return crtIp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column key_word_tb.crt_ip
     *
     * @param crtIp the value for key_word_tb.crt_ip
     *
     * @mbg.generated Sun Apr 08 13:12:42 CST 2018
     */
    public void setCrtIp(String crtIp) {
        this.crtIp = crtIp == null ? null : crtIp.trim();
    }
}