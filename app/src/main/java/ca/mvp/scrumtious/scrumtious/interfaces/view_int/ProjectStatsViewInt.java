package ca.mvp.scrumtious.scrumtious.interfaces.view_int;


import java.util.ArrayList;
import java.util.Date;

public interface ProjectStatsViewInt extends ListenerInt {
    void populateBurndownChart (ArrayList<Date>dates, ArrayList<Long>points);
}
