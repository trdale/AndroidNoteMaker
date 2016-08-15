package tomdale.androidnotemaker;

import android.app.Application;

public class Globals extends Application {
    private String data = null;
    private String selected = null;
    private String selectedTitle = null;
    private String selectedContext = null;

    public String getData(){
        return this.data;
    }
    public String getSelected() { return this.selected; }
    public String getSelectedTitle() { return this.selectedTitle; }
    public String getSelectedContext() { return this.selectedContext; }

    public void setData(String d){
        this.data=d;
    }
    public void setSelected (String d){
        this.selected=d;
    }
    public void setSelectedTitle (String d){
        this.selectedTitle=d;
    }
    public void setSelectedContext (String d){
        this.selectedContext=d;
    }
}