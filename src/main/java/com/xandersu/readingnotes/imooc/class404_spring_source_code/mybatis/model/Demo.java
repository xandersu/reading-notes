package com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.model;

import lombok.ToString;

@ToString
public class Demo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demo.id
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demo.name
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demo.job
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    private String job;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demo.id
     *
     * @return the value of demo.id
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demo.id
     *
     * @param id the value for demo.id
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demo.name
     *
     * @return the value of demo.name
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demo.name
     *
     * @param name the value for demo.name
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demo.job
     *
     * @return the value of demo.job
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    public String getJob() {
        return job;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demo.job
     *
     * @param job the value for demo.job
     *
     * @mbg.generated Sun Mar 29 17:56:12 CST 2020
     */
    public void setJob(String job) {
        this.job = job == null ? null : job.trim();
    }
}