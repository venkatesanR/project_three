package com.addval.springstruts;

import com.addval.utils.StrUtl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class TabDetails implements Serializable {
    private HashMap tabs = null;
    private List tabNames = null;

    public TabDetails() {
        tabs = new HashMap();
        tabNames = new ArrayList();
    }

    public void add (TabInfo tabinfo){
        if( tabinfo != null && !StrUtl.isEmptyTrimmed( tabinfo.getName() ) ) {
            if( !tabs.containsKey( tabinfo.getName() ) ){
                tabs.put(tabinfo.getName(),tabinfo);
                int position = tabs.size() - 1 ;
                tabinfo.setPosition( position );
                tabNames.add( tabinfo.getName() );
            }
        }
    }

    public List getTabNames(){
        return tabNames;
    }

    public TabInfo getTabInfo(String tabName){
        if( tabs.containsKey( tabName) ){
            return (TabInfo) tabs.get( tabName );
        }
        return null;
    }

    public int getTabCount() {
        return tabs.size();
    }

    public String getTabName(int position){
        String tabName = null;
        TabInfo tabInfo = null;
        for (Iterator iterator = tabs.keySet().iterator(); iterator.hasNext();) {
            tabName = (String) iterator.next();
            tabInfo =  (TabInfo) tabs.get( tabName );
            if( tabInfo.getPosition() == position ){
                return tabName;
            }
        }
        return null;
    }




}
