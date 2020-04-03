package cn.henry.study.entity;

import cn.henry.study.anno.FormAttribute;

import java.util.Date;
import java.util.List;

/**
 * description: 流通书籍
 *
 * @author Hlingoes
 * @date 2019/12/22 14:30
 */
public class Book {
    Long id;
    @FormAttribute(label = "书名", required = true)
    String name;
    @FormAttribute(label = "出版商", required = true)
    String pubCompany;
    @FormAttribute(label = "购买人", required = true)
    String author;
    Date pubTime;
    Date buyTime;

    @FormAttribute(label = "借阅人", type = "select", required = true, related = true)
    List<BookBorrower> bookBorrowers;

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

    public String getPubCompany() {
        return pubCompany;
    }

    public void setPubCompany(String pubCompany) {
        this.pubCompany = pubCompany;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public List<BookBorrower> getBookBorrowers() {
        return bookBorrowers;
    }

    public void setBookBorrowers(List<BookBorrower> bookBorrowers) {
        this.bookBorrowers = bookBorrowers;
    }
}
