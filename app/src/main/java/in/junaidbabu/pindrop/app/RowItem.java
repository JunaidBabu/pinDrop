package in.junaidbabu.pindrop.app;

/**
 * Created by neo on 6/3/14.
 */
public class RowItem {
    private String imageId;
    private String title;
    private String desc;
    private String url;

    public RowItem(String imageId, String title, String desc, String url) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.url = url;
    }

    public String getImageId() {
        return imageId;
    }
    public String getUrl(){
        return url;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}