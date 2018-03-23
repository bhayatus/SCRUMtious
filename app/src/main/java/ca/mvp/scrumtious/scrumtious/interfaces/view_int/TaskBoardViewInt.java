package ca.mvp.scrumtious.scrumtious.interfaces.view_int;

import android.view.View;

public interface TaskBoardViewInt {
    void showMessage(String message, boolean showAsToast);
    void setEmptyStateView();
    void onClickDeleteTask(final String tid);
    void onClickSwitchTask(View view, final String tid);
    void onLongClickTask(String tid);
}
