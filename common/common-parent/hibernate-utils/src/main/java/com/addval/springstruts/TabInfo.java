package com.addval.springstruts;

import java.io.Serializable;

public class TabInfo implements Serializable {
    private String name = null;
    private String label = null;
    private String link = null;
    private int position = -1;

    private TabInfo(){

    }

    public TabInfo(String name,String label,String link){
        setName( name );
        setLabel( label );
        setLink(link );
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
