package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

import java.util.ArrayList;

public interface ProjectStatsViewInt extends ListenerInt {
    void populateBurndownChart (ArrayList<Long>daysFromStart, ArrayList<Long>costs);
    void populateNumMembers(long numMembers);
    void populateNumSprints(long numSprints);
    void populateNumUserStories(long total, long completed);
    void populateProjectCreationDate(String date);
}
