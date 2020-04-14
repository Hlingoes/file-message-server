package cn.henry.study.entity;

import java.util.Date;

/**
 * description: 借阅或所属人
 *
 * @author Hlingoes
 * @date 2019/12/22 14:34
 */
public class BookBorrower {
    private Long id;
    private String name;
    private Long number;
    private int sexual;
    private Date fetchTime;
    private Date backTime;
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public int getSexual() {
        return sexual;
    }

    public void setSexual(int sexual) {
        this.sexual = sexual;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
