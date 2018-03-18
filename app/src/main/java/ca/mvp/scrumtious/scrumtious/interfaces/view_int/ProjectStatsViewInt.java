package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


import java.util.ArrayList;
import java.util.Date;

public interface ProjectStatsViewInt extends ListenerInt {
    void populateBurndownChart (ArrayList<String>dates, ArrayList<Long>points);
}
