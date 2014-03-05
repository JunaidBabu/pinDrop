package in.junaidbabu.pindrop.app;

/**
 * Created by neo on 5/3/14.
 */
public class DataClass {
    private int id;
    private String title;
    private String location;
    private String url;

    

    public DataClass(){

    }

    public DataClass(String title, String location, String url) {
        super();
        this.title = title;
        this.location = location;
        this.url = url;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Data [id=" + id + ", title=" + title + ", location=" + location
                + "]";
    }

    public String getTitle() {
        return this.title;
    }

    public String getLocation() {
        return this.location;
    }
    public String getUrl() {
        return this.url;
    }


    public void setId(int id) {
        this.id=id;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public void setLocation(String location) {
        this.location=location;
    }
    public void setUrl(String url) {
        this.url=url;
    }

    public int getId() {
        return this.id;
    }
}