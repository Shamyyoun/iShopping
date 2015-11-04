package datamodels;

import java.io.Serializable;

/**
 * Created by Shamyyoun on 7/16/2015.
 */
public class Category implements Serializable {
    private int id;
    private String title;
    private String image;

    public Category(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
